package com.champion.dance.domain.mapper;

import com.champion.dance.domain.entity.Member;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberMapper {
    int deleteByPrimaryKey(String id);

    int insert(Member record);

    int insertSelective(Member record);

    Member selectByPrimaryKey(String id);

    Member findByOpenId(String openId);

    Member findByMobile(String mobile);

    int updateByPrimaryKeySelective(Member record);

    int updateByPrimaryKey(Member record);
}