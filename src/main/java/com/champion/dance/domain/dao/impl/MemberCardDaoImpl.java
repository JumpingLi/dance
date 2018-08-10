package com.champion.dance.domain.dao.impl;

import com.champion.dance.domain.dao.MemberCardDao;
import com.champion.dance.domain.entity.MemberCard;
import com.champion.dance.domain.mapper.MemberCardMapper;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

/**
 * @author: JiangPing Li
 * @date: 2018-08-02 17:26
 */
@Repository
public class MemberCardDaoImpl extends SqlSessionDaoSupport implements MemberCardDao {


    @Override
    @Autowired(required = false)
    public void setSqlSessionFactory(@Qualifier("sqlSessionFactory") SqlSessionFactory sqlSessionFactory) {
        super.setSqlSessionFactory(sqlSessionFactory);
    }
//    @Override
//    public void setSqlSessionTemplate(SqlSessionTemplate sqlSessionTemplate)
//    {
//        super.setSqlSessionTemplate(sqlSessionTemplate);
//    }
    @Override
    public void findCardById(String id) {
        SqlSession session = getSqlSession();
        MemberCardMapper memberCardMapper = session.getMapper(MemberCardMapper.class);
//        MemberCard memberCard =  memberCardMapper.selectByPrimaryKey(id);
        memberCardMapper.updateByPrimaryKeySelective(MemberCard.builder()
                .id(id)
                .name("123")
                .build());
    }
}
