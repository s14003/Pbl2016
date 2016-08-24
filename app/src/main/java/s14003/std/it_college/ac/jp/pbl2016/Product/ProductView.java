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
import s14003.std.it_college.ac.jp.pbl2016.OrderCancelActivity;
//import s14003.std.it_college.ac.jp.pbl2016.OrderCheckActivity;
import s14003.std.it_college.ac.jp.pbl2016.OrderCheck.Ordercheck;
import s14003.std.it_college.ac.jp.pbl2016.R;

public class ProductView extends AppCompatActivity {

    private MyHelper myHelper;
    private Handler mHandler;
    private List<ProductItem> itemList;
    private ItemAdapter adapter;
    private List<ProductItem> selectProduct = new ArrayList<>();

    /**
     * ProductItem Class
     * 商品のデータ
     */
    private class ProductItem {
        public int _id;
        public String id;
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
            //final ArrayList<Person> finalSelectPerson = selectPerson;
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        item.setCheckFlag(true);
                        selectProduct.add(itemList.get(position));
                        Log.e("select:", selectProduct.get(selectProduct.size() - 1).name);
                    } else {
                        item.setCheckFlag(false);
                        selectProduct.remove(itemList.get(position));
                    }
                }
            });

            /*
            Button btnOK = (Button)findViewById(R.id.btn_check_order);
            btnOK.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    for (ProductItem productItem : selectProduct) {
                        Log.e("ProductName :", productItem.name);
                        Log.e("ProductPrice :", String.valueOf(productItem.price));
                    }
                }
            });
            //*/

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

        myHelper = new MyHelper(this);

        // ハンドラを生成
        mHandler = new Handler();

        // ListViewの処理
        itemList = new ArrayList<>();
        adapter = new ItemAdapter(getApplicationContext(), 0, itemList);
        adapter.setNotifyOnChange(true);
        ListView listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        Log.d("productList", "Create");
        // Table取得したデータをListViewにセットするためのスレッド
        //*
        (new Thread(new Runnable() {
            @Override
            public void run() {
                setProductData();

                //メインスレッドのメッセージキューにメッセージを登録します。
                mHandler.post(new Runnable (){
                    //run()の中の処理はメインスレッドで動作されます。
                    public void run(){
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        })).start();
        //*/

        //listView.setOnItemClickListener(this);

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
     */
    public void logOut() {
        Log.d("LOGOUT", "ProductView.logOut");
        //test
        if (isSelectProduct()) selectProduct.remove(0);
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
            alertDlg.setMessage("商品を選択してくださいね");
            alertDlg.setPositiveButton(
                    "OK",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
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
                        Log.d("ORDER", "OK clicked");
                        insertOrderListRecord();
                    }
                });
        acceptDlg.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
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
                alertDlg.setPositiveButton(
                        "OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                // 表示
                alertDlg.create().show();
                return;
            }
        }

        // 注文確認画面へ繊遷移
        /*Intent intent = new Intent(this, Ordercheck.class);
        startActivity(intent);*/
    }

    /**
     * insert Method
     * 注文テーブルにレコードを追加する
     */
    private boolean insertRecord(ProductItem item) {
        SQLiteDatabase db = myHelper.getWritableDatabase();

        // 列に対応する値をセットする
        //TODO: アカウントメールアドレスを取得する
        SharedPreferences data = getSharedPreferences("Maildata", Context.MODE_PRIVATE);
        String mailAddr = data.getString("Mailsave", "www.xvideos.com");
        ContentValues values = new ContentValues();
        values.put(MyHelper.ColumnsOrder.MAILADDRESS, mailAddr);
        values.put(MyHelper.ColumnsOrder.PRODUCTNAME, item.name);
        values.put(MyHelper.ColumnsOrder.PRICE, item.price);
        values.put(MyHelper.ColumnsOrder.PRODUCTID, item.id);

        // データベースに行を追加する
        long id = db.insert(MyHelper.TABLE_NAME_ORDER, null, values);
        if (id == -1) {
            Log.v("CHECK_ORDER", "行の追加に失敗したよ");
            return false;
        }
        else {
            Log.v("CHECK_ORDER", "行の追加に成功したよ");
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
        Log.d("CHECK_ORDER", "isSelectProduct()");
        return selectProduct.size() > 0;
    }

    private void setProductData() {
        Log.d("", "setProductData()");
        selectProductList();
    }

    /**
     * selectProductList Method
     * 商品DBから情報を取得して表示
     */
    private void selectProductList() {
        // 1. SQLiteDatabaseオブジェクトを取得
        SQLiteDatabase db = myHelper.getReadableDatabase();

        // 2. query()を呼び、検索を行う
        Cursor cursor = db.query(MyHelper.TABLE_NAME, null, null, null, null, null,
                MyHelper.Columns._ID + " ASC");

        // 3. 読み込み位置を先頭にする、falseの場合は結果０件
        if (!cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return;
        }

        // 4. 列のindex(位置)を取得する
        int _idIndex = cursor.getColumnIndex(MyHelper.Columns._ID);
        int idIndex = cursor.getColumnIndex(MyHelper.Columns.ID);
        int nameIndex = cursor.getColumnIndex(MyHelper.Columns.NAME);
        int priceIndex = cursor.getColumnIndex(MyHelper.Columns.PRICE);
        int stockIndex = cursor.getColumnIndex(MyHelper.Columns.STOCK);

        // 5. 行を読み込む
        itemList.removeAll(itemList);
        do {
            ProductItem item = new ProductItem();
            item._id = cursor.getInt(_idIndex);
            item.id = cursor.getString(idIndex);
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
        m.add(0, 20, 2, "DB更新");
        m.add(0, 30, 3, "メールアドレス登録");
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
            case 20:
                //TODO: DB更新(後で消す)
                //スレッドを生成して起動します
                (new Thread(new Runnable() {
                    @Override
                    public void run() {
                        initTable();
                        setProductData();

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
                editor.putString("Mailsave", "yogi.com");
                editor.apply();
                return true;
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
        int count = db.delete(MyHelper.TABLE_NAME, null, null);

        setProductDbData();

        for (int i = 0; i < itemDbList.size(); i++) {
            ProductDbItem item = itemDbList.get(i);

            // 列に対応する値をセットする
            ContentValues values = new ContentValues();
            values.put(MyHelper.Columns.ID, item.id);
            values.put(MyHelper.Columns.NAME, item.name);
            values.put(MyHelper.Columns.PRICE, item.price);
            values.put(MyHelper.Columns.STOCK, item.stock);

            // データベースに行を追加する
            long id = db.insert(MyHelper.TABLE_NAME, null, values);
            if (id == -1) {
                Log.d("Database", "failed");
            }
        }
    }

    private class ProductDbItem {
        String id;
        String name;
        int price;
        int stock;
    }

    private List<ProductDbItem> itemDbList;

    public void setProductDbData() {
        itemDbList = new ArrayList<ProductDbItem>();

        ProductDbItem item = new ProductDbItem();
        item.id = "A01";
        item.name = "赤鉛筆";
        item.price = 50;
        item.stock = 100;
        itemDbList.add(item);

        item = new ProductDbItem();
        item.id = "A02";
        item.name = "青鉛筆";
        item.price = 50;
        item.stock = 50;
        itemDbList.add(item);

        item = new ProductDbItem();
        item.id = "A03";
        item.name = "消しゴム";
        item.price = 75;
        item.stock = 1000;
        itemDbList.add(item);

        item = new ProductDbItem();
        item.id = "A04";
        item.name = "三角定規";
        item.price = 120;
        item.stock = 10;
        itemDbList.add(item);

        item = new ProductDbItem();
        item.id = "A05";
        item.name = "ボールペン黒";
        item.price = 80;
        item.stock = 25;
        itemDbList.add(item);

        item = new ProductDbItem();
        item.id = "A06";
        item.name = "ボールペン赤";
        item.price = 90;
        item.stock = 24;
        itemDbList.add(item);

        item = new ProductDbItem();
        item.id = "A07";
        item.name = "３色ボールペン";
        item.price = 120;
        item.stock = 30;
        itemDbList.add(item);

    }
}