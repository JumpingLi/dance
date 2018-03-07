package com.champion.dance.service.impl;

import com.champion.dance.domain.entity.Studio;
import com.champion.dance.domain.mapper.StudioMapper;
import com.champion.dance.service.StudioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Created with dance
 * Author: jiangping.li
 * Date: 2018/2/28 14:35
 * Description：
 */
@Service
public class StudioServiceImpl implements StudioService {
    @Autowired
    private StudioMapper studioMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    @SuppressWarnings("unchecked")
    public Studio findById(String studioId) {
        Studio studio = (Studio) redisTemplate.opsForValue().get(studioId);
        if(Objects.isNull(studio)){
            studio = studioMapper.selectByPrimaryKey(studioId);
            redisTemplate.opsForValue().set(studioId,studio,60*60*24, TimeUnit.SECONDS);
        }
        return studio;
    }
}
