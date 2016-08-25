package s14003.std.it_college.ac.jp.pbl2016.Account;

import s14003.std.it_college.ac.jp.pbl2016.MyDatabase;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
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

import s14003.std.it_college.ac.jp.pbl2016.Product.ProductView;
import s14003.std.it_college.ac.jp.pbl2016.R;

public class CreateAccount extends AppCompatActivity {

    private MyDatabase mdb;

    private DataStr date;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        mdb = new MyDatabase(this);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final AlertDialog.Builder datealert = new AlertDialog.Builder(this);

        //戻るボタンの処理
        Button back_btn = (Button)findViewById(R.id.back_button);
        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        //確認ボタンクリック時の処理
        Button conf_btn = (Button)findViewById(R.id.confirmation_btn);
        conf_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //DataStr date = new DataStr();
                date = new DataStr();
                String err_msg = "";
                String date_msg = "";

                //姓の有無判定
                EditText surname_id = (EditText)findViewById(R.id.surname);
                date.surname = surname_id.getText().toString();
                if(date.surname.equals("")){
                    err_msg += "姓を入力して下さい\n";
                }
                else{
                    date_msg += "名前:" + date.surname;
                }

                //名の有無判定
                EditText setname = (EditText)findViewById(R.id.editname);
                date.editname = setname.getText().toString();
                if(date.editname.equals("")){
                    err_msg += "名を入力して下さい\n";
                }
                else{
                    date_msg += " " + date.editname + "\n";
                }

                //Emailの有無の判定
                EditText setEmail = (EditText)findViewById(R.id.setEmail);
                date.Email = setEmail.getText().toString();
                if(date.Email.equals("")){
                    err_msg += "Emailを入力して下さい\n";
                }
                //この辺にEmailが被ってないかの処理
                else if(serchMail(date.Email)){
                    err_msg += "このEmailは使われています\n";
                }
                else{
                    date_msg += "Email: " + date.Email + "\n";
                }

                EditText setaddress_id = (EditText)findViewById(R.id.setaddress);
                date.address = setaddress_id.getText().toString();
                if(date.address.equals("")){
                    err_msg += "住所を入力して下さい\n";
                }
                else{
                    date_msg += "住所: " + date.address + "\n";
                }

                //passwordの有無の判定
                EditText setpassword = (EditText)findViewById(R.id.setpassword);
                date.password = setpassword.getText().toString();
                if(date.password.equals("")){
                    err_msg += "passwordを入力して下さい\n";
                }
                //confpasswordの有無の確認
                EditText setconfpass = (EditText)findViewById(R.id.confpass);
                date.confpass = setconfpass.getText().toString();
                if(date.confpass.equals("")){
                    err_msg += "確認のパスワードを入力して下さい\n";
                }
                else if(!date.confpass.equals(date.password)){
                    err_msg += "パスワードが一致しません\n";
                }

                if(!err_msg.equals("")){
                    alert.setTitle("Error");
                    alert.setMessage(err_msg);
                    alert.setPositiveButton("OK",
                            new DialogInterface.OnClickListener(){
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //Okボタンクリックした時の処理
                                    //pass
                                }
                            });
                    alert.create().show();
                }
                else{
                    datealert.setTitle("確認");
                    datealert.setMessage(date_msg);
                    datealert.setPositiveButton("登録",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //登録ボタン押した時の処理書くところ insert
                                    if(!insertAccount(date)){
                                        Toast.makeText(CreateAccount.this, "登録に失敗しました", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        Toast.makeText(CreateAccount.this, "登録に成功しました", Toast.LENGTH_SHORT).show();
                                        //Emailをしぇあする
                                        setShare(date.Email);
                                        // 商品一覧画面に飛ばす
                                        StartActivity();
                                    }
                                }
                            });
                    datealert.setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    //キャンセルボタン押した時の処理
                                    //pass
                                }
                            });
                    datealert.create().show();
                }
            }
        });
    }
    private class DataStr {
        String surname;
        String editname;
        String Email;
        String address;
        String password;
        String confpass;
    }

    //databaseに登録
    private boolean insertAccount(DataStr accountdata){

        SQLiteDatabase db = mdb.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(MyDatabase.ColumnsAccount.LastName, accountdata.surname);
        values.put(MyDatabase.ColumnsAccount.FirstName, accountdata.editname);
        values.put(MyDatabase.ColumnsAccount.MailAddress, accountdata.Email);
        values.put(MyDatabase.ColumnsAccount.Address, accountdata.address);
        values.put(MyDatabase.ColumnsAccount.Password, accountdata.password);

        ContentValues blackvalues = new ContentValues();
        blackvalues.put(MyDatabase.ColumnsAccount.MailAddress, accountdata.Email);
        blackvalues.put(MyDatabase.ColumnsBlackList.TOTALORDER, 0);

        Log.d("NOW", accountdata.Email + " " + accountdata.password);

        long ret;
        long black;
        try {
          ret = db.insert(mdb.TABLE_NAME_ACCOUNT, null, values);
            black = db.insert(mdb.TABLE_NAME_BLACK_LIST, null,blackvalues);
        }finally {
            db.close();
        }
        if (ret == -1 || black == -1){
            //Log.d("database", "登録失敗");
            return false;
        }
        else{
            //Log.d("database", "登録成功");
            return true;
        }
        //return false;
    }


    //既に使用されているメールアドレスがないか調べる
    private boolean serchMail(String target){

        SQLiteDatabase db_q = mdb.getReadableDatabase();

        String where = MyDatabase.ColumnsAccount.MailAddress + "=?";
        String [] args = {target};
        Cursor cursor = db_q.query(mdb.TABLE_NAME_ACCOUNT,null, where, args,null, null, null);
        if(cursor.moveToFirst()){
            Log.d("Emailserch", "overlap");
            return true;
        }
        else{
            Log.d("Emailserch", "登録いけるよ");
            return false;
        }
    }

    private void setShare(String mail){
        SharedPreferences data = getSharedPreferences("Maildata", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putString("Mailsave", mail);
        editor.putInt("Totalordersave",0);
        editor.apply();
    }
    private void StartActivity(){
        Intent inte = new Intent(this,ProductView.class);
        //under line test code
        //Intent inte = new Intent(this,Account_Profile.class);
        startActivity(inte);
    }
}

