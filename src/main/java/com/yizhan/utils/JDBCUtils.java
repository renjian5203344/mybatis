package com.yizhan.utils;

import com.mysql.jdbc.Connection;

import java.sql.*;
import java.util.List;

//所有mysql操作在里面执行
public class JDBCUtils {
    private static String connet;
    private static String driverClassName;
    private static String url;
    private static String username;
    private static String password;
    private static boolean autoCommit;

    //声名一个Connetion类型的静态属性，用来缓存一个已经存在的链接
    private static Connection conn;

    static {
        config();//启动及加载,加载就有驱动

    }

    private  static void config(){
        driverClassName = "com.mysql.jdbc.Driver";
        url ="jdbc:mysql://localhost:3306/test";
        username = "root";
        password ="123456";
        //设置手动提交
        autoCommit = false;


    }
   //载入数据库驱动类
    private static boolean load(){

        try {
            Class.forName(driverClassName);
            return  true;

        } catch (ClassNotFoundException e) {
            System.out.println("驱动类"+driverClassName+"加载失败");
        }
        return false;

    }

    //检测conn是否可用-不可用返回true

    private static boolean invalid(){
        if (conn !=null){
            try {
                if (conn.isClosed() || !conn.isValid(3)){
                    return true;
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return false;
        }else {
            return true;
        }
    }

    //创建conn
    public static Connection connect(){
        if (invalid()){//链接失败
            load();//加载驱动
            try {
                conn = (Connection) DriverManager.getConnection(url,username,password);
            } catch (SQLException e) {
                System.out.println("建立"+connet+"数据失败,"+e.getMessage());
            }

        }
        return conn;

    }

    //设置是否自动提交事务
    public static void transztion(){
        try {
            conn.setAutoCommit(autoCommit);
        } catch (SQLException e) {
            System.out.println("设置事务提交出现问题"+e.getMessage());
        }
    }

    //创建statment对象
    public static Statement statement(){
        Statement st = null;
        connect();
        transztion();
        //设置提交方式
        try {
            st = conn.createStatement();
        } catch (SQLException e) {
            System.out.println("创建tatment无效"+e.getMessage());
        }

        return st;


    }

   //创建PrepareStatment对象

  private static PreparedStatement prepare(String SQL,boolean autoGeneratekes){
        PreparedStatement ps = null;
        connect();
        transztion();
      try {
        if (autoGeneratekes){

                ps = conn.prepareStatement(SQL,Statement.RETURN_GENERATED_KEYS);


        }else {
            ps = conn.prepareStatement(SQL);
        }
      } catch (SQLException e) {
          System.out.println("创建PrepareStatment对象失败:"+e.getMessage());
      }
      return ps;

  }

   //提交事务
    private static void commit(java.sql.Connection c){
     if (c!=null && !autoCommit){
         try {
             c.commit();
         } catch (SQLException e) {
             e.printStackTrace();
         }
     }
    }


    //回滚事务

    private static void rollback(java.sql.Connection c){
        if (c!=null && !autoCommit){
            try {
                c.rollback();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
    //释放资源
    public static void release(Object closeable){
        if (closeable instanceof ResultSet){
            ResultSet rs = (ResultSet)closeable;
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (closeable instanceof Statement){
            Statement st = (Statement)closeable;
            try {
                st.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (closeable instanceof java.sql.Connection){
            java.sql.Connection c =(java.sql.Connection)closeable;
            try {
                c.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



    //插入数据库
    public static int insert(String sql,boolean autoGenerateKeys,List<Object> params){
        int var = -1;
        if (sql == null || sql.trim().isEmpty()){  //如果sql 等于null或sql trim()之后为空
            System.out.println("sql语句有误无法执行");
        }
        if(!sql.trim().toLowerCase().startsWith("insert")){  //trim且转换为小写且不是以insert开头的sql
            throw new RuntimeException("指定的sql语句不是插入语句");
        }
        sql =sql.trim();
        sql = sql.toLowerCase();
        if (params.size() > 0){  //如果有参数
            PreparedStatement ps =prepare(sql,autoGenerateKeys);
            Connection c = null;
            try {
                c = (Connection) ps.getConnection(); //获取链接对象
            } catch (SQLException e) {
                e.printStackTrace();
            }
            try{
                for (int i=0;i<params.size();i++){
                    Object p = params.get(i);
                    ps.setObject(i+1,p);
                }
                int count = ps.executeUpdate();
                var =count;
                commit(c);

            }catch (SQLException e){
                rollback(c);
            }
        }
        return var;
    }



     public static ResultSet query(String sql, List<Object> params){
        if (sql == null || sql.trim().isEmpty() || !sql.trim().toLowerCase().startsWith("select")){
            throw new RuntimeException("sql语句为空或不是查询语句");
        }

        ResultSet rs = null;
        if (params!=null&& params.size()>0){
            PreparedStatement ps = prepare(sql,false);

            try {
            for (int i = 0; i<params.size();i++){

                    ps.setObject(i+1,params.get(i));

            }
            rs = ps.executeQuery();
            } catch (SQLException e) {
                System.out.println("执行sql失败:"+e.getMessage());
            }

        }else {
            Statement st = statement();
            try {
                st.executeQuery(sql);
            } catch (SQLException e) {
                System.out.println("执行sql失败:"+e.getMessage());
            }
        }
        return rs;

     }







}
