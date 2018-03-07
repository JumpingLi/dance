package com.champion.dance.domain.entity;

import com.champion.dance.domain.enumeration.CardStatus;
import com.champion.dance.domain.enumeration.CardType;
import com.champion.dance.domain.enumeration.ChargeType;
import com.champion.dance.domain.enumeration.NameType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberCard {
    private String id;

    private String studioId;

    private String memberId;

    private String code;

    private String name;

    private CardType type;

    private NameType nameType;

    private CardStatus status;

    private Timestamp activationTime;

    private Timestamp expirationTime;

    private Integer remainCount;

    private String counselor;

    private String operator;

    private ChargeType chargeType;

    private BigDecimal amount;

}