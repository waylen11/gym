package com.hyw.gym.dto;

import lombok.Data;

import java.util.List;

@Data
public class CardInfoVO {
    private Integer remainingDays;
    private String memberXufei;
    private Integer status;
    private List<RenewRecordItem> renewRecords;

    /** 单条续费记录 */
    @Data
    public static class RenewRecordItem {
        private Long rechargeId;
        private Long money;
        private String rechargeTime;
        private MemberTypeItem membertype;
        private String remark;
    }

    /** 卡类型（只挑前端要的字段） */
    @Data
    public static class MemberTypeItem {
        private Long id;
        private String typeName;
    }
}
