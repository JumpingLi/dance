package com.champion.dance.domain.entity;

import com.champion.dance.domain.enumeration.StartLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.DayOfWeek;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Course {
    private String id;

    private String teacherId;

    private String danceId;

    private String studioId;

    private String studioName;

    private String danceName;

    private DayOfWeek weekDate;

    private Timestamp beginTime;

    private Timestamp endTime;

    private StartLevel complexity;

    private Integer totalAmount;

    private Integer aliveAmount;

}