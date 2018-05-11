package com.champion.dance.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Subbranch {
    private String id;

    private String name;

    private String studioId;

    private String telephone;

    private String province;

    private String city;

    private String district;

    private String address;

}