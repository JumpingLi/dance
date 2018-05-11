package com.champion.dance.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberCardType {
    private String id;

    private String name;

    private String studioId;

    private Integer expirationDate;

    private BigDecimal price;

}