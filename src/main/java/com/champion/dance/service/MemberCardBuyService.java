package com.champion.dance.service;

import com.champion.dance.domain.entity.MemberCardBuyInfo;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/28 14:10
 * Description：
 */
public interface MemberCardBuyService {

    List<MemberCardBuyInfo> findByMemberId(String memberId, RowBounds rowBounds);

    int insertBuyInfo(MemberCardBuyInfo buyInfo);
}
