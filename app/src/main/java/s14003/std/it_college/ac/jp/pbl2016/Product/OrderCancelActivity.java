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

import s14003.std.it_college.ac.jp.pbl2016.MyDatabase;
import s14003.std.it_college.ac.jp.pbl2016.R;

public class OrderCancelActivity extends AppCompatActivity {
    private MyDatabase myHelper;
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

            checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (checkBox.isChecked()) {
                        selectProduct.add(orderItemList.get(position));
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
        myHelper = new MyDatabase(this);

        // ハンドラを生成
        mHandler = new Handler();

        // ListViewの処理
        orderItemList = new ArrayList<OrderItem>();
        adapter = new ItemAdapter(getApplicationContext(), 0, orderItemList);
        adapter.setNotifyOnChange(true);
        ListView listView = (ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);

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

        // 戻るボタン
        Button btnBack = (Button) findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                back();
            }
        });

        // 注文キャンセルボタン
        Button transition = (Button) findViewById(R.id.btn_order_cancel);
        transition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSelectProduct()) {
                    setOrderCancelDialog();
                }
                else {
                    createFailedDialog();
                }
            }
        });
    }

    private void createFailedDialog() {
        AlertDialog.Builder failedDlg = new AlertDialog.Builder(this);
        failedDlg.setMessage("商品を選択してください");
        failedDlg.create().show();
    }

    /**
     * isSelectProduct Method
     * 注文リストに１個以上登録されているか確認する
     * @return
     */
    private boolean isSelectProduct() {
        return selectProduct.size() > 0;
    }

    private void back() {
        selectProduct.clear();
        startActivity(new Intent(this, ProductView.class));

        //TODO: 後でけす
        /*
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
                        createCancelDialog(updateTable());
                    }
                });
        listDlg.setNegativeButton("Cancel", null);
        // 表示
        listDlg.create().show();
    }

    private void createCancelDialog(boolean isSuccessful) {
        // キャンセル確認ダイアログ
        AlertDialog.Builder dlg = new AlertDialog.Builder(this);
        dlg.setTitle("商品のキャンセル");
        if (isSuccessful) {
            dlg.setMessage("キャンセルが完了しました");
            selectProduct.clear();
            startActivity(new Intent(this, ProductView.class));
        }
        else {
            dlg.setMessage("キャンセル出来ませんでした");
        }
        // 表示
        dlg.create().show();

    }

    private boolean updateTable() {
        // 以下をトライザクション処理で行う
        // 一つでもエラーがあると全てロールバックする
        boolean isSuccessful = false;
        SQLiteDatabase dbWrite = myHelper.getWritableDatabase();
        dbWrite.beginTransaction();
        try {
            for (OrderItem item : selectProduct) {
                updateProductListTable(dbWrite, item);
                updateBlackListTable(dbWrite, item);
                updateOrderAfterListTable(dbWrite, item);
            }
            dbWrite.setTransactionSuccessful();
            isSuccessful = true;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            dbWrite.endTransaction();
        }
        selectProduct.clear();
        if (isSuccessful) updateSelectProductList();

        return isSuccessful;
    }

    /**
     * updateProductListTable Method
     * 商品テーブルの在庫数を更新する
     * 商品テーブルの在庫数に注文テーブルの数量を加算する
     * @param item
     */
    private void updateProductListTable(SQLiteDatabase dbWrite, OrderItem item) throws Exception {
        // 商品名、数量を指定
        String[] cols = {
                MyDatabase.ColumnsProducts.PRODUCTNAME,
                MyDatabase.ColumnsProducts.STOCK
        };
        String selection = MyDatabase.ColumnsProducts.PRODUCTNAME + " = ?";
        String[] selectionArgs = {item.productName};
        Cursor cursor = dbWrite.query(MyDatabase.TABLE_NAME_PRODUCTS, cols, selection, selectionArgs, null, null, null);

        // 読み込み位置を先頭にする、falseの場合は結果０件
        if (!cursor.moveToFirst()) {
            cursor.close();
            throw new Exception();
        }

        // 現在の商品テーブルのストックに注文テーブルの数量を加算した値を求める
        int stockIndex = cursor.getColumnIndex(MyDatabase.ColumnsProducts.STOCK);
        int updateStock = cursor.getInt(stockIndex) + item.quantity;

        // Cursorを閉じる
        cursor.close();

        // 列に対応する値をセットする
        ContentValues values = new ContentValues();
        values.put(MyDatabase.ColumnsProducts.STOCK, updateStock);

        // 加算した値をテーブルに反映させる
        long id = dbWrite.update(MyDatabase.TABLE_NAME_PRODUCTS, values, selection, selectionArgs);
        if (id == -1) {
            Log.v("NOW", "行の追加に失敗したよ");
            throw new Exception();
        }
    }

    /**
     * updateBlackListTable Method
     * ブラックリストテーブルの注文合計額を更新をする
     * ブラックリストテーブルの注文合計額から(発注.数量 * 発注.数量)を減算する
     * @param item
     */
    private void updateBlackListTable(SQLiteDatabase dbWrite, OrderItem item) throws Exception {
        // メールアドレス、注文合計額を指定
        String[] cols = {
                MyDatabase.ColumnsBlackList.MAILADDRESS,
                MyDatabase.ColumnsBlackList.TOTALORDER
        };
        String selection = MyDatabase.ColumnsBlackList.MAILADDRESS + " = ?";
        // アカウントメールアドレスを取得する
        SharedPreferences data = getSharedPreferences("Maildata", Context.MODE_PRIVATE);
        String mailAddr = data.getString("Mailsave", "failed.com");
        String[] selectionArgs = {mailAddr};
        Cursor cursor = dbWrite.query(MyDatabase.TABLE_NAME_BLACK_LIST, cols, selection, selectionArgs, null, null, null);

        // 3. 読み込み位置を先頭にする、falseの場合は結果０件
        if (!cursor.moveToFirst()) {
            cursor.close();
            throw new Exception();
        }

        // ブラックリストテーブルの注文合計額から(発注.数量 * 発注.数量)を減算した値を求める
        int totalorderIndex = cursor.getColumnIndex(MyDatabase.ColumnsBlackList.TOTALORDER);
        int updateTotalOrder = cursor.getInt(totalorderIndex) - (item.price * item.quantity);

        // 6. Cursorを閉じる
        cursor.close();

        // 列に対応する値をセットする
        ContentValues values = new ContentValues();
        values.put(MyDatabase.ColumnsBlackList.TOTALORDER, updateTotalOrder);

        // 加算した値をテーブルに反映させる
        long id = dbWrite.update(MyDatabase.TABLE_NAME_BLACK_LIST, values, selection, selectionArgs);
        if (id == -1) {
            Log.v("CHECK_ORDER", "行の追加に失敗したよ");
            throw new Exception();
        }
    }

    /**
     * updateOrderAfterListTable Method
     * 注文テーブルからチェックリストのレコードを削除する
     */
    private void updateOrderAfterListTable(SQLiteDatabase dbWrite, OrderItem item) throws Exception {
        // データベースから発注IDが同じレコードを削除する
        String whereClause = MyDatabase.ColumnsOrderAfter.ORDERID + " = ?";
        String whereArgs[] = {String.valueOf(item.orderId)};
        long id = dbWrite.delete(MyDatabase.TABLE_NAME_ORDERAFTER, whereClause, whereArgs);
        if (id == -1) {
            Log.d("CHECK_ORDER", "レコードの削除に失敗したよ");
            throw new Exception();
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
                MyDatabase.ColumnsOrderAfter.MAILADDRESS,
                MyDatabase.ColumnsOrderAfter.PRODUCTNAME,
                MyDatabase.ColumnsOrderAfter.PRICE,
                MyDatabase.ColumnsOrderAfter.QUANTITY,
                MyDatabase.ColumnsOrderAfter.ORDERID
        };
        // 発注者のメールアドレスとログインアドレスが同じレコードのみを指定
        String selection = MyDatabase.ColumnsOrderAfter.MAILADDRESS + " = ?";
        SharedPreferences data = getSharedPreferences("Maildata", Context.MODE_PRIVATE);
        String mailAddr = data.getString("Mailsave", "");
        Log.d("NOW", "cancel" + mailAddr);
        String[] selectionArgs = {mailAddr};
        Cursor cursor = db.query(MyDatabase.TABLE_NAME_ORDERAFTER, cols, selection, selectionArgs, null, null,
                MyDatabase.ColumnsOrder.ORDERID + " ASC");

        // 3. 読み込み位置を先頭にする、falseの場合は結果０件
        if (!cursor.moveToFirst()) {
            Log.d("NOW", "cancel 0件");
            cursor.close();
            db.close();
            return;
        }

        // 4. 列のindex(位置)を取得する
        int productnameIndex = cursor.getColumnIndex(MyDatabase.ColumnsOrder.PRODUCTNAME);
        int priceIndex = cursor.getColumnIndex(MyDatabase.ColumnsOrder.PRICE);
        int quantityIndex = cursor.getColumnIndex(MyDatabase.ColumnsOrder.QUANTITY);
        int orderIdIndex = cursor.getColumnIndex(MyDatabase.ColumnsOrder.ORDERID);

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
