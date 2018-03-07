package com.champion.dance.domain.mapper;

import com.champion.dance.domain.entity.Comment;
import com.champion.dance.domain.dto.CommentDto;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface CommentMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Comment record);

    int insertSelective(Comment record);

    Comment selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Comment record);

    int updateByPrimaryKeyWithBLOBs(Comment record);

    int updateByPrimaryKey(Comment record);

    List<CommentDto> findCommentsByCourseId(String courseId, RowBounds rowBounds);

    Comment findByParams(Map params);
}