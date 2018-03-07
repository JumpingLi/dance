package com.champion.dance.service;

import com.champion.dance.domain.entity.Course;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/26 17:08
 * Description：
 */
public interface CourseService {
    List<Course> findAllCourse(LocalDate localDate);

    Course findByCourseId(String courseId);

}
