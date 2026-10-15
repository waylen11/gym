package com.hyw.gym.service;
// @author 黄元威

import com.hyw.gym.common.Result;
import com.hyw.gym.common.UserHolder;
import com.hyw.gym.dao.PrivateCoachInfoDao;
import com.hyw.gym.dto.MyPrivateCoachDto;
import com.hyw.gym.entity.PrivateCoachInfo;
import com.hyw.gym.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.ws.Holder;
import java.util.ArrayList;
import java.util.List;

@Service
public class MemberPrivateCourseDaoImpl {
    @Autowired
    private PrivateCoachInfoDao privateCoachInfoDao;

    public Result<List<MyPrivateCoachDto>> showMyPrivateCoach() {
        User Currentuser = UserHolder.get();
        if (Currentuser==null){
            return Result.error(401, "未登录");
        }
        List<PrivateCoachInfo> privateCoachInfos = privateCoachInfoDao.queryByIdNative(Currentuser.getId());
        List<MyPrivateCoachDto> myPrivateCoachDtos = new ArrayList<>();
        for (PrivateCoachInfo p:privateCoachInfos){
            MyPrivateCoachDto myPrivateCoachDto = new MyPrivateCoachDto();
            myPrivateCoachDto.setRecordId(p.getId());
            myPrivateCoachDto.setCount(p.getCount());
            myPrivateCoachDto.setRealprice(p.getRealprice());
            myPrivateCoachDto.setCourseDate(p.getCourseDate() == null ? null : p.getCourseDate().toString());
            myPrivateCoachDto.setStatus(p.getStatus());
            myPrivateCoachDto.setRemark(p.getRemark());

            if (p.getSubject() != null) {
                MyPrivateCoachDto.SubjectItem si = new MyPrivateCoachDto.SubjectItem();
                si.setId(p.getSubject().getId());
                si.setCourseName(p.getSubject().getCourseName());
                si.setSellingPrice(p.getSubject().getSellingPrice());
                myPrivateCoachDto.setSubject(si);
            }
            if (p.getCoach() != null) {
                MyPrivateCoachDto.CoachItem ci = new MyPrivateCoachDto.CoachItem();
                ci.setId(p.getCoach().getId());
                ci.setRealName(p.getCoach().getRealName());
                myPrivateCoachDto.setCoach(ci);
            }
            myPrivateCoachDtos.add(myPrivateCoachDto);
        }

        return Result.success(myPrivateCoachDtos);
    }
}
