package s14003.std.it_college.ac.jp.pbl2016.OrderCheck;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

/**
 * Created by samuel on 16/08/04.
 */
public class MyHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "product.db";
    private static final int DB_VERSION = 1;

    // 商品テーブル定義
    public static final String TABLE_NAME_PRODUCTS = "Products";
    private static final String SQL_CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_NAME_PRODUCTS + "(" +
            ColumnsProducts._ID + " INTEGER primary key autoincrement," +
            ColumnsProducts.ID + " INTEGER," +
            ColumnsProducts.NAME + " TEXT," +
            ColumnsProducts.PRICE + " INTEGER," +
            ColumnsProducts.STOCK + " INTEGER" + ")";

    public interface ColumnsProducts extends BaseColumns {
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String PRICE = "price";
        public static final String STOCK = "stock";
    }

    // 注文テーブル定義
    public static final String TABLE_NAME_ORDER = "Orderlist";
    private static final String SQL_CREATE_TABLE_ORDER =  "CREATE TABLE " + TABLE_NAME_ORDER + "(" +
            ColumnsOrder.ORDERID + " INTEGER primary key autoincrement," +
            ColumnsOrder.MAILADDRESS + " TEXT," +
            ColumnsOrder.PRODUCTNAME + " TEXT," +
            ColumnsOrder.QUANTITY + " INTEGER," +
            ColumnsOrder.PRICE + " INTEGER," +
            ColumnsOrder.PRODUCTID + " INTEGER)";

    public interface ColumnsOrder extends BaseColumns {
        public static final String ORDERID = "orderid";
        public static final String MAILADDRESS = "mailaddress";
        public static final String PRODUCTNAME = "productname";
        public static final String QUANTITY = "quantity";
        public static final String PRICE = "price";
        public static final String PRODUCTID = "productid";
    }

    // 発注テーブル定義
    public static final String TABLE_NAME_ORDER_AFTER = "OrderAfterlist";
    private static final String SQL_CREATE_TABLE_ORDER_AFTER =  "CREATE TABLE " + TABLE_NAME_ORDER_AFTER + "(" +
            ColumnsOrderAfter.ORDERID + " INTEGER primary key autoincrement," +
            ColumnsOrderAfter.MAILADDRESS + " TEXT," +
            ColumnsOrderAfter.PRODUCTNAME + " TEXT," +
            ColumnsOrderAfter.QUANTITY + " INTEGER," +
            ColumnsOrderAfter.PRICE + " INTEGER," +
            ColumnsOrderAfter.PRODUCTID + " INTEGER)";

    public interface ColumnsOrderAfter extends BaseColumns {
        public static final String ORDERID = "orderid";
        public static final String MAILADDRESS = "mailaddress";
        public static final String PRODUCTNAME = "productname";
        public static final String QUANTITY = "quantity";
        public static final String PRICE = "price";
        public static final String PRODUCTID = "productid";
    }

    // ブラックリストテーブル定義
    public static final String TABLE_NAME_BLACKLIST = "BlackList";
    private static final String SQL_CREATE_TABLE_BLACKLIST =  "CREATE TABLE " + TABLE_NAME_BLACKLIST + "(" +
            ColumnsBlacklist.MAILADDRESS + " TEXT," +
            ColumnsBlacklist.TOTALORDER + " INTEGER)";

    public interface ColumnsBlacklist extends BaseColumns {
        public static final String MAILADDRESS = "mailaddress";
        public static final String TOTALORDER = "totalorder";
    }

    public MyHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        Log.d("Myhelper", "true");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        Log.e("MyHelper","onCreate");
//        db.execSQL(SQL_CREATE_TABLE);

        db.execSQL(SQL_CREATE_TABLE_PRODUCTS);

        db.execSQL(SQL_CREATE_TABLE_ORDER);
        db.execSQL(SQL_CREATE_TABLE_ORDER_AFTER);
        db.execSQL(SQL_CREATE_TABLE_BLACKLIST);

        // 仮の値
        //db.execSQL("insert into Orderlist(MAILADDRESS, PRODUCTNAME, QUANTITY, PRICE, PRODUCTID) values('osamu.com', 'ちくわ', 10, 100, 1);");
        db.execSQL("insert into BlackList(mailaddress, totalOrder) values('failed.com', 1050);");
        db.execSQL("insert into BlackList(mailaddress, totalOrder) values('coffee.com', 1050);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}