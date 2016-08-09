package s14003.std.it_college.ac.jp.pbl2016.Product;

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

    // 商品テーブル設定
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

    // 注文テーブル設定
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

    public MyHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE);
        db.execSQL(SQL_CREATE_TABLE_ORDER);

        // 仮の値
        //db.execSQL("insert into Orderlist(MAILADDRESS, PRODUCTNAME, QUANTITY, PRICE, PRODUCTID) values('osamu.com', 'ちくわ', 10, 100, 1);");
        //db.execSQL("insert into Orderlist(MAILADDRESS, PRODUCTNAME, QUANTITY, PRICE, PRODUCTID) values('osamu.com', 'かまぼこ', 15, 150, 2);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
