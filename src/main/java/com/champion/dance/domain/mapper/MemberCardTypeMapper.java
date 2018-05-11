package com.champion.dance.domain.mapper;

import com.champion.dance.domain.entity.MemberCardType;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberCardTypeMapper {
    int deleteByPrimaryKey(String id);

    int insert(MemberCardType record);

    int insertSelective(MemberCardType record);

    MemberCardType selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(MemberCardType record);

    int updateByPrimaryKey(MemberCardType record);
}