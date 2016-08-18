package s14003.std.it_college.ac.jp.pbl2016.Product;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import s14003.std.it_college.ac.jp.pbl2016.Product.MyHelper;
import s14003.std.it_college.ac.jp.pbl2016.R;

public class OrderCancelActivity extends AppCompatActivity {
    private MyHelper myHelper;
    private Handler mHandler;
    private List<OrderItem> orderItemList;
    private ItemAdapter adapter;
    private List<OrderItem> selectProduct = new ArrayList<>();

    /**
     * OrderItem Class
     * 発注のデータ
     */
    private class OrderItem {
        public int orderId;
        public String mailAddress;
        public String productName;
        public int quantity;
        public int price;
        public int productId;
    }

    /**
     * ItemAdapter Class
     * ListViewの描画を管理するクラス
     */
    private class ItemAdapter extends ArrayAdapter<OrderItem> {
        private LayoutInflater inflater;

        public ItemAdapter(Context context, int resource, List<OrderItem> objects){
            super(context, resource, objects);
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.product_row, null, false);
            TextView nameView = (TextView)view.findViewById(R.id.name);
            TextView priceView = (TextView)view.findViewById(R.id.price);
            OrderItem item = getItem(position);
            nameView.setText(item.productName);
            priceView.setText(String.valueOf(item.price));

            final CheckBox checkBox = (CheckBox)view.findViewById(R.id.checkBox);

            selectProduct = new ArrayList<OrderItem>();
            //final ArrayList<Person> finalSelectPerson = selectPerson;
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        selectProduct.add(orderItemList.get(position));
                        Log.d("cancel", "select:" + selectProduct.get(selectProduct.size() - 1).productName);
                    } else {
                        selectProduct.remove(orderItemList.get(position));
                    }
                }
            });

            return  view;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_cancel);

        // DBマネージャーを生成
        myHelper = new MyHelper(this);

        // ハンドラを生成
        mHandler = new Handler();

        // ListViewの処理
        orderItemList = new ArrayList<OrderItem>();
        adapter = new ItemAdapter(getApplicationContext(), 0, orderItemList);
        adapter.setNotifyOnChange(true);
        ListView listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        // Table取得したデータをListViewにセットするためのスレッド
        //*
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
        //*/

        //listView.setOnItemClickListener(this);

        /*
        // 戻るボタン
        Button btnLogout = (Button) findViewById(R.id.btn_logout);
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logOut();
            }
        });
        */

        // 注文キャンセルボタン
        Button transition = (Button) findViewById(R.id.btn_order_cancel);
        transition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: DBからチェックリストのレコードをデリートするエリートサイヤ人ベジータ
                delete();
            }
        });
    }

    /**
     * delete Method
     * DBからチェックリストのレコードをデリート
     */
    private void delete() {
        for (OrderItem item : selectProduct) {
            StringBuilder sb = new StringBuilder();
            sb.append(item.mailAddress + " ");
            sb.append(item.orderId + " ");
            sb.append(item.price + " ");
            sb.append(item.productId + " ");
            sb.append(item.productName + " ");
            sb.append(item.quantity + " ");
            Log.d("delete", sb.toString());
        }
    }

    /**
     * selectProductList Method
     * 商品DBから情報を取得して表示
     */
    private void selectProductList() {
        // 1. SQLiteDatabaseオブジェクトを取得
        SQLiteDatabase db = myHelper.getReadableDatabase();

        // 2. query()を呼び、検索を行う
        // メールアドレス、商品名、値段、数量を指定
        String[] cols = {
                MyHelper.ColumnsOrderAfter.MAILADDRESS,
                MyHelper.ColumnsOrderAfter.PRODUCTNAME,
                MyHelper.ColumnsOrderAfter.PRICE,
                MyHelper.ColumnsOrderAfter.QUANTITY
        };
        // 発注者のメールアドレスとログインアドレスが同じレコードのみを指定
        String selection = MyHelper.ColumnsOrderAfter.MAILADDRESS + " = ?";
        SharedPreferences data = getSharedPreferences("Maildata", Context.MODE_PRIVATE);
        String mailAddr = data.getString("Mailsave", "www.xvideos.com");
        String[] selectionArgs = {mailAddr};
        Cursor cursor = db.query(MyHelper.TABLE_NAME_ORDER_AFTER, cols, selection, selectionArgs, null, null,
                MyHelper.ColumnsOrder.ORDERID + " ASC");

        // 3. 読み込み位置を先頭にする、falseの場合は結果０件
        if (!cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return;
        }

        // 4. 列のindex(位置)を取得する
        int productnameIndex = cursor.getColumnIndex(MyHelper.ColumnsOrder.PRODUCTNAME);
        int priceIndex = cursor.getColumnIndex(MyHelper.ColumnsOrder.PRICE);
        int quantityIndex = cursor.getColumnIndex(MyHelper.ColumnsOrder.QUANTITY);

        // 5. 行を読み込む
        orderItemList.removeAll(orderItemList);
        do {
            OrderItem item = new OrderItem();
            item.productName = cursor.getString(productnameIndex);
            item.price = cursor.getInt(priceIndex);
            item.quantity = cursor.getInt(quantityIndex);

            orderItemList.add(item);
        } while (cursor.moveToNext());

        // 6. Cursorを閉じる
        cursor.close();

        // 7. データベースを閉じる
        db.close();
    }
}
