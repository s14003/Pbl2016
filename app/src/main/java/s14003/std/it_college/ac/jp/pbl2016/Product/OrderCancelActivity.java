package s14003.std.it_college.ac.jp.pbl2016.Product;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
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
    private List<OrderItem> selectProduct = new ArrayList<>();  //選択中の商品リスト

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
            View view = inflater.inflate(R.layout.activity_order_cancel_row, null, false);
            TextView nameView = (TextView)view.findViewById(R.id.idname_tv);
            TextView priceView = (TextView)view.findViewById(R.id.price_tv);
            TextView quantityView = (TextView)view.findViewById(R.id.quantity_tv);
            OrderItem item = getItem(position);
            nameView.setText(item.productName);
            priceView.setText(String.valueOf(item.price));
            quantityView.setText(String.valueOf(item.quantity));

            final CheckBox checkBox = (CheckBox)view.findViewById(R.id.checkBox);

            //selectProduct = new ArrayList<OrderItem>();
            //final ArrayList<Person> finalSelectPerson = selectPerson;
            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        selectProduct.add(orderItemList.get(position));
                        Log.d("cancel", "select:" + selectProduct.get(selectProduct.size() - 1).productName
                                + " orderId:" + selectProduct.get(selectProduct.size() - 1).orderId
                                + " quantity:" + selectProduct.get(selectProduct.size() - 1).quantity);
                    } else {
                        selectProduct.remove(orderItemList.get(position));
                    }
                    Log.d("select", "selectProduct.size():" + String.valueOf(selectProduct.size()));
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

        //*
        // 戻るボタン
        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("OrderCancelActivity", "btnBack.back");
                back();
            }
        });
        //*/

        // 注文キャンセルボタン
        Button transition = (Button) findViewById(R.id.btn_order_cancel);
        transition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setOrderCancelDialog();
            }
        });
    }

    private void back() {
        selectProduct.clear();
        startActivity(new Intent(this, ProductView.class));
        //TODO: 後でけす
        //*
        SQLiteDatabase db = this.myHelper.getReadableDatabase();
        Cursor cursor = db.query(MyHelper.TABLE_NAME_BLACKLIST, new String[] {MyHelper.ColumnsBlacklist.TOTALORDER, MyHelper.ColumnsBlacklist.MAILADDRESS},
                MyHelper.ColumnsBlacklist.MAILADDRESS + " = \"failed.com\"", null, null, null, null);

        if (!cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return;
        }
        Log.d("back", String.valueOf(cursor.getInt(cursor.getColumnIndex(MyHelper.ColumnsBlacklist.TOTALORDER))));
        cursor.close();
        db.close();
        //*/
    }

    private void setOrderCancelDialog() {
        // リスト表示用のアラートダイアログ
        adapter = new ItemAdapter(getApplicationContext(), 0, this.selectProduct);
        adapter.setNotifyOnChange(true);
        ListView listView = new ListView(this);
        listView.setAdapter(adapter);

        AlertDialog.Builder listDlg = new AlertDialog.Builder(this);
        listDlg.setTitle("商品のキャンセル");
        listDlg.setView(listView);

        listDlg.setPositiveButton(
                "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // OK ボタンクリック処理
                        updateTable();
                    }
                });
        listDlg.setNegativeButton(
                "Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Cancel ボタンクリック処理
                    }
                });
        // 表示
        listDlg.create().show();
    }

    private void updateTable() {
        // 以下をトライザクション処理で行う
        // 一つでもエラーがあると全てロールバックする
        SQLiteDatabase dbWrite = myHelper.getWritableDatabase();
        dbWrite.beginTransaction();
        try {
            for (OrderItem item : selectProduct) {
                updateProductListTable(dbWrite, item);
                updateBlackListTable(dbWrite, item);
                updateOrderAfterListTable(dbWrite, item);
            }
            dbWrite.setTransactionSuccessful();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            dbWrite.endTransaction();
        }
        selectProduct.clear();
        updateSelectProductList();
    }

    /**
     * updateProductListTable Method
     * 商品テーブルの在庫数を更新する
     * @param item
     */
    private void updateProductListTable(SQLiteDatabase dbWrite, OrderItem item) throws Exception {
        //TODO: 商品テーブルの在庫数に注文テーブルの数量を加算する
        // 商品名、数量を指定
        String[] cols = {
                MyHelper.ColumnsProducts.NAME,
                MyHelper.ColumnsProducts.STOCK
        };
        String selection = MyHelper.ColumnsProducts.NAME + " = ?";
        String[] selectionArgs = {item.productName};
        Cursor cursor = dbWrite.query(MyHelper.TABLE_NAME_PRODUCTS, cols, selection, selectionArgs, null, null, null);

        // 読み込み位置を先頭にする、falseの場合は結果０件
        if (!cursor.moveToFirst()) {
            cursor.close();
            //dbRead.close();
            return;
        }

        // 現在の商品テーブルのストックに注文テーブルの数量を加算した値を求める
        int stockIndex = cursor.getColumnIndex(MyHelper.ColumnsProducts.STOCK);
        int updateStock = cursor.getInt(stockIndex) + item.quantity;

        // Cursorを閉じる
        cursor.close();

        // 列に対応する値をセットする
        ContentValues values = new ContentValues();
        values.put(MyHelper.ColumnsProducts.STOCK, updateStock);

        // 加算した値をテーブルに反映させる
        long id = dbWrite.update(MyHelper.TABLE_NAME_PRODUCTS, values, selection, selectionArgs);
        if (id == -1) {
            Log.v("CHECK_ORDER", "行の追加に失敗したよ");
        }
        else {
            Log.v("CHECK_ORDER", "行の追加に成功したよ");
        }
    }

    /**
     * updateBlacckListTable Method
     * ブラックリストテーブルの注文合計額を更新をする
     * @param item
     * @return
     */
    private boolean updateBlackListTable(SQLiteDatabase dbWrite, OrderItem item) throws Exception {
        boolean successful = false;
        //TODO: ブラックリストテーブルの注文合計額から(発注.数量 * 発注.数量)を減算する
        // メールアドレス、注文合計額を指定
        String[] cols = {
                MyHelper.ColumnsBlacklist.MAILADDRESS,
                MyHelper.ColumnsBlacklist.TOTALORDER
        };
        String selection = MyHelper.ColumnsBlacklist.MAILADDRESS + " = ?";
        // アカウントメールアドレスを取得する
        SharedPreferences data = getSharedPreferences("Maildata", Context.MODE_PRIVATE);
        String mailAddr = data.getString("Mailsave", "failed.com");
        String[] selectionArgs = {mailAddr};
        Cursor cursor = dbWrite.query(MyHelper.TABLE_NAME_BLACKLIST, cols, selection, selectionArgs, null, null, null);

        // 3. 読み込み位置を先頭にする、falseの場合は結果０件
        if (!cursor.moveToFirst()) {
            cursor.close();
            //dbRead.close();
            return false;
        }

        // ブラックリストテーブルの注文合計額から(発注.数量 * 発注.数量)を減算した値を求める
        int totalorderIndex = cursor.getColumnIndex(MyHelper.ColumnsBlacklist.TOTALORDER);
        int updateTotalOrder = cursor.getInt(totalorderIndex) - (item.price * item.quantity);
        Log.d("updateProductListTable", String.valueOf(updateTotalOrder));

        // 6. Cursorを閉じる
        cursor.close();

        // 列に対応する値をセットする
        ContentValues values = new ContentValues();
        values.put(MyHelper.ColumnsBlacklist.TOTALORDER, updateTotalOrder);

        // 加算した値をテーブルに反映させる
        long id = dbWrite.update(MyHelper.TABLE_NAME_BLACKLIST, values, selection, selectionArgs);
        if (id == -1) {
            Log.v("CHECK_ORDER", "行の追加に失敗したよ");
        }
        else {
            Log.v("CHECK_ORDER", "行の追加に成功したよ");
        }

        return successful;
    }

    /**
     * updateOrderAfterListTable Method
     * DBからチェックリストのレコードを削除
     */
    private void updateOrderAfterListTable(SQLiteDatabase dbWrite, OrderItem item) throws Exception {
        //TODO: 注文テーブルからチェックリストのレコードを削除する
        // データベースから発注IDが同じレコードを削除する
        String whereClause = MyHelper.ColumnsOrderAfter.ORDERID + " = ?";
        String whereArgs[] = {String.valueOf(item.orderId)};
        long id = dbWrite.delete(MyHelper.TABLE_NAME_ORDER_AFTER, whereClause, whereArgs);
        if (id == -1) {
            Log.d("CHECK_ORDER", "レコードの削除に失敗したよ");
        }
        else {
            Log.d("CHECK_ORDER", "レコードの削除に成功したよ");
        }
    }

    /**
     * updateSelectProductList Method
     * 商品リストビューを更新する
     */
    private void updateSelectProductList() {
        orderItemList.clear();
        adapter = new ItemAdapter(getApplicationContext(), 0, orderItemList);
        adapter.setNotifyOnChange(true);
        ListView listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);
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
        // メールアドレス、商品名、値段、数量を指定
        String[] cols = {
                MyHelper.ColumnsOrderAfter.MAILADDRESS,
                MyHelper.ColumnsOrderAfter.PRODUCTNAME,
                MyHelper.ColumnsOrderAfter.PRICE,
                MyHelper.ColumnsOrderAfter.QUANTITY,
                MyHelper.ColumnsOrderAfter.ORDERID
        };
        // 発注者のメールアドレスとログインアドレスが同じレコードのみを指定
        String selection = MyHelper.ColumnsOrderAfter.MAILADDRESS + " = ?";
        SharedPreferences data = getSharedPreferences("Maildata", Context.MODE_PRIVATE);
        String mailAddr = data.getString("Mailsave", "failed.com");
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
        int orderIdIndex = cursor.getColumnIndex(MyHelper.ColumnsOrder.ORDERID);

        // 5. 行を読み込む
        orderItemList.removeAll(orderItemList);
        do {
            OrderItem item = new OrderItem();
            item.productName = cursor.getString(productnameIndex);
            item.price = cursor.getInt(priceIndex);
            item.quantity = cursor.getInt(quantityIndex);
            item.orderId = cursor.getInt(orderIdIndex);

            orderItemList.add(item);
        } while (cursor.moveToNext());

        // 6. Cursorを閉じる
        cursor.close();

        // 7. データベースを閉じる
        db.close();
    }
}
