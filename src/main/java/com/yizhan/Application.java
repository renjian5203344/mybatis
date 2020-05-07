package com.yizhan;

import com.yizhan.entity.User;
import com.yizhan.mapper.UserMapper;
import com.yizhan.sqlsession.YzsqlSession;

public class Application {

    public static void main(String[] args){
        /***
         * 测试查询
         */
//        YzsqlSession sqlSession = new  YzsqlSession();
//        UserMapper mapper = sqlSession.getUserMapper(UserMapper.class);
//       User user = mapper.selectUserById(1);
//        System.out.println(user);

        /***
         * 测试新增
         */
        YzsqlSession sqlSession = new  YzsqlSession();
        UserMapper mapper =  sqlSession.getUserMapper(UserMapper.class);

        int i = mapper.insertUser(2,"测试",28,"bejing");
        System.out.println(i);
        }




}
