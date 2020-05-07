package com.yizhan.sqlsession;

import com.yizhan.annotation.YzInsert;
import com.yizhan.annotation.YzParam;
import com.yizhan.annotation.YzQurey;
import com.yizhan.utils.JDBCUtils;
import com.yizhan.utils.SQLUtils;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class UserMapperInvocationHandler implements InvocationHandler {
    private Class userMapperClazz;

    public UserMapperInvocationHandler(Class clazz){
        this.userMapperClazz = clazz;


    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        YzQurey yzQurey = method.getDeclaredAnnotation(YzQurey.class);

        if (null != yzQurey){
            //获取结果
            return getResult(method,yzQurey,args);
        }

       YzInsert yzInsert = method.getDeclaredAnnotation(YzInsert.class);
       if (null != yzInsert){
           String insert  = yzInsert.value();
           // 1.插入参数
           String[] insertParame = SQLUtils.getInsertParams(insert);
           // 2.参数绑定
           ConcurrentHashMap<String, Object> paramMap = getMethodParam(method, args);
           // 3.将参数值添加到list里面
           List<Object> paramValueList = addParamToList(insertParame,paramMap);

           insert = SQLUtils.replaceParam(insert,insertParame);

           return JDBCUtils.insert(insert,false,paramValueList);



       }

        return null;
    }

    private List<Object> addParamToList(String[] insertParame, ConcurrentHashMap<String, Object> paramMap) {
        List<Object> paramValueList = new ArrayList<>();
        for (String name :insertParame){
            Object paramValue = paramMap.get(name.trim());
            paramValueList.add(paramValue);
        }
        return paramValueList;
    }

    private Object getResult(Method method, YzQurey yzQurey, Object[] args) throws Exception {
        String querySql = yzQurey.value();
        //获取sql参数
        List<Object> paramList = SQLUtils.getSelectParams(querySql);

        //替换sql参数
        querySql = SQLUtils.replaceParam(querySql,paramList);

        //获取方法参数，绑定值
        ConcurrentHashMap<String,Object> paramap = getMethodParam(method,args);

        List<Object>  paramValueList = new ArrayList<>();
        for (Object param:paramList){
            Object paramValue = paramap.get(param);
            paramValueList.add(paramValue);
        }
        System.out.println("paramValueList" + paramValueList);
        ResultSet rs = JDBCUtils.query(querySql,paramValueList);
        if (!rs.next()){
            return null;
        }

        Class<?> returnType = method.getReturnType();
        Object obj = returnType.newInstance();
        //光标移位左移一位
        rs.previous();

        while (rs.next()){
            Field[] filds = returnType.getDeclaredFields();
            for (Field field:filds){
                String fildName = field.getName();
                field.setAccessible(true);
                if (("id").equals(fildName) || ("userAge").equals(fildName)){
                    int fieldValue = rs.getInt(fildName);
                    field.setInt(obj,fieldValue);

                }else {
                    String fileValue = rs.getString(fildName);
                    field.setAccessible(true);
                    field.set(obj,fileValue);
                }

            }
        }

                return obj;
    }


    private ConcurrentHashMap<String,Object> getMethodParam(Method method, Object[] args) {
        ConcurrentHashMap paramMap = new ConcurrentHashMap();
        Parameter[] parameters = method.getParameters();
        for(int i = 0;i<parameters.length;i++){
            YzParam ykParam = parameters[i].getAnnotation(YzParam.class);
            if(null == ykParam){
                continue;
            }
            String paramName = ykParam.value();
            Object paramValue =args[i];
            paramMap.put(paramName,paramValue);
        }
        return paramMap;
    }
}
