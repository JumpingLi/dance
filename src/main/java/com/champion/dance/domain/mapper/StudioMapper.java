package com.champion.dance.domain.mapper;

import com.champion.dance.domain.entity.Studio;
import org.springframework.stereotype.Repository;

@Repository
public interface StudioMapper {
    int deleteByPrimaryKey(String id);

    int insert(Studio record);

    int insertSelective(Studio record);

    Studio selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Studio record);

    int updateByPrimaryKey(Studio record);
}