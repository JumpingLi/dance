package com.champion.dance.service.impl;

import com.champion.dance.domain.entity.Comment;
import com.champion.dance.domain.mapper.CommentMapper;
import com.champion.dance.service.CommentService;
import com.champion.dance.domain.dto.CommentDto;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/28 15:15
 * Description：
 */
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentMapper commentMapper;

    @Override
    public List<CommentDto> findCommentsByCourseId(String courseId, RowBounds rowBounds) {
        return commentMapper.findCommentsByCourseId(courseId,rowBounds);
    }

    @Override
    public int insertComment(Comment comment) {
        return commentMapper.insertSelective(comment);
    }

    @Override
    public Comment findByParams(Map params) {
        return commentMapper.findByParams(params);
    }
}
