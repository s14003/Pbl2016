package s14003.std.it_college.ac.jp.pbl2016.Account;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import s14003.std.it_college.ac.jp.pbl2016.R;

public class Account_Profile extends AppCompatActivity {

    private Member_database mdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account__profile);
        mdb = new Member_database(this);
        set_Profdata();

        //戻るボタン処理
        Button finish_btn = (Button)findViewById(R.id.finish_button);
        finish_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        //確認ボタン押した時の処理
        Button Update_btn = (Button)findViewById(R.id.update_button);
        Update_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String error = insertcheck();
                if(error.equals("")){
                    //登録処理
                }
                else{
                    Toast.makeText(Account_Profile.this, error, Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    //空白、パスワードの一致を調べる
    private String insertcheck(){
        String err_msg = "";
        EditText lastname_set = (EditText)findViewById(R.id.lastname);
        EditText firstname_set = (EditText)findViewById(R.id.firstname);
        EditText address_set = (EditText)findViewById(R.id.profile_address);
        EditText password_set = (EditText)findViewById(R.id.pass_prof);
        EditText conf_set = (EditText)findViewById(R.id.passconf);

        if(lastname_set.equals("") || firstname_set.equals("") ||
                address_set.equals("") || password_set.equals("")){
            err_msg += "未入力の項目があります";
        }
        if(password_set.equals(conf_set)){
            err_msg += "パスワードが一致しません";
        }
        return err_msg;
    }

    //DBから取得したデータをEditTextに入れる
    private void set_Profdata(){
        Profdata data = new Profdata();
        data = serchDBdata(data);

        EditText lastname_set = (EditText)findViewById(R.id.lastname);
        EditText firstname_set = (EditText)findViewById(R.id.firstname);
        EditText address_set = (EditText)findViewById(R.id.profile_address);
        EditText password_set = (EditText)findViewById(R.id.pass_prof);

        lastname_set.setText(data.lastname);
        firstname_set.setText(data.firstname);
        address_set.setText(data.prof_address);
        password_set.setText(data.prof_password);
    }

    //DBからデータを取ってきてdataに入れて返す
    private Profdata serchDBdata(Profdata data){
        String target = getEmailshare();
        //queryのための変数達
        String[] targetcolumns = {Member_database.Columns.LastName,Member_database.Columns.FirstName,
                Member_database.Columns.Address,Member_database.Columns.password};
        String serachcond = Member_database.Columns.MailAdddres + " = ?";
        String[] serachargs = {target};

        SQLiteDatabase db = mdb.getReadableDatabase();

        Cursor cursor = db.query(Member_database.TABLE_NAME, targetcolumns, serachcond, serachargs,null,null,null);

        while(cursor.moveToNext()){
            data.lastname = cursor.getString(0);
            data.firstname = cursor.getString(1);
            data.prof_address = cursor.getString(2);
            data.prof_password = cursor.getString(3);
        }
        Log.d("test", data.lastname + data.firstname + data.prof_address + data.prof_password);


        return data;
    }
    private class Profdata{
        String lastname;
        String firstname;
        String prof_address;
        String prof_password;
        //String conf_pass;

    }

    //シェアされているEmailを取ってくる
    private String getEmailshare(){
        SharedPreferences data = getSharedPreferences("Maildata", Context.MODE_PRIVATE);
        String getdata = data.getString("Mailsave", "");
        //Log.d("test", getdata);
        return getdata;
    }
}