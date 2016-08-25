package s14003.std.it_college.ac.jp.pbl2016.Product;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

//import s14003.std.it_college.ac.jp.pbl2016.ChangeAccountInformationActivity;
import s14003.std.it_college.ac.jp.pbl2016.Account.Account_Profile;
import s14003.std.it_college.ac.jp.pbl2016.LoginActivity;
import s14003.std.it_college.ac.jp.pbl2016.MyDatabase;
import s14003.std.it_college.ac.jp.pbl2016.Product.OrderCancelActivity;
//import s14003.std.it_college.ac.jp.pbl2016.OrderCheckActivity;
import s14003.std.it_college.ac.jp.pbl2016.OrderCheck.Ordercheck;
import s14003.std.it_college.ac.jp.pbl2016.R;

public class ProductView extends AppCompatActivity {
    private MyDatabase myHelper;
    private Handler mHandler;
    private List<ProductItem> itemList;
    private List<ProductDbItem> itemDbList;
    private ItemAdapter adapter;
    private List<ProductItem> selectProduct = new ArrayList<>();

    /**
     * ProductItem Class
     * 商品のデータ
     */
    private class ProductItem {
        public int _id;
        public int id;
        public String name;
        public int price;
        public int stock;
        boolean flag;

        void setCheckFlag(boolean checkFlag) {
            this.flag = checkFlag;
        }
    }

    /**
     * ItemAdapter Class
     * ListViewの描画を管理するクラス
     */
    private class ItemAdapter extends ArrayAdapter<ProductItem> {
        private LayoutInflater inflater;

        public ItemAdapter(Context context, int resource, List<ProductItem> objects){
            super(context, resource, objects);
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.product_row, null, false);
            TextView nameView = (TextView)view.findViewById(R.id.name);
            TextView priceView = (TextView)view.findViewById(R.id.price);
            final ProductItem item = getItem(position);
            nameView.setText(item.name);
            priceView.setText(String.valueOf(item.price));

            final CheckBox checkBox = (CheckBox)view.findViewById(R.id.checkBox);

