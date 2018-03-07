package com.champion.dance.service.impl;

import com.champion.dance.domain.dto.CourseSubscribeDto;
import com.champion.dance.domain.entity.CourseSubscriptInfo;
import com.champion.dance.domain.mapper.CourseSubscriptInfoMapper;
import com.champion.dance.service.CourseSubscribeService;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/28 10:56
 * Description：
 */
@Service
public class CourseSubscribeServiceImpl implements CourseSubscribeService{
    @Autowired
    private CourseSubscriptInfoMapper courseSubscriptInfoMapper;

    @Override
    public List<CourseSubscriptInfo> findByParams(Map params) {
        return courseSubscriptInfoMapper.findByParams(params);
    }

    @Override
    public CourseSubscriptInfo findById(Integer id) {
        return courseSubscriptInfoMapper.selectByPrimaryKey(id);
    }

    @Override
    public int insertCourseSubscribe(CourseSubscriptInfo courseSubscriptInfo) {
        return courseSubscriptInfoMapper.insertSelective(courseSubscriptInfo);
    }

    @Override
    public int updateCourseSubscribe(CourseSubscriptInfo courseSubscriptInfo) {
        return courseSubscriptInfoMapper.updateByPrimaryKeySelective(courseSubscriptInfo);
    }

    @Override
    public List<CourseSubscribeDto> findCourseSubscribeByParams(Map params, RowBounds rowBounds) {
        return courseSubscriptInfoMapper.findCourseSubscribeByParams(params,rowBounds);
    }

    @Override
    public Integer findAllByMemberId(String memberId) {
        return courseSubscriptInfoMapper.findAllByMemberId(memberId);
    }
}
