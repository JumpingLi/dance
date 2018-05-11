package com.champion.dance.domain.entity;

import com.champion.dance.domain.enumeration.CourseSubscribeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.sql.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CourseSubscriptInfo {
    private Integer id;

    private String courseId;

    private String memberId;

    private String cardId;

    private CourseSubscribeStatus status;

    private Date courseDate;

    private Timestamp subscribeTime;

}