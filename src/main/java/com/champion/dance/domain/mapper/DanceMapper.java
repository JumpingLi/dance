package com.champion.dance.domain.mapper;

import com.champion.dance.domain.entity.Dance;
import org.springframework.stereotype.Repository;

@Repository
public interface DanceMapper {
    int deleteByPrimaryKey(String id);

    int insert(Dance record);

    int insertSelective(Dance record);

    Dance selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Dance record);

    int updateByPrimaryKey(Dance record);
}