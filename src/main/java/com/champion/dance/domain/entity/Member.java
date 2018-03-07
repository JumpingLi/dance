package com.champion.dance.domain.entity;

import com.champion.dance.domain.enumeration.DancerGrade;
import com.champion.dance.domain.enumeration.Sex;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Member {
    private String id;

    private String openId;

    private String nickname;

    private String realName;

    private String mobile;

    private DancerGrade grade;

    private Sex sex;

    private Integer age;

    private Date birthday;

    private String avatarUrl;

    private String province;

    private String city;

    private String district;

    private String region;

    private Integer danceAge;

    private String danceId;

}