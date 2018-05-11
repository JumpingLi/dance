package com.champion.dance.service;

import com.champion.dance.domain.entity.Comment;
import com.champion.dance.domain.dto.CommentDto;
import org.apache.ibatis.session.RowBounds;

import java.util.List;
import java.util.Map;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/28 15:14
 * Description：
 */
public interface CommentService {

    List<CommentDto> findCommentsByCourseId(String courseId, RowBounds rowBounds);

    int insertComment(Comment comment);

    Comment findByParams(Map params);
}
