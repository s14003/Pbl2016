package s14003.std.it_college.ac.jp.pbl2016.Account;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class Member_database extends SQLiteOpenHelper {

    private final static String DB_NAME = "Member_master.db";

    private final static int DB_VERSION = 1;

    public final static String TABLE_NAME = "Account";
    public final static String BLACK_LIST_TABLE = "BlackList";

    private final static String set_CREATE_TABLE =
            "create table " + TABLE_NAME + "(" +
                    Columns.LastName + " Text," +
                    Columns.FirstName + " Text," +
                    Columns.MailAdddres + " Text primary key," +
                    Columns.Address + " Text," +
                    Columns.password + " Text);";
    private final static String set_CREATE_BLACKLIST =
            "create table " + BLACK_LIST_TABLE + "(" +
                    Columns.MailAdddres + " Text primary key," +
                    Columns.totalorder + " int);";




    public interface Columns extends BaseColumns{
        public static final String LastName = "LastName";
        public static final String FirstName = "FirstName";
        public static final String MailAdddres = "MailAddress";
        public static final String Address = "Address";
        public static final String password = "password";
        public static final String totalorder = "totalorder";
    }

    public Member_database(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d("database", "make_database");
        db.execSQL(set_CREATE_TABLE);
        db.execSQL(set_CREATE_BLACKLIST);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
