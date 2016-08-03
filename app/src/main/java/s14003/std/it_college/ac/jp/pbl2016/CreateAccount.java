package s14003.std.it_college.ac.jp.pbl2016;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class CreateAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        final AlertDialog.Builder alert = new AlertDialog.Builder(this);
        //final AlertDialog.Builder datealert = new AlertDialog.Builder(this);

        //確認ボタンクリック時の処理
        Button conf_btn = (Button)findViewById(R.id.confirmation_btn);
        conf_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DateStr date = new DateStr();
                String err_msg = "";

                //姓の有無判定
                EditText surname_id = (EditText)findViewById(R.id.surname);
                date.surname = surname_id.getText().toString();
                if(date.surname.equals("")){
                    err_msg += "姓を入力して下さい\n";
                }

                //名の有無判定
                EditText setname = (EditText)findViewById(R.id.editname);
                date.editname = setname.getText().toString();
                if(date.editname.equals("")){
                    err_msg += "名を入力して下さい\n";
                }

                //Emailの有無の判定
                EditText setEmail = (EditText)findViewById(R.id.setEmail);
                date.Email = setEmail.getText().toString();
                if(date.Email.equals("")){
                    err_msg += "Emailを入力して下さい\n";
                }
                //Emailの全角文字判定
                if(emjudge(date.Email)){
                    err_msg += "Emailは半角のみです\n";
                }

                //この辺にEmailが被ってないかの処理

                EditText setaddress_id = (EditText)findViewById(R.id.setaddress);
                date.address = setaddress_id.getText().toString();
                if(date.address.equals("")){
                    err_msg += "住所を入力して下さい\n";
                }

                //passwordの有無の判定
                EditText setpassword = (EditText)findViewById(R.id.setpassword);
                date.password = setpassword.getText().toString();
                if(date.password.equals("")){
                    err_msg += "passwordを入力して下さい\n";
                }
                if(emjudge(date.password)){
                    err_msg += "パスワードは半角入力のみです\n";
                }

                //confpasswordの有無の確認
                EditText setconfpass = (EditText)findViewById(R.id.confpass);
                date.confpass = setconfpass.getText().toString();
                if(date.confpass.equals("")){
                    err_msg += "確認のパスワードを入力して下さい\n";
                }
                if(!date.confpass.equals(date.password)){
                        err_msg += "確認用パスワードが間違っています\n";
                }
                if(!err_msg.equals("")){
                    alert.setMessage(err_msg);
                }
                alert.create().show();
            }
        });
    }

    private class DateStr{
        String surname;
        String editname;
        String Email;
        String address;
        String password;
        String confpass;
    }

    //全角の判定
    private boolean emjudge(String s){
        boolean judge = false;
        char[] strs = s.toCharArray();
        for(int i = 0;i < s.length();i++){
            if(String.valueOf(strs[i]).getBytes().length >= 2){
                return true;
            }
        }
        return judge;
    }
}
