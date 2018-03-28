package com.champion.dance.service.impl;

import com.champion.dance.domain.entity.MemberCardBuyInfo;
import com.champion.dance.domain.mapper.MemberCardBuyInfoMapper;
import com.champion.dance.service.MemberCardBuyService;
import org.apache.ibatis.session.RowBounds;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/28 14:12
 * Description：
 */
@Service
public class MemberCardBuyServiceImpl implements MemberCardBuyService {

    @Autowired
    private MemberCardBuyInfoMapper memberCardBuyInfoMapper;
    @Override
    public List<MemberCardBuyInfo> findByMemberId(String memberId, RowBounds rowBounds) {
        return memberCardBuyInfoMapper.findByMemberId(memberId,rowBounds);
    }

    @Override
    public int insertBuyInfo(MemberCardBuyInfo buyInfo) {
        return memberCardBuyInfoMapper.insertSelective(buyInfo);
    }
}
