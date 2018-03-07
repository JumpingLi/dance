package com.champion.dance.domain.mapper;

import com.champion.dance.domain.dto.CourseSubscribeDto;
import com.champion.dance.domain.entity.CourseSubscriptInfo;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Map;

public interface CourseSubscriptInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CourseSubscriptInfo record);

    int insertSelective(CourseSubscriptInfo record);

    CourseSubscriptInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CourseSubscriptInfo record);

    int updateByPrimaryKey(CourseSubscriptInfo record);

    List<CourseSubscriptInfo> findByParams(Map params);

    List<CourseSubscribeDto> findCourseSubscribeByParams(Map params , RowBounds rowBounds);

    Integer findAllByMemberId(String memberId);
}