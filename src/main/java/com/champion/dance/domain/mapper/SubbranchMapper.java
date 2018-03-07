package com.champion.dance.domain.mapper;

import com.champion.dance.domain.entity.Subbranch;
import org.springframework.stereotype.Repository;

@Repository
public interface SubbranchMapper {
    int deleteByPrimaryKey(String id);

    int insert(Subbranch record);

    int insertSelective(Subbranch record);

    Subbranch selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(Subbranch record);

    int updateByPrimaryKey(Subbranch record);
}