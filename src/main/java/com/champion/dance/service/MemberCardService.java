package com.champion.dance.service;

import com.champion.dance.domain.entity.MemberCard;
import org.apache.ibatis.session.RowBounds;

import java.util.List;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/27 18:53
 * Description：
 */
public interface MemberCardService {
    List<MemberCard> findByMemberId(String memberId);

    List<MemberCard> findByMemberId(String memberId, RowBounds rowBounds);

    int updateByCardId(MemberCard memberCard);

    MemberCard findByCode(String code);

    int insertMemberCard(MemberCard memberCard);

    MemberCard findByCardId(String cardId);
}
