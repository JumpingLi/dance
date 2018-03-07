package com.champion.dance.service.impl;

import com.champion.dance.domain.entity.Teacher;
import com.champion.dance.domain.mapper.TeacherMapper;
import com.champion.dance.service.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/27 9:44
 * Description：
 */
@Service
public class TeacherServiceImpl implements TeacherService {

    @Autowired
    private TeacherMapper teacherMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @SuppressWarnings("unchecked ")
    public Map<String,Teacher> findAllTeacher(List<String> keys) {
        List<Teacher> teacherList = redisTemplate.opsForValue().multiGet(keys);
        teacherList =  teacherList.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(teacherList)){
            teacherList = teacherMapper.findAllTeacher();
            Map<String,Teacher> teacherMap = teacherList.stream().collect(Collectors.toMap(Teacher::getId, Function.identity()));
            redisTemplate.opsForValue().multiSet(teacherMap);
            redisTemplate.executePipelined((RedisCallback<Object>) connection -> {
                for (String key : teacherMap.keySet()) {
                    connection.expire(redisTemplate.getKeySerializer().serialize(key), 60*60*24);
                }
                return null;
            });
        }
        return teacherList.stream().collect(Collectors.toMap(Teacher::getId, Function.identity()));
    }

    @Override
    @SuppressWarnings("unchecked ")
    public Teacher findById(String id) {
        Teacher teacher = (Teacher) redisTemplate.opsForValue().get(id);
        if(Objects.isNull(teacher)){
            teacher = teacherMapper.selectByPrimaryKey(id);
//            redisTemplate.opsForValue().set(id,teacher,60*60*24, TimeUnit.SECONDS);
        }
        return teacher;
    }
}
