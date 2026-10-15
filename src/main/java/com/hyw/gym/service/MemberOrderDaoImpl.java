package com.hyw.gym.service;
// @author 黄元威

import cn.hutool.core.util.RandomUtil;
import com.hyw.gym.common.Result;
import com.hyw.gym.common.UserHolder;
import com.hyw.gym.dao.MemberTypeDao;
import com.hyw.gym.dao.PayOrderDao;
import com.hyw.gym.dto.OrderDTO;
import com.hyw.gym.entity.Membertype;
import com.hyw.gym.entity.PayOrder;
import com.hyw.gym.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 会员端-模拟支付
 * 企业级闭环：创建订单（待支付）→ 模拟支付回调（复用续卡逻辑）→ 订单置已支付
 * 真实场景：微信/支付宝回调后端接口；演示环境：直接调用 pay 接口模拟回调
 */
@Service
public class MemberOrderDaoImpl {

    @Autowired
    private PayOrderDao payOrderDao;
    @Autowired
    private MemberTypeDao memberttypeDao;
    @Autowired
    private MemberCardDaoImpl memberCardDao;

    private static final DateTimeFormatter ORDER_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
    private static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    /** 待支付订单上限（防刷单堆积） */
    private static final int MAX_PENDING = 5;
    /** 下单后超过该时长未支付，订单自动失效（懒过期：支付时校验，无需定时任务） */
    private static final long TIMEOUT_MS = 30 * 60 * 1000L;

    /**
     * 创建订单（预支付）：只记录"要买什么、多少钱"，不立即续卡
     */
    public Result<OrderDTO> createOrder(Long typeId) {
        // 1. 登录校验
        User current = UserHolder.get();
        if (current == null) return Result.error(401, "未登录");
        if (typeId == null) return Result.error(400, "卡类型ID不能为空");

        // 2. 校验卡类型存在，金额取卡类型价格
        Membertype mt = memberttypeDao.findById(typeId).orElse(null);
        if (mt == null) return Result.error(400, "会员卡类型不存在");

        // 3. 防重复下单：同一卡类型只能有一笔待支付订单（先支付/取消才能再下同类型的）
        List<PayOrder> pendingSameType = payOrderDao.queryPendingByMemberAndType(current.getId(), typeId);
        if (!pendingSameType.isEmpty()) {
            return Result.error(400, "已有该卡类型的待支付订单，请先支付或取消");
        }

        // 4. 防堆积：待支付订单总量上限
        if (payOrderDao.countPendingByMember(current.getId()) >= MAX_PENDING) {
            return Result.error(400, "待支付订单过多（上限 " + MAX_PENDING + " 笔），请先处理已有订单");
        }

        // 5. 生成订单（订单号 = 时间戳 + 6 位随机数，唯一约束兜底）
        PayOrder order = new PayOrder();
        order.setOrderNo(ORDER_FMT.format(LocalDateTime.now()) + String.format("%06d", RandomUtil.randomInt(1000000)));
        order.setMember(current);
        order.setMembertype(mt);
        order.setAmount((double) mt.getPrice());
        order.setStatus(0);   // 待支付
        order.setCreateTime(new Timestamp(System.currentTimeMillis()));
        payOrderDao.save(order);

        return Result.success(toDTO(order));
    }

    /**
     * 模拟支付：模拟第三方支付平台"支付成功回调"
     * 真实场景是平台调我们；此处直接调本接口模拟。校验通过后复用续卡逻辑。
     */
    @Transactional(rollbackFor = Exception.class)
    public Result<Map<String, Object>> pay(Long orderId) {
        // 1. 登录校验 + 订单归属校验（防越权支付别人的订单）
        User current = UserHolder.get();
        if (current == null) return Result.error(401, "未登录");
        PayOrder order = payOrderDao.findById(orderId).orElse(null);
        if (order == null || order.getMember() == null
                || !order.getMember().getId().equals(current.getId())) {
            return Result.error(400, "订单不存在");
        }

        // 2. 防重复支付：只有"待支付"状态能支付
        if (order.getStatus() != 0) {
            return Result.error(400, order.getStatus() == 1 ? "订单已支付，请勿重复支付" : "订单已取消");
        }

        // 3. 超时未支付：下单超过 30 分钟自动失效（懒过期，真实系统用定时任务关单，此处支付时校验）
        if (order.getCreateTime() != null
                && System.currentTimeMillis() - order.getCreateTime().getTime() > TIMEOUT_MS) {
            return Result.error(400, "订单已超时（30 分钟）未支付，请重新下单");
        }

        // 4. 模拟支付成功回调 → 复用完整续卡逻辑（锁 + 充值记录 + 加天数 + 删缓存）
        Result<Map<String, Object>> renewResult = memberCardDao.renew(order.getMembertype().getId());
        if (renewResult.getCode() != 200) {
            return renewResult;   // 续卡失败则不标记已支付，订单保持待支付
        }

        // 4. 订单置已支付 + 记录支付时间
        order.setStatus(1);
        order.setPayTime(new Timestamp(System.currentTimeMillis()));
        payOrderDao.save(order);

        // 5. 返回续卡结果 + 订单信息
        Map<String, Object> data = renewResult.getData();
        data.put("orderNo", order.getOrderNo());
        data.put("amount", order.getAmount());
        data.put("payTime", SDF.format(order.getPayTime()));
        return Result.success(data);
    }

    /**
     * 我的订单列表（按下单时间倒序）
     */
    public Result<List<OrderDTO>> myOrders() {
        User current = UserHolder.get();
        if (current == null) return Result.error(401, "未登录");
        List<PayOrder> list = payOrderDao.queryByMemberNative(current.getId());
        List<OrderDTO> vos = new ArrayList<>();
        for (PayOrder o : list) {
            vos.add(toDTO(o));
        }
        return Result.success(vos);
    }

    private OrderDTO toDTO(PayOrder o) {
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(o.getId());
        dto.setOrderNo(o.getOrderNo());
        dto.setAmount(o.getAmount());
        dto.setStatus(o.getStatus());
        dto.setStatusText(o.getStatus() == 0 ? "待支付" : (o.getStatus() == 1 ? "已支付" : "已取消"));
        if (o.getMembertype() != null) {
            dto.setTypeName(o.getMembertype().getTypeName());
        }
        dto.setCreateTime(o.getCreateTime() == null ? null : SDF.format(o.getCreateTime()));
        dto.setPayTime(o.getPayTime() == null ? null : SDF.format(o.getPayTime()));
        return dto;
    }
}
