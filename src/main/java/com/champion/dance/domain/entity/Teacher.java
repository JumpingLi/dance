package com.champion.dance.domain.entity;

import com.champion.dance.domain.enumeration.Sex;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Teacher implements Serializable{
    private String id;

    private String name;

    private String nickname;

    private String mobile;

    private Sex sex;

    private String studioId;

    private String danceId;

    private String avatarUrl;

    private String identify;

    private String intro;
}