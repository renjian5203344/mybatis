package com.yizhan.sqlsession;

import java.lang.reflect.Proxy;

public class YzsqlSession {

    //动态代理
    public static <T> T getUserMapper(Class clazz){
        return (T)Proxy.newProxyInstance(clazz.getClassLoader(),new Class[]{clazz},new UserMapperInvocationHandler(clazz));
    }
}
