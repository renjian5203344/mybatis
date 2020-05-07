package com.yizhan.utils;

import java.util.ArrayList;
import java.util.List;

public class SQLUtils {

    public static String replaceParam(String sql,List<Object> selectParams){
        for (int i = 0;i<selectParams.size();i++){
            //select * from user where id = ? nd name=?
            Object paramName = selectParams.get(i);
            String old = "#{"+paramName+"}".replace(" ","");
//            sql = sql.replace("#{"+paramName+"}","?");
            sql = sql.replace(old.trim(),"?");
        }
        return sql;
    }

    public static String replaceParam(String sql,String[] parameterName){
        for(int i=0;i<parameterName.length;i++){
            String string = parameterName[i].trim();
            sql = sql.replace("#{"+string+"}","?");
        }
        return sql;
    }


    //@YzQurey("select * from user where id = #{id} and  name=#{name}")
   public static List<Object> getSelectParams(String sql){
       int startIndex = sql.indexOf("where")+5;
       String whereClause = sql.substring(startIndex);
      String[] paramsStrs = whereClause.split("and");
      List<Object> paramList = new ArrayList<>();
      for (String paramStr: paramsStrs){
          String sp = paramStr.split("=")[1];
//          String param =  sp.replace("#{","").replace("}","");
          String param = sp.replace("#{","").replace("}","").trim();
          paramList.add(param);

      }



      return paramList;


    }

    //insert into user(id,userName,userAge,userAddress) values(#{id},#{userName},#{userAge},#{userAddress})
    public static String[] getInsertParams(String sql){
        //"insert into user(id,userName,userAge,userAddress) values(#{id},#{userName},#{userAge},#{userAddress})
        int startIndex = sql.indexOf("values");
        String value= sql.substring(startIndex+6).replaceAll("#\\{","").replaceAll("}","").replace("(","").replace(")","");
        return value.split(",");
    }

    public static void main(String[] args) {
//       String sql = "select * from user where id = #{id} and name=#{name}";
//        int startIndex = sql.indexOf("where")+5;
//        String whereClause = sql.substring(startIndex);
//        System.out.println(whereClause);//id =#{id} amd

        String sql = "insert into user(id,userName,userAge,userAddress) values(#{id},#{userName},#{userAge},#{userAddress})";
        for (String s : getInsertParams(sql)) {
            System.out.println(s);

        }


    }
}
