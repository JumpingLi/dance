package com.champion.dance.service.impl;

import com.champion.dance.domain.entity.Member;
import com.champion.dance.domain.mapper.MemberMapper;
import com.champion.dance.service.MemberService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/25 17:30
 * Description：
 */
@Slf4j
@Service
public class MemberServiceImpl implements MemberService {
    @Autowired
    private MemberMapper memberMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public Optional<Member> findByOpenId(String openId) {
        Member member = (Member) redisTemplate.opsForValue().get(openId);
        if (member == null || StringUtils.isEmpty(member.getRealName())) {
            member = memberMapper.findByOpenId(openId);
            redisTemplate.opsForValue().set(openId,member,60*60*24, TimeUnit.SECONDS);
        }
        return Optional.ofNullable(member);
    }

    @Override
    public int updateMemberById(Member member) {
        return memberMapper.updateByPrimaryKeySelective(member);
    }

    @Override
    public int insertMember(Member member) {
        return memberMapper.insertSelective(member);
    }

    @Override
    public Member findByMobile(String mobile) {
        return memberMapper.findByMobile(mobile);
    }
}
