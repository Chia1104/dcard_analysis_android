package com.example.dcardtry;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.dcardtry.SQLconnect.MysqlCon;
import java.sql.DriverManager;
import java.util.List;

public class Manager_AddAccount extends AppCompatActivity{

    private DrawerLayout drawerLayout;
    private EditText addAccountInput;
    private EditText addPasswordInput;
    private EditText addNameInput;
    private EditText addJobTitleInput;
    private boolean addAdministratorInput;
    private String Mtxt;
    private TextView result;
    private RadioButton managerbtn;
    String REGISTER_URL = "http://localhost:13306/registration.php";
    String Name,Job,Account,Password;//接收登入頁面傳過來的資料
    TextView MDM_Tilte;//側邊選單標題 : 姓名+職稱



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_manager_add_account );

        //設定隱藏標題
        getSupportActionBar().hide();
        //設定隱藏狀態
        getWindow().getDecorView().setSystemUiVisibility( View.SYSTEM_UI_FLAG_FULLSCREEN);

        drawerLayout=findViewById( R.id.managerDrawerLayout );
        result=findViewById( R.id.addresult );

        //取得傳遞過來的資料
        Intent intent = this.getIntent();
        Name = intent.getStringExtra("name");
        Job = intent.getStringExtra( "job" );
        Account = intent.getStringExtra( "account" );
        Password = intent.getStringExtra("password");

        MDM_Tilte=findViewById( R.id.manager_drawer_menu_title );
        MDM_Tilte.setText( "\t"+Name+"\n"+Job+"\t\t 您好" );

    }



    public void AddAccountBtn(View v) {
        // 取得 EditText 資料
        addAccountInput = (EditText) findViewById(R.id.new_Account);
        addPasswordInput = (EditText) findViewById(R.id.new_Password);
        addNameInput = (EditText) findViewById(R.id.new_Name);
        addJobTitleInput = (EditText) findViewById(R.id.new_JobTitle);
        managerbtn = (RadioButton) findViewById( R.id.ManagerCheckBtn );

        String addAccount = addAccountInput.getText().toString();
        String addPassword = addPasswordInput.getText().toString();
        String addName = addNameInput.getText().toString();
        String addJobTitle = addJobTitleInput.getText().toString();

        if(managerbtn.isChecked()){
            addAdministratorInput=true;
            Mtxt="管理者";
        }
        else {
            addAdministratorInput=false;
            Mtxt="非管理者";
        }

        //Initialize alert dialog
        AlertDialog.Builder builder=new AlertDialog.Builder( Manager_AddAccount.this);
        //set title
        builder.setTitle( "確認新增" );
        //set message
        builder.setMessage( "確定新增 : \n"+addAccount+"\n"+addPassword+"\n"+addName+"\n"+addJobTitle+"\n"+Mtxt+"\n"+"?" );
        //Positive yes button
        builder.setPositiveButton( "確定", (dialog, which) -> new Thread(() -> {
            // 將資料寫入資料庫
            MysqlCon con = new MysqlCon();
            con.insertData(addAccount,addPassword,addName,addJobTitle,addAdministratorInput);

            // 清空 EditText
            addAccountInput.post(() -> addAccountInput.setText(""));
            addPasswordInput.post(() -> addPasswordInput.setText(""));
            addNameInput.post(() -> addNameInput.setText(""));
            addJobTitleInput.post(() -> addJobTitleInput.setText(""));
            managerbtn.setChecked( false );

            // 讀取更新後的資料
            //final String updata = con.getData();

            //Log.v("OK", updata);
            //result.post(() -> result.setText(updata));

        }).start());
        //Negative no button
        builder.setNegativeButton( "取消 ", (dialog, which) -> {
            //Dismiss dialog
            dialog.dismiss();
        });
        builder.show();

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

    public void Click_M_ChangePassword(View view){ redirectActivity( this,ManagerChangePassword.class ); }

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
    //側邊選單code End
}