package s14003.std.it_college.ac.jp.pbl2016;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by s15008 on 16/08/02.
 */
public class MyHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "product.db";
    private static final int DB_VERSION = 1;
    public static final String TABLE_NAME = "products";
    private static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "(" +
            Columns._ID + " INTEGER primary key autoincrement," +
            Columns.ID + " TEXT," +
            Columns.NAME + " TEXT," +
            Columns.PRICE + " INTEGER," +
            Columns.STOCK + " INTEGER" + ")";

    public interface Columns extends BaseColumns {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String PRICE = "price";
        public static final String STOCK = "stock";
    }

    public MyHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}
