package s14003.std.it_college.ac.jp.pbl2016.Account;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import s14003.std.it_college.ac.jp.pbl2016.R;

public class Account_Profile extends AppCompatActivity {

    private Member_database mdb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account__profile);
        mdb = new Member_database(this);
        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
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
//                String item_msg = changecheck();
                Log.d("data", error);
                if(error.equals("")) {
                    Profdata send_data = getconf_ET();
                    if(updateAccount(send_data)){
                        Toast.makeText(Account_Profile.this, "更新に成功しました。", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                    else{
                        Toast.makeText(Account_Profile.this, "更新に失敗しました。", Toast.LENGTH_SHORT).show();
                    }
                    //登録処理
                    /*alert.setTitle("確認");
                    //alert.setMessage("以下を変更します。よろしいですか？\n" + item_msg);
                    alert.setPositiveButton("OK",
                            new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    Profdata send_data = getconf_ET();
                                    if(updateAccount(send_data)){
                                        Toast.makeText(Account_Profile.this, "更新に成功しました。", Toast.LENGTH_SHORT).show();
                                        finish();
                                    }
                                    else{
                                        Toast.makeText(Account_Profile.this, "更新に失敗しました。", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    alert.setNegativeButton("CANCEL",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //pass
                                }
                            });
                    alert.create().show();
                }*/
                }else{
                    Toast.makeText(Account_Profile.this, error, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    //空白、パスワードの一致を調べる
    private String insertcheck(){
        String err_msg = "";
        EditText lastname_set = (EditText)findViewById(R.id.lastname);
        String lastname_str = lastname_set.getText().toString();
        EditText firstname_set = (EditText)findViewById(R.id.firstname);
        String firstname_str = firstname_set.getText().toString();
        EditText address_set = (EditText)findViewById(R.id.profile_address);
        String address_str = address_set.getText().toString();
        EditText password_set = (EditText)findViewById(R.id.pass_prof);
        String password_str = password_set.getText().toString();
        EditText conf_set = (EditText)findViewById(R.id.passconf);
        String conf_pass_str = conf_set.getText().toString();

        if(lastname_str.equals("") || firstname_str.equals("") ||
                address_str.equals("") || password_str.equals("")){
            err_msg += "未入力の項目があります\n";
        }
        if(!password_str.equals(conf_pass_str)){
            err_msg += "パスワードが一致しません\n";
        }
        return err_msg;
    }

    //変更された項目をcheckする
    /*private String changecheck(){
        String change_msg = "";
        Profdata olddata = new Profdata();
        olddata = serchDBdata(olddata);
        Profdata newdata = getconf_ET();
        if(!olddata.lastname.equals(newdata.lastname)){
            change_msg += "姓\n";
        }
        if(!olddata.firstname.equals(newdata.firstname)){
            change_msg += "名\n";
        }
        if(!olddata.prof_address.equals(newdata.prof_address)){
            change_msg += "住所\n";
        }
        if(!olddata.prof_password.equals(newdata.prof_password)){
            change_msg += "パスワード\n";
        }
        return change_msg;
    }*/
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
    //DBを更新する
    private boolean updateAccount(Profdata Accountdata){
        SQLiteDatabase db = mdb.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Member_database.Columns.LastName,Accountdata.lastname);
        values.put(Member_database.Columns.FirstName,Accountdata.firstname);
        values.put(Member_database.Columns.Address,Accountdata.prof_address);
        values.put(Member_database.Columns.password,Accountdata.prof_password);

        String target = getEmailshare();
        String serach = Member_database.Columns.MailAdddres + " = ?";
        String[] args = {target};

        long ret;

        try{
            ret = db.update(mdb.TABLE_NAME, values, serach, args);
        }finally {
            db.close();
        }
        if(ret == -1){
            //Log.d("database","更新失敗");
            return false;
        }
        else{
            //Log.d("database","更新成功");
            return true;
        }
    }
    //呼びだされた時のEditText取得処理
    private Profdata getconf_ET(){
        Profdata data = new Profdata();

        EditText lastname_set = (EditText)findViewById(R.id.lastname);
        EditText firstname_set = (EditText)findViewById(R.id.firstname);
        EditText address_set = (EditText)findViewById(R.id.profile_address);
        EditText password_set = (EditText)findViewById(R.id.pass_prof);
        //EditText conf_set = (EditText)findViewById(R.id.passconf);

        data.lastname = lastname_set.getText().toString();
        data.firstname = firstname_set.getText().toString();
        data.prof_address = address_set.getText().toString();
        data.prof_password = password_set.getText().toString();

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