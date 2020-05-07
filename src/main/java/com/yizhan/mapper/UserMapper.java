package com.yizhan.mapper;

import com.yizhan.annotation.YzInsert;
import com.yizhan.annotation.YzParam;
import com.yizhan.annotation.YzQurey;
import com.yizhan.entity.User;

public interface UserMapper {

    @YzQurey("select * from user where id=#{id}")
    public User selectUserById(@YzParam("id") int id);


    @YzInsert("insert into user(id,userName,userAge,userAddress) values(#{id},#{userName},#{userAge},#{userAddress})")
    int insertUser(@YzParam("id") int id,@YzParam("userName") String userName,@YzParam("userAge") int age,@YzParam("userAddress") String  userAddress);
}
