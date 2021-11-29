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

public class ManagerChangePassword extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    String Name,Job,Account,Password;//接收登入頁面傳過來的資料
    TextView MDM_Tilte;//側邊選單標題 : 姓名+職稱
    TextView MTitle;//此頁面標題
    EditText in_Moldpassword,in_Mnewpassword,in_Mnewpcheck;
    String Moldpassword,Mnewpassword,Mnewpcheck;
    TextView Mchangeresult;//更改結果

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_manager_change_password );

        //設定隱藏標題
        getSupportActionBar().hide();
        //設定隱藏狀態
        getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_FULLSCREEN);

        drawerLayout = (DrawerLayout) findViewById(R.id.managerDrawerLayout);

        //取得傳遞過來的資料
        Intent intent = this.getIntent();
        Name = intent.getStringExtra("name");
        Job = intent.getStringExtra( "job" );
        Account = intent.getStringExtra( "account" );
        Password = intent.getStringExtra("password");

        //加上側邊選單姓名、職稱
        MDM_Tilte=findViewById( R.id.manager_drawer_menu_title );
        MDM_Tilte.setText(Name+"\n"+Job+"\t\t 您好" );

        //最上面顯示目前帳號
        MTitle=findViewById( R.id.MCP_Title );
        MTitle.setText( "帳號 : "+Account );

        Mchangeresult=(TextView)findViewById( R.id.MCP_Result );
    }

    public void MChangeBtn(View view){
        //重這裡更改密碼後，密碼會變空格。但是從php檔不會
        Intent intent = this.getIntent();
        Password = intent.getStringExtra("password");
        in_Moldpassword=(EditText)findViewById( R.id.MCP_Page_OldPasswordInput );
        in_Mnewpassword=(EditText)findViewById( R.id.MCP_Page_NewPasswordInput );
        in_Mnewpcheck=(EditText)findViewById( R.id.MCP_Page_NewPasswordCheckInput );

        Moldpassword=in_Moldpassword.getText().toString();
        Mnewpassword=in_Mnewpassword.getText().toString();
        Mnewpcheck=in_Mnewpcheck.getText().toString();

        //進資料庫更改 開始
        if(Moldpassword.equals(Password) ){
            if(Mnewpassword.equals( Mnewpcheck )){
                new Thread(() -> {
                    String v;
                    MysqlCon UCPassword = new MysqlCon();
                    // 讀取資料
                    final String changedone = UCPassword.UserChangedPassword(Account,Mnewpassword);
                    if(changedone!=null){
                        v="更改成功";
                    }
                    else v="更改失敗";

                    Log.v("OK",v);
                    Mchangeresult.post(() -> Mchangeresult.setText(v));
                }).start();
            }else{Mchangeresult.setText( "新密碼兩次輸入不同" );}
        }
        else{Mchangeresult.setText( "舊密碼錯誤" );}
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

    public void ClickAddUser(View view){
        redirectActivity( this,Manager_AddAccount.class );
    }

    public void ClickManagerHome(View view){
        redirectActivity( this,ManagerPage.class );
    }

    public void Click_M_ChangePassword(View view){
        redirectActivity( this,ManagerChangePassword.class );
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
        builder.setPositiveButton( "是", (dialog, which) -> {
            //Finish activity
            activity.finishAffinity();
            //回到登入頁面
            Intent intent=new Intent(activity,MainActivity.class);
            activity.startActivity( intent );
        });
        //Negative no button
        builder.setNegativeButton( "否", (dialog, which) -> {
            //Dismiss dialog
            dialog.dismiss();
        });
        builder.show();
    }

    //側邊選單code End
}