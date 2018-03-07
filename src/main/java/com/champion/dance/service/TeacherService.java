package com.champion.dance.service;

import com.champion.dance.domain.entity.Teacher;

import java.util.List;
import java.util.Map;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/26 17:34
 * Description：
 */
public interface TeacherService {
    Map<String,Teacher> findAllTeacher(List<String> ids);

    Teacher findById(String id);
}