            selectProduct = new ArrayList<ProductItem>();
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        item.setCheckFlag(true);
                        selectProduct.add(itemList.get(position));
                        Log.e("select:", selectProduct.get(selectProduct.size() - 1).name + "stock:" + selectProduct.get(selectProduct.size() - 1).stock);
                    } else {
                        item.setCheckFlag(false);
                        selectProduct.remove(itemList.get(position));
                    }
                }
            });

            return  view;
        }
    }

    /**
     * onCreate Method
     * Activityの初期化処理
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);
        // DBマネージャーを生成

        myHelper = new MyDatabase(this);

        // ハンドラを生成
        mHandler = new Handler();

        // 商品テーブルの初期化(初回のみの実行)
        SharedPreferences spData = getSharedPreferences("initTable", Context.MODE_PRIVATE);
        boolean isInitProductsTable = spData.getBoolean("productsTable", false);
        if (!isInitProductsTable) {
            setProductDbData();
            initTable();
            SharedPreferences.Editor editor = spData.edit();
            editor.putBoolean("productsTable", true);
            editor.apply();
        }

        // ログイン情報の初期化(デバッグ用)
        spData = getSharedPreferences("Maildata", Context.MODE_PRIVATE);
        String mailAddr = spData.getString("Mailsave", "");
        Log.d("NOW", "ProductView.onCreate" + mailAddr);
        if (mailAddr.isEmpty()) {
            SharedPreferences.Editor editor = spData.edit();
            editor.putString("Mailsave", "failed.com");
            editor.apply();
            Log.d("NOW", "ProductView.isEmpty : " + mailAddr);
        }

        // ListViewの処理
        itemList = new ArrayList<>();
        adapter = new ItemAdapter(getApplicationContext(), 0, itemList);
        adapter.setNotifyOnChange(true);
        ListView listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);


        Log.d("productList", "Create");


        // Table取得したデータをListViewにセットするためのスレッド
        (new Thread(new Runnable() {
            @Override
            public void run() {
                selectProductList();

                //メインスレッドのメッセージキューにメッセージを登録します。
                mHandler.post(new Runnable (){
                    //run()の中の処理はメインスレッドで動作されます。
                    public void run(){
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        })).start();

        // ログアウトボタン
        Button btnLogout = (Button) findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });

        // 注文確認ボタン
        Button transition = (Button) findViewById(R.id.btn_check_order);
        transition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkOrderList();
            }
        });
    }

    /**
     * logOut Method
     * ログアウト処理
     * ログイン情報を削除してログイン画面に遷移する
     */
    public void logOut() {
        // ログインアドレスを空にする
        SharedPreferences data = getSharedPreferences("Maildata", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = data.edit();
        editor.putString("Mailsave", "");
        editor.apply();

        // ログイン画面に遷移する
        startActivity(new Intent(this, LoginActivity.class));
    }

    /**
     * checkOrder Method
     * 注文確認
     */
    public void checkOrderList() {
        // ダイアログの生成
        AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
        alertDlg.setTitle("注文確認");

        if (!isSelectProduct()) {
            // 選択エラーのダイアログ
            alertDlg.setMessage("商品を選択してください");
            alertDlg.create().show();
            return;
        }

        // 確認ダイアログの生成
        AlertDialog.Builder acceptDlg = new AlertDialog.Builder(this);
        acceptDlg.setTitle("注文確認");
        acceptDlg.setMessage("注文を確定してもいいですか？");
        acceptDlg.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        insertOrderListRecord();
                        selectProduct.clear();
                    }
                });
        acceptDlg.setNegativeButton("Cancel", null);

        // 表示
        acceptDlg.create().show();
    }

    /**
     * insertOrderListRecord Method
     * 注文リストをレコードに追加する
     */
    private void insertOrderListRecord() {
        // 注文リストをDBに登録
        for (ProductItem item : selectProduct) {

            boolean isErr = !insertRecord(item);

            // DB登録エラー
            if (isErr) {
                // 確認ダイアログの生成
                AlertDialog.Builder alertDlg = new AlertDialog.Builder(this);
                alertDlg.setTitle("注文確認");
                alertDlg.setMessage("商品を登録出来ませんでした");
                alertDlg.setPositiveButton("OK", null);

                // 表示
                alertDlg.create().show();
                return;
            }
        }


        // 注文確認画面へ遷移する
        Intent intent = new Intent(this, Ordercheck.class);
        startActivity(intent);

    }

    /**
     * insert Method
     * 注文テーブルにレコードを追加する
     */
    private boolean insertRecord(ProductItem item) {
        SQLiteDatabase db = myHelper.getWritableDatabase();

        // 列に対応する値をセットする
        // アカウントメールアドレスを取得する
        SharedPreferences data = getSharedPreferences("Maildata", Context.MODE_PRIVATE);
        String mailAddr = data.getString("Mailsave", "failed.com");
        Log.d("NOW", "PRODUCTinsert" + mailAddr);
        ContentValues values = new ContentValues();
        values.put(MyDatabase.ColumnsOrder.MAILADDRESS, mailAddr);
        values.put(MyDatabase.ColumnsOrder.PRODUCTNAME, item.name);
        values.put(MyDatabase.ColumnsOrder.PRICE, item.price);
        values.put(MyDatabase.ColumnsOrder.PRODUCTID, item.id);

        // データベースに行を追加する
        long id = db.insert(MyDatabase.TABLE_NAME_ORDER, null, values);
        if (id == -1) {
            Log.v("CHECK_ORDER", "行の追加に失敗したよ" + mailAddr);
            return false;
        }
        else {
            Log.v("CHECK_ORDER", "行の追加に成功したよ" + mailAddr);
        }

        db.close();
        return true;
    }

    /**
     * isSelectProduct Method
     * 注文リストに１個以上登録されているか確認する
     * @return
     */
    private boolean isSelectProduct() {
        return selectProduct.size() > 0;
    }

    /**
     * selectProductList Method
     * 商品DBから情報を取得して表示
     */
    private void selectProductList() {
        // 1. SQLiteDatabaseオブジェクトを取得
        SQLiteDatabase db = myHelper.getReadableDatabase();

        // 2. query()を呼び、検索を行う
        Cursor cursor = db.query(MyDatabase.TABLE_NAME_PRODUCTS, null, null, null, null, null,
                MyDatabase.ColumnsProducts._ID + " ASC");

        // 3. 読み込み位置を先頭にする、falseの場合は結果０件
        if (!cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return;
        }

        // 4. 列のindex(位置)を取得する
        int _idIndex = cursor.getColumnIndex(MyDatabase.ColumnsProducts._ID);
        int idIndex = cursor.getColumnIndex(MyDatabase.ColumnsProducts.ID);
        int nameIndex = cursor.getColumnIndex(MyDatabase.ColumnsProducts.PRODUCTNAME);
        int priceIndex = cursor.getColumnIndex(MyDatabase.ColumnsProducts.PRICE);
        int stockIndex = cursor.getColumnIndex(MyDatabase.ColumnsProducts.STOCK);

        // 5. 行を読み込む
        itemList.removeAll(itemList);
        do {
            ProductItem item = new ProductItem();
            item._id = cursor.getInt(_idIndex);
            item.id = cursor.getInt(idIndex);
            item.name = cursor.getString(nameIndex);
            item.price = cursor.getInt(priceIndex);
            item.stock = cursor.getInt(stockIndex);

            itemList.add(item);
        } while (cursor.moveToNext());

        // 6. Cursorを閉じる
        cursor.close();

        // 7. データベースを閉じる
        db.close();

    }

    /**
     * onCreateOptionsMenu Method
     * オプションメニューの項目設定
     * @param m
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        Log.d("NOW", "onCreateOptionsMenu");
        m.add(0, 0, 0, "アカウント情報変更");
        m.add(0, 10, 1, "商品のキャンセル");
        /*
        m.add(0, 20, 2, "DB更新");
        m.add(0, 30, 3, "メールアドレス登録(failed.com)");
        m.add(0, 40, 4, "メールアドレス登録(coffee.com)");
        */
        return true;
    }

    /**
     * onOptionsItemSelected Method
     * オプションメニューの押下時処理
     * @param mi
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        switch(mi.getItemId()) {
            case 0:
                //TODO: アカウント情報変更-削除
                startActivity(new Intent(this, Account_Profile.class));
                return true;
            case 10:
                //TODO: 商品のキャンセル
                startActivity(new Intent(this, OrderCancelActivity.class));
                return true;
            /*
            case 20:
                //TODO: DB更新(後で消す)
                //スレッドを生成して起動します
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        selectProductList();

                        //メインスレッドのメッセージキューにメッセージを登録します。
                        mHandler.post(new Runnable (){
                            //run()の中の処理はメインスレッドで動作されます。
                            public void run(){
                                adapter.notifyDataSetChanged();
                            }
                        });
                    }
                })).start();
                return true;
            case 30:
                //TODO: 後でけす
                SharedPreferences data = getSharedPreferences("Maildata", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = data.edit();
                editor.putString("Mailsave", "failed.com");
                editor.apply();
                return true;
            case 40:
                //TODO: 後でけす
                data = getSharedPreferences("Maildata", Context.MODE_PRIVATE);
                editor = data.edit();
                editor.putString("Mailsave", "coffee.com");
                editor.apply();
                return true;
            */

            default:
                return false;
        }
    }

    /**
     * initTable method
     * DBの初期化
     */
    public void initTable() {
        SQLiteDatabase db = myHelper.getWritableDatabase();

        // 一旦を削除
        int count = db.delete(MyDatabase.TABLE_NAME_PRODUCTS, null, null);

        for (int i = 0; i < itemDbList.size(); i++) {
            ProductDbItem item = itemDbList.get(i);

            // 列に対応する値をセットする
            ContentValues values = new ContentValues();
            values.put(MyDatabase.ColumnsProducts.ID, item.id);
            values.put(MyDatabase.ColumnsProducts.PRODUCTNAME, item.name);
            values.put(MyDatabase.ColumnsProducts.PRICE, item.price);
            values.put(MyDatabase.ColumnsProducts.STOCK, item.stock);

            // データベースに行を追加する
            long id = db.insert(MyDatabase.TABLE_NAME_PRODUCTS, null, values);
            if (id == -1) {
                Log.d("Database", "failed");
            }
        }
    }

    /**
     * ProductDbItem Class
     *
     */
    private class ProductDbItem {
        int id;
        String name;
        int price;
        int stock;
    }

    /**
     * setProductDbData Method
     * 商品リストの仮のデータをセットする
     */
    public void setProductDbData() {
        itemDbList = new ArrayList<ProductDbItem>();

        ProductDbItem item = new ProductDbItem();
        item.id = 1;
        item.name = "赤鉛筆";
        item.price = 50;
        item.stock = 100;
        itemDbList.add(item);

        item = new ProductDbItem();
        item.id = 2;
        item.name = "青鉛筆";
        item.price = 50;
        item.stock = 50;
        itemDbList.add(item);

        item = new ProductDbItem();
        item.id = 3;
        item.name = "消しゴム";
        item.price = 75;
        item.stock = 1000;
        itemDbList.add(item);

        item = new ProductDbItem();
        item.id = 4;
        item.name = "三角定規";
        item.price = 120;
        item.stock = 10;
        itemDbList.add(item);

        item = new ProductDbItem();
        item.id = 5;
        item.name = "ボールペン黒";
        item.price = 80;
        item.stock = 25;
        itemDbList.add(item);

        item = new ProductDbItem();
        item.id = 6;
        item.name = "ボールペン赤";
        item.price = 90;
        item.stock = 24;
        itemDbList.add(item);

        item = new ProductDbItem();
        item.id = 7;
        item.name = "３色ボールペン";
        item.price = 120;
        item.stock = 30;
        itemDbList.add(item);

    }
}