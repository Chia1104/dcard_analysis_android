package com.example.dcardtry;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class CreateAccount extends AppCompatActivity {

    String accountArray,passwordArray,emailArray,phoneArray;
    /*每組資料皆有帳號、密碼、e-mail、電話*/
    Intent passaccount;
    TextView resultmessage,data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
    }

    public void create(View view){
        EditText accoun = (EditText) findViewById(R.id.C_AccountInput);
        String account = accoun.getText().toString();//抓取帳號

        EditText passwor1 = (EditText) findViewById(R.id.C_Password1Input);
        String password1 = passwor1.getText().toString();//抓取密碼

        EditText passwor2 = (EditText) findViewById(R.id.C_Password2Input);
        String password2 = passwor2.getText().toString();// 抓取第二次輸入的密碼

        EditText name = (EditText) findViewById(R.id.C_NameInput);
        String namer =name.getText().toString();//抓取姓名

        EditText position = (EditText) findViewById(R.id.C_PositionInput);
        String positionr = position.getText().toString();//抓取職稱

        resultmessage = (TextView) findViewById(R.id.createResult);
        data=(TextView)findViewById(R.id.data);

        if(password2.equals(password1)) {
            accountArray=account;
            passwordArray=password1;
            resultmessage.setText("帳號建立成功");
            data.setText("現有帳號 :"+account);
        }
        else{resultmessage.setText("兩次密碼不一樣");}
    }

    public void backToLogin(View view){
        passaccount = new Intent(CreateAccount.this, MainActivity.class);
        passaccount.putExtra("accountlist", accountArray);
        passaccount.putExtra("passwordlist", passwordArray);
        passaccount.putExtra("emaillist", emailArray);
        passaccount.putExtra("phonelist", phoneArray);
        startActivity(passaccount);
    }
}