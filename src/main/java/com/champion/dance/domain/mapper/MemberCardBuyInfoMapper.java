package com.champion.dance.domain.mapper;

import com.champion.dance.domain.entity.MemberCardBuyInfo;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberCardBuyInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(MemberCardBuyInfo record);

    int insertSelective(MemberCardBuyInfo record);

    MemberCardBuyInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(MemberCardBuyInfo record);

    int updateByPrimaryKey(MemberCardBuyInfo record);

    List<MemberCardBuyInfo> findByMemberId(String memberId, RowBounds rowBounds);
}