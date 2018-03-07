package com.champion.dance.service;

import com.champion.dance.domain.entity.Member;

import java.util.Optional;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/25 17:29
 * Description：
 */
public interface MemberService {
    Optional<Member> findByOpenId(String openId);

    int updateMemberById(Member member);

    int insertMember(Member member);

    Member findByMobile(String mobile);
}
