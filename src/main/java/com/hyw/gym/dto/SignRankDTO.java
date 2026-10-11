package com.hyw.gym.dto;

import lombok.Data;

import java.util.List;

/**
 * 签到排行榜 DTO：本月 Top 榜单 + 我的排名
 */
@Data
public class SignRankDTO {
    private List<RankItem> rankList;   // Top 榜单
    private RankItem myRank;           // 我的排名（未上榜则为 null）

    @Data
    public static class RankItem {
        private Integer rank;          // 名次
        private Long memberId;         // 会员 id
        private String realName;       // 会员姓名
        private Integer count;         // 本月签到次数
    }
}
