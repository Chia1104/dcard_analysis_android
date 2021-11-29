package com.example.dcardtry;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.dcardtry.SQLconnect.MysqlCon;

public class UserChangePassword extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    String Name,Job,Account,Password;//接收帳號相關資料
    TextView DM_Tilte;//側邊選單標題 : 姓名+職稱
    TextView Title;//此頁面標題
    EditText in_oldpassword,in_newpassword,in_newpcheck;
    String oldpassword,newpassword,newpcheck;
    TextView changeresult;//更改結果
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_user_change_password );

        //設定隱藏標題
        getSupportActionBar().hide();
        //設定隱藏狀態
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);

        //取得傳遞過來的資料
        Intent intent = this.getIntent();
        Name = intent.getStringExtra("name");
        Job = intent.getStringExtra( "job" );
        Account = intent.getStringExtra( "account" );
        Password = intent.getStringExtra("password");

        //加上側邊選單姓名、職稱
        DM_Tilte=findViewById( R.id.drawer_menu_title );
        DM_Tilte.setText( Name+"\n"+Job+"\t\t 您好" );

        //最上面顯示目前帳號
        Title=findViewById( R.id.UCP_Title );
        Title.setText( "帳號 : "+Account );

        changeresult=(TextView)findViewById( R.id.UCP_Result );
    }

    public void ChangeBtn(View view){
        //重這裡更改密碼後，密碼會變空格。但是從php檔不會
        Intent intent = this.getIntent();
        Password = intent.getStringExtra("password");
        in_oldpassword=(EditText)findViewById( R.id.UCP_Page_OldPasswordInput );
        in_newpassword=(EditText)findViewById( R.id.UCP_Page_NewPasswordInput );
        in_newpcheck=(EditText)findViewById( R.id.UCP_Page_NewPasswordCheckInput );

        oldpassword=in_oldpassword.getText().toString();
        newpassword=in_newpassword.getText().toString();
        newpcheck=in_newpcheck.getText().toString();

        //進資料庫更改 開始
        if(oldpassword.equals(Password) ){
            if(newpassword.equals( newpcheck )){
                new Thread(() -> {
                    String v;
                    MysqlCon UCPassword = new MysqlCon();
                    // 讀取資料
                    final String changedone = UCPassword.UserChangedPassword(Account,newpassword);
                    if(changedone!=null){
                        v="更改成功";
                    }
                    else v="更改失敗";

                    Log.v("OK",v);
                    changeresult.post(() -> changeresult.setText(v));
                }).start();
            }else{changeresult.setText( "新密碼兩次輸入不同" );}
        }
        else{changeresult.setText( "舊密碼錯誤" );}
        //進資料庫更改 結束
    }

    //側邊選單code Strat
    public void ClickMenu(View view){
        openDrawer(drawerLayout);
    }

    public void openDrawer(DrawerLayout drawerLayout) {
        drawerLayout.openDrawer( GravityCompat.START );
    }

    public void ClickCancle(View view){
        closeDrawer(drawerLayout);
    }

    public static void closeDrawer(DrawerLayout drawerLayout) {
        //check condition
        if(drawerLayout.isDrawerOpen( GravityCompat.START )){
            //when drawer is open
            drawerLayout.closeDrawer( GravityCompat.START );
        }
    }

    public void ClickHome(View view){
        //Redirect(重定向) activity to homePage
        redirectActivity(this,HomePage.class);
    }

    public void ClickArticle(View view){
        //Redirect(重定向) activity to articlePage
        redirectActivity(this,ArticlePage.class);
    }

    public void ClickChart(View view){
        //Redirect(重定向) activity to chartPage
        //redirectActivity(this,);
    }

    public void ClickTrend(View view){
        //Redirect(重定向) activity to chartPage
        redirectActivity(this,MoreBarChart.class);
    }

    public void ClickAccountInfo(View view){
        //Restart activity_home_page.xml
        closeDrawer(drawerLayout);
    }

    public void ClickLogout(View view){
        //回到登入頁面
        logout(this);
    }

    public void redirectActivity(Activity activity, Class aClass){
        //導到其他頁面
        //Initialize intent
        Intent intent=new Intent(activity,aClass);
        intent.putExtra( "name",Name );
        intent.putExtra( "job",Job );
        intent.putExtra( "account",Account );
        intent.putExtra( "password",Password );
        //set flag
        //intent.setFlags( Intent.FLAG_ACTIVITY_NEW_TASK );
        //start activity
        activity.startActivity(intent);
    }

    public static void logout(Activity activity){
        //Initialize alert dialog
        AlertDialog.Builder builder=new AlertDialog.Builder( activity );
        //set title
        builder.setTitle( "登出提醒" );
        //set message
        builder.setMessage( "確定要登出嗎?" );
        //Positive yes button
        builder.setPositiveButton( "是", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Finish activity
                activity.finishAffinity();
                //回到登入頁面
                Intent intent=new Intent(activity,MainActivity.class);
                activity.startActivity( intent );
            }
        } );
        //Negative no button
        builder.setNegativeButton( "否", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Dismiss dialog
                dialog.dismiss();
            }
        } );
        builder.show();
    }

    @Override
    protected void onPause(){
        super.onPause();
        closeDrawer(drawerLayout);
    }
    //側邊選單code End
}