package com.champion.dance.domain.entity;

import com.champion.dance.domain.enumeration.StartLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Comment {
    private Integer id;

    private String courseId;

    private String memberId;

    private StartLevel startLevel;

    private Timestamp createTime;

    private String content;

}