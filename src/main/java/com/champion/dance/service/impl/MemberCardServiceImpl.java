package com.champion.dance.service.impl;

import com.champion.dance.domain.entity.MemberCard;
import com.champion.dance.domain.mapper.MemberCardMapper;
import com.champion.dance.service.MemberCardService;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/27 18:56
 * Description：
 */
@Service
public class MemberCardServiceImpl implements MemberCardService {
    @Autowired
    private MemberCardMapper memberCardMapper;

    @Override
    public List<MemberCard> findByMemberId(String memberId) {
        return memberCardMapper.findByMemberId(memberId);
    }

    @Override
    public List<MemberCard> findByMemberId(String memberId, RowBounds rowBounds) {
        return memberCardMapper.findByMemberId(memberId,rowBounds);
    }

    @Override
    public int updateByCardId(MemberCard memberCard) {
        return memberCardMapper.updateByPrimaryKeySelective(memberCard);
    }

    @Override
    public MemberCard findByCode(String code) {
        return memberCardMapper.findByCode(code);
    }

    @Override
    public int insertMemberCard(MemberCard memberCard) {
        return memberCardMapper.insertSelective(memberCard);
    }

    @Override
    public MemberCard findByCardId(String cardId) {
        return memberCardMapper.selectByPrimaryKey(cardId);
    }
}
