package com.champion.dance.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberCardBuyInfo {
    private Integer id;

    private String memberId;

    private String content;

    private Date createTime;

    private String operator;

}