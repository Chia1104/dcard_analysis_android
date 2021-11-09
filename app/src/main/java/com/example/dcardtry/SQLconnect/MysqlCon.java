package com.example.dcardtry.SQLconnect;

import static java.lang.Integer.parseInt;

import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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

    public int getData() {
        String data="1";
        String[] tryk ;
        String mail="";
        int a=0;
        //String data = "",l = "1";

        try {
            Connection con = DriverManager.getConnection(url, db_user, db_password);
            String sql = "SELECT Mail FROM cgu.account";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next())
            {
                mail= rs.getString("Mail");
                //String job =rs.getString( "Job" );
                //String name = rs.getString("Name");
                //String password=rs.getString( "Password" );
                //data += id + "," + name  +"\n";//+ "," + job +"," + password
                data+=mail;
                a+=1;

            }

            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return a;
    }

    public void insertData(String account,String password,String name,String jobtitle,boolean Administrator) {
        try {
            Connection con = DriverManager.getConnection(url, db_user, db_password);
            String sql = "INSERT INTO cgu.account  (Name,Job,Mail,Password,Administrator) VALUES ('"+name+"','"+jobtitle+"','"+account+"','"+password+"',"+Administrator+")";
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

    public int HomeAmount(int year,int month){
        int count=0;
        String y=Integer.toString(year);
        String m=Integer.toString( month );
        if(month<10){
            m="0"+m;
        }
        int m1=month+1;
        String nextm=Integer.toString(m1);
        if(month+1<10){
            nextm="0"+nextm;
        }
        String date=y+"-"+m;
        String nextmonth=y+"-"+nextm;
        try {
            Connection con = DriverManager.getConnection(url, db_user, db_password);
            String sql = "SELECT CreatedAt FROM `dcard_rawdata` WHERE CreatedAt>='"+date+"-01' AND CreatedAt<'"+nextmonth+"-01'";
            Statement st = con.createStatement();
            ResultSet resultSet = st.executeQuery( sql );

                while (resultSet.next()){ count+=1; }

            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("DB", "寫入資料失敗");
            Log.e("DB", e.toString());
        }
        return count;
    }

    public float ScoreAnalysis(int year,int month){
        float AvgScore=0,Sum=0,score=0,An=0,Ans=0;
        String nav;//正負符號
        ArrayList<String> ID =new  ArrayList();
        String y=Integer.toString(year);
        String m=Integer.toString( month );
        if(month<10){
            m="0"+m;
        }
        int m1=month+1;
        String nextm=Integer.toString(m1);
        if(month+1<10){
            nextm="0"+nextm;
        }
        String date=y+"-"+m;
        String nextmonth=y+"-"+nextm;

        //找相對應日期內所有文章ID 開始
        try {
            Connection con = DriverManager.getConnection(url, db_user, db_password);
            String sql = "SELECT Id FROM `dcard_rawdata` WHERE CreatedAt>='"+date+"-01' AND CreatedAt<'"+nextmonth+"-01'";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next())
            {
                String id = rs.getString("Id");
                //String date = rs.getString("Date");

                ID.add( id );

            }
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //找相對應日期內所有文章ID 結束

        //計算平均分數 開始
        try {
            Connection con = DriverManager.getConnection(url, db_user, db_password);
            String scoreString = "SELECT SA_Score ,SA_Class FROM `nlp_analysis` WHERE Id >='"+ID.get( 0 )+"' AND Id <= '"+ID.get( ID.size()-1 )+"' ORDER BY Id DESC";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(scoreString);
            while (rs.next()) {
                score = rs.getFloat("SA_Score");
                nav=rs.getString("SA_Class");
                Sum+=score;
            }
            AvgScore=Sum/(ID.size());
            An=Math.round( (AvgScore*100));
            Ans=An/100;
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //計算平均分數 結束
        return Ans;
    }

    public int KeywordCount (int year,int month){
        int Counts=0;
        ArrayList<String> ID =new  ArrayList();
        String y=Integer.toString(year);
        String m=Integer.toString( month );
        if(month<10){
            m="0"+m;
        }
        int m1=month+1;
        String nextm=Integer.toString(m1);
        if(month+1<10){
            nextm="0"+nextm;
        }
        String date=y+"-"+m;
        String nextmonth=y+"-"+nextm;

        //找相對應日期內所有文章ID 開始
        try {
            Connection con = DriverManager.getConnection(url, db_user, db_password);
            String sql = "SELECT Id FROM `dcard_rawdata` WHERE CreatedAt>='"+date+"-01' AND CreatedAt<'"+nextmonth+"-01'";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            while (rs.next())
            {
                String id = rs.getString("Id");

                ID.add( id );

            }
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //找相對應日期內所有文章ID 結束

        //計算關鍵詞文章數 開始
        try {
            Connection con = DriverManager.getConnection(url, db_user, db_password);
            String keywordString = "SELECT Id FROM `comparison` WHERE Id >='"+ID.get( 0 )+"' AND Id <= '"+ID.get( ID.size()-1 )+"' ORDER BY Id DESC";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(keywordString);
            while (rs.next()) {
                Counts+=1;
            }
            st.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //計算關鍵詞文章數 結束

        return Counts;
    }

    public String UserChangedPassword(String Account,String NewPassword){
        boolean changresult=false;
        String s=Account;
        try {
            Connection con = DriverManager.getConnection(url, db_user, db_password);
            String sql = "UPDATE cgu.account SET Password='"+NewPassword+"' WHERE Mail='"+Account+"'";
            Statement st = con.createStatement();
            st.executeUpdate(sql);
            st.close();
            changresult=true;
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("DB", "寫入資料失敗");
            Log.e("DB", e.toString());
        }
        return s;
    }

}