package com.champion.dance.domain.dto;

import com.champion.dance.domain.entity.CourseSubscriptInfo;
import com.champion.dance.domain.enumeration.CardType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.time.DayOfWeek;


/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/3/1 10:36
 * Description：
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper=true)
public class CourseSubscribeDto extends CourseSubscriptInfo {
    private String studioName;
    private String danceName;
    private DayOfWeek weekDate;
    private Timestamp beginTime;
    private Timestamp endTime;
    private String teacherId;
    private CardType type;
    private String name;
    private Integer remainCount;//剩余次数,为0则不限次
}
