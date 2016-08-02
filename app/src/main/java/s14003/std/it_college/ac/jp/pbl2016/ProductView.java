package s14003.std.it_college.ac.jp.pbl2016;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ProductView extends Activity implements AdapterView.OnItemClickListener, View.OnClickListener {

    private MyHelper myHelper;
    private Handler mHandler;


    /**
     * リストビュークリック時の処理
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long l) {

        ProductItem item = (ProductItem) parent.getItemAtPosition(position);

        /*
        Intent intent = new Intent(this, EditProduct.class);

        intent.putExtra("mode", "edit");

        intent.putExtra("_id", item._id);
        intent.putExtra("id", item.id);
        intent.putExtra("name", item.name);
        intent.putExtra("price", item.price);
        intent.putExtra("stock", item.stock);

        startActivity(intent);
        //*/
    }

    @Override
    public void onClick(View view) {
        /*
        Intent intent = new Intent(this, EditProduct.class);

        intent.putExtra("mode", "add");

        startActivity(intent);
        */
    }

    private class ProductItem {
        public int _id;
        public String id;
        public String name;
        public int price;
        public int stock;
    }

    private List<ProductItem> itemList;
    private ItemAdapter adapter;

    private void setProductData() {
        /*
        ProductItem item = new ProductItem();
        item.id = "A01";
        item.name = "赤鉛筆";
        item.price = "50";
        item.stock = "100";
        itemList.add(item);

        item = new ProductItem();
        item.id = "A02";
        item.name = "青鉛筆";
        item.price = "50";
        item.stock = "50";
        itemList.add(item);
        */

        selectProductList();
    }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_view);

        // DBマネージャーを生成
        myHelper = new MyHelper(this);

        // ハンドラを生成
        mHandler = new Handler();

        // ListViewの処理
        itemList = new ArrayList<ProductItem>();
        adapter = new ItemAdapter(getApplicationContext(), 0, itemList);
        adapter.setNotifyOnChange(true);
        ListView listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);

        // Table取得したデータをListViewにセットするためのスレッド
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

        listView.setOnItemClickListener(this);

        // 新規登録ボタン
        /*
        Button btn_add = (Button)findViewById(R.id.btn_add);
        btn_add.setOnClickListener(this);
        //*/

        Button btn_ini = (Button)findViewById(R.id.btn_ini);
        btn_ini.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
            }
        });


    }

    /**
     * ListViewの描画を管理するクラス
     */
    private class ItemAdapter extends ArrayAdapter<ProductItem> {
        private LayoutInflater inflater;

        public ItemAdapter(Context context, int resource, List<ProductItem> objects){
            super(context, resource, objects);
            inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = inflater.inflate(R.layout.product_row, null, false);
            TextView idView = (TextView)view.findViewById(R.id.id);
            TextView nameView = (TextView)view.findViewById(R.id.name);
            TextView priceView = (TextView)view.findViewById(R.id.price);
            TextView stockView = (TextView)view.findViewById(R.id.stock);
            ProductItem item = getItem(position);
            idView.setText(item.id);
            nameView.setText(item.name);
            priceView.setText(String.valueOf(item.price));
            stockView.setText(String.valueOf(item.stock));
            return  view;
        }
    }

    /**
     * テーブルを初期化するための処理
     */
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

}
