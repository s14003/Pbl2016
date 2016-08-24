package s14003.std.it_college.ac.jp.pbl2016.OrderCheck;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

public class Database extends SQLiteOpenHelper {

    private static final String DB_NAME = "product.db";
    private static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "ordered_products";

    private static final String SQL_CREATE_ORDERTABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    Columns._ID + " INTEGER primary key autoincrement," +
                    Columns.productname + " TEXT," +
                    Columns.PRICE + " INTEGER," +
                    Columns.quantity + " INTEGER)";

    private static final String SQL_CREATE_ORDERAFTERTABLE =
            "CREATE TABLE " + TABLE_NAME + "(" +
                    Columns._ID + " INTEGER primary key autoincrement," +
                    Columns.productname + " TEXT," +
                    Columns.PRICE + " INTEGER," +
                    Columns.quantity + " INTEGER)";

    public interface Columns extends BaseColumns {
        public static final String productname = "productname";
        public static final String quantity = "quantity";
        public static final String PRICE  = "price";
    }

    public Database(Context context) {

        super(context, DB_NAME, null, DB_VERSION);

        Log.d("Database", "Database");

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.d("MyHelper", "onCreate");

        db.execSQL(SQL_CREATE_ORDERTABLE);
        db.execSQL(SQL_CREATE_ORDERAFTERTABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
