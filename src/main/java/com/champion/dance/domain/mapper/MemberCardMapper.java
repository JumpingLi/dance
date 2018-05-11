package com.champion.dance.domain.mapper;

import com.champion.dance.domain.entity.MemberCard;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberCardMapper {
    int deleteByPrimaryKey(String id);

    int insert(MemberCard record);

    int insertSelective(MemberCard record);

    MemberCard selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(MemberCard record);

    int updateByPrimaryKey(MemberCard record);

    List<MemberCard> findByMemberId(String memberId);

    List<MemberCard> findByMemberId(String memberId, RowBounds rowBounds);

    MemberCard findByCode(String code);
}