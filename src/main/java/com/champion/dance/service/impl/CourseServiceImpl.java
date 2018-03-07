package com.champion.dance.service.impl;

import com.champion.dance.domain.entity.Course;
import com.champion.dance.domain.mapper.CourseMapper;
import com.champion.dance.service.CourseService;
import com.champion.dance.utils.DateTimeUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/26 17:16
 * Description：
 */
@Service
public class CourseServiceImpl implements CourseService {

    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public List<Course> findAllCourse(LocalDate localDate) {
        // 当天的或者第二天的课程从redis缓存里拿
        List<Course> sourceCourseList = courseMapper.findAllCourse(localDate.getDayOfWeek());
        List<String> courseIds = sourceCourseList.stream().map(Course::getId).collect(Collectors.toList());
        LocalDate now = LocalDate.now();

        if(localDate.compareTo(now) == 0 || localDate.compareTo(now.plusDays(1)) == 0){
            List<Course> cacheCourseList = redisTemplate.opsForValue().multiGet(courseIds);
            cacheCourseList =  cacheCourseList.stream().filter(Objects::nonNull).collect(Collectors.toList());
            if(CollectionUtils.isEmpty(cacheCourseList)){
                Map<String,Course> courseMap = sourceCourseList.stream().collect(Collectors.toMap(Course::getId, Function.identity()));
                redisTemplate.opsForValue().multiSet(courseMap);
                if(localDate.compareTo(now) == 0){
                    long seconds = DateTimeUtil.dateDifferenceSeconds(LocalDateTime.now(),
                            LocalDateTime.of(localDate.plusDays(1).getYear(),localDate.plusDays(1).getMonth(),localDate.plusDays(1).getDayOfMonth(),0,0));
                    redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                        for (String key : courseMap.keySet()) {
                            connection.expire(redisTemplate.getKeySerializer().serialize(key), seconds);
                        }
                        return null;
                    });
                }
                if(localDate.compareTo(now.plusDays(1)) == 0){
                    long seconds = DateTimeUtil.dateDifferenceSeconds(LocalDateTime.now(),
                            LocalDateTime.of(localDate.plusDays(2).getYear(),localDate.plusDays(2).getMonth(),localDate.plusDays(2).getDayOfMonth(),0,0));
                    redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                        for (String key : courseMap.keySet()) {
                            connection.expire(redisTemplate.getKeySerializer().serialize(key), seconds);
                        }
                        return null;
                    });
                }
                cacheCourseList = sourceCourseList;
            }
            return cacheCourseList;
        }
        return sourceCourseList;
    }

    @Override
    public Course findByCourseId(String courseId) {
        return courseMapper.selectByPrimaryKey(courseId);
    }
}
