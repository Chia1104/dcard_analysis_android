package com.example.dcardtry.SQLconnect;

import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MysqlCon {

    // 資料庫定義
    String mysql_ip = "120.126.19.127";
    int mysql_port = 13306; // Port 預設為 3306
    String db_name = "cgu";
    String url = "jdbc:mysql://"+mysql_ip+":"+mysql_port+"/"+db_name;
    String db_user = "public";
    String db_password = "SQL.110APP";
    public void run() {
        /*
       try {
            Class.forName("com.mysql.jdbc.Driver");
            Log.v("DB","加載驅動成功");
        }catch( ClassNotFoundException e) {
            Log.e("DB","加載驅動失敗");
            return;
        }
*/
        // 連接資料庫
        try {
            Connection con = DriverManager.getConnection(url,db_user,db_password);
            Log.v("DB","遠端連接成功");
        }catch(SQLException e) {
            Log.e("DB","遠端連接失敗");
            Log.e("DB", e.toString());
        }
    }

    public String getData() {
        String data = "";
        try {
            Connection con = DriverManager.getConnection(url, db_user, db_password);
            String sql = "SELECT * FROM cgu.account";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next())
            {
                String id = rs.getString("Name");
                String name = rs.getString("Mail");
                data += id + ", " + name + "\n";
            }
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return data;
    }

    public void insertData(String account,String password,String name,String jobtitle,boolean Administrator) {
        try {
            Connection con = DriverManager.getConnection(url, db_user, db_password);
            String sql = "INSERT INTO cgu.account  (Name,Job,Mail,Password,Administrator) VALUES ('" + name+"',' "+ jobtitle +"',' "+ account + "',' "+ password + "',"+Administrator+")";
            Statement st = con.createStatement();
            st.executeUpdate(sql);
            st.close();

            if(Administrator==true){
                Log.v("DB", "寫入資料完成：" + name + jobtitle + account + password + "管理者");
            }
            else Log.v("DB", "寫入資料完成：" + name + jobtitle + account + password +"非管理者");

        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("DB", "寫入資料失敗");
            Log.e("DB", e.toString());
        }
    }

}