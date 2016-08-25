package s14003.std.it_college.ac.jp.pbl2016;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/**
 * Created by s15008 on 16/08/24.
 */

public class MyDatabase extends SQLiteOpenHelper {

    private final static String DB_NAME = "pbl.db";

    private final static int DB_VERSION = 1;

    /**
     * Account Table
     * 会員テーブル
     */
    public final static String TABLE_NAME_ACCOUNT = "Account";

    public interface ColumnsAccount extends BaseColumns {
        public static final String LastName = "LastName";
        public static final String FirstName = "FirstName";
        public static final String MailAddress = "MailAddress";
        public static final String Address = "Address";
        public static final String Password = "Password";
    }

    private final static String SQL_CREATE_TABLE_ACCOUNT = "CREATE TABLE " + TABLE_NAME_ACCOUNT + "(" +
                    ColumnsAccount.LastName + " Text," +
                    ColumnsAccount.FirstName + " Text," +
                    ColumnsAccount.MailAddress + " Text primary key," +
                    ColumnsAccount.Address + " Text," +
                    ColumnsAccount.Password + " Text);";

    /**
     * Order Table
     * 注文テーブル
     */
    public static final String TABLE_NAME_ORDER = "Order";

    public interface ColumnsOrder extends BaseColumns {
        public static final String ORDERID = "Orderid";
        public static final String MAILADDRESS = "Mailaddress";
        public static final String PRODUCTNAME = "Productname";
        public static final String QUANTITY = "Quantity";
        public static final String PRICE = "Price";
        public static final String PRODUCTID = "Productid";
    }

    private static final String SQL_CREATE_TABLE_ORDER =  "CREATE TABLE " + TABLE_NAME_ORDER + "(" +
            ColumnsOrder.ORDERID + " INTEGER primary key autoincrement," +
            ColumnsOrder.MAILADDRESS + " TEXT," +
            ColumnsOrder.PRODUCTNAME + " TEXT," +
            ColumnsOrder.QUANTITY + " INTEGER," +
            ColumnsOrder.PRICE + " INTEGER," +
            ColumnsOrder.PRODUCTID + " INTEGER)";

    /**
     * OrderAfter Table
     * 発注テーブル
     */
    public static final String TABLE_NAME_ORDERAFTER = "OrderAfter";

    public interface ColumnsOrderAfter extends BaseColumns {
        public static final String ORDERID = "Orderid";
        public static final String MAILADDRESS = "Mailaddress";
        public static final String PRODUCTNAME = "Productname";
        public static final String QUANTITY = "Quantity";
        public static final String PRICE = "Price";
        public static final String PRODUCTID = "Productid";
    }
    private static final String SQL_CREATE_TABLE_ORDER_AFTER =  "CREATE TABLE " + TABLE_NAME_ORDERAFTER + "(" +
            ColumnsOrderAfter.ORDERID + " INTEGER primary key autoincrement," +
            ColumnsOrderAfter.MAILADDRESS + " TEXT," +
            ColumnsOrderAfter.PRODUCTNAME + " TEXT," +
            ColumnsOrderAfter.QUANTITY + " INTEGER," +
            ColumnsOrderAfter.PRICE + " INTEGER," +
            ColumnsOrderAfter.PRODUCTID + " INTEGER)";


    /**
     * CodeM Table
     * コードマスタテーブル
     * 未使用
     */
    public static final String TABLE_NAME_CODEM = "CodeM";

    /**
     * BlackList Table
     * ブラックリストテーブル
     */
    public final static String TABLE_NAME_BLACK_LIST = "BlackList";

    public interface ColumnsBlackList extends BaseColumns {
        public static final String MAILADDRESS = "MailAddress";
        public static final String TOTALORDER = "Totalorder";
    }

    private final static String SQL_CREATE_TABLE_BLACKLIST = "CREATE TABLE " + TABLE_NAME_BLACK_LIST + "(" +
            ColumnsBlackList.MAILADDRESS + " TEXT primary key," +
            ColumnsBlackList.TOTALORDER + " INTEGER);";

    /**
     * Products Table
     * 商品テーブル
     */
    public static final String TABLE_NAME_PRODUCTS = "Products";

    public interface ColumnsProducts extends BaseColumns {
        public static final String ID = "Id";
        public static final String PRODUCTNAME = "Productname";
        public static final String PRICE = "Price";
        public static final String STOCK = "Stock";
    }

    private static final String SQL_CREATE_TABLE_PRODUCTS = "CREATE TABLE " + TABLE_NAME_PRODUCTS + "(" +
            ColumnsProducts._ID + " INTEGER primary key autoincrement," +
            ColumnsProducts.ID + " INTEGER," +
            ColumnsProducts.PRODUCTNAME + " TEXT," +
            ColumnsProducts.PRICE + " INTEGER," +
            ColumnsProducts.STOCK + " INTEGER" + ")";


    public MyDatabase(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TABLE_ACCOUNT);
        db.execSQL(SQL_CREATE_TABLE_ORDER);
        db.execSQL(SQL_CREATE_TABLE_ORDER_AFTER);
        db.execSQL(SQL_CREATE_TABLE_BLACKLIST);
        db.execSQL(SQL_CREATE_TABLE_PRODUCTS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
    }
}
