package com.champion.dance.service;

import com.champion.dance.domain.entity.CourseSubscriptInfo;
import com.champion.dance.domain.dto.CourseSubscribeDto;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Map;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/28 10:54
 * Description：
 */
public interface CourseSubscribeService {
    List<CourseSubscriptInfo> findByParams(Map params);

    CourseSubscriptInfo findById(Integer id);

    int insertCourseSubscribe(CourseSubscriptInfo courseSubscriptInfo);

    int updateCourseSubscribe(CourseSubscriptInfo courseSubscriptInfo);

    List<CourseSubscribeDto> findCourseSubscribeByParams(Map params, RowBounds rowBounds);

    Integer findAllByMemberId(String memberId);

    Integer findAllByCardId(String cardId);
}
