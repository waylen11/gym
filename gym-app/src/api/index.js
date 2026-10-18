import request from './request'

// ===== 登录模块 =====
// 发送验证码：POST /api/member/code?phone=
export const sendCode = (phone) => request.post('/member/code', null, { params: { phone } })

// 登录：POST /api/member/login?phone=&code= → data 就是 token 字符串
export const login = (phone, code) => request.post('/member/login', null, { params: { phone, code } })

// 退出登录：POST /api/member/logout
export const logout = () => request.post('/member/logout')

// ===== 个人信息 =====
export const getMe = () => request.get('/member/me')
// 更新个人资料：PUT /api/member/update  body: {realName, sex, age, phone}
export const updateMe = (data) => request.put('/member/update', data)

// ===== 会员卡 =====
// 卡信息 + 续费记录
export const getCardInfo = () => request.get('/member/cardInfo')
// 卡类型列表
export const getCardTypes = () => request.get('/member/cardType')

// ===== 订单（模拟支付）=====
// 创建订单：POST /api/member/order/create?typeId= → {orderId, amount, ...}
export const createOrder = (typeId) => request.post('/member/order/create', null, { params: { typeId } })
// 模拟支付：POST /api/member/order/pay/{orderId}
export const payOrder = (orderId) => request.post(`/member/order/pay/${orderId}`)
// 我的订单
export const getOrders = () => request.get('/member/orders')

// ===== 商品 / 积分 =====
export const getGoods = () => request.get('/member/goods')
// 积分兑换：POST /api/member/exchange/{goodsId} → {goodsName, pointsCost, remainPoints}
export const exchangeGoods = (goodsId) => request.post(`/member/exchange/${goodsId}`)
export const getExchanges = () => request.get('/member/exchanges')
export const getConsumptions = () => request.get('/member/consumptions')

// ===== 签到 =====
export const getSignStatus = () => request.get('/member/sign/status')
// 签到：POST /api/member/sign?latitude=&longitude=
export const doSign = (latitude, longitude) =>
  request.post('/member/sign', null, { params: { latitude, longitude } })
export const getSignRank = () => request.get('/member/sign/rank')

// ===== 课程 / 教练 =====
export const getCourses = () => request.get('/member/courses')
export const getCoachList = () => request.get('/member/coachlist')
export const getCoachDetail = (id) => request.get(`/member/coach/${id}`)
export const getMyPrivate = () => request.get('/member/privateCourses')

// ===== 预约 =====
// 创建预约：POST /api/member/appointment  body: {recordId, appointmentDate, remark}
export const createAppointment = (data) => request.post('/member/appointment', data)
// 我的预约：GET /api/member/appointments?status=（0已预约 1已打卡 2已取消，不传=全部）
export const getAppointments = (status) => request.get('/member/appointments', { params: { status } })
// 取消预约：DELETE /api/member/appointment/{id}
export const cancelAppointment = (id) => request.delete(`/member/appointment/${id}`)

// ===== 公告 / 失物招领 =====
export const getNotices = () => request.get('/member/notices')
// status 可选：0 未认领 / 1 已认领 / 不传=全部
export const getLosts = (status) => request.get('/member/losts', { params: { status } })
