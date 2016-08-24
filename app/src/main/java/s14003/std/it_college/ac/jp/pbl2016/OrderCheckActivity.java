package s14003.std.it_college.ac.jp.pbl2016;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

import s14003.std.it_college.ac.jp.pbl2016.Product.MyHelper;
import s14003.std.it_college.ac.jp.pbl2016.Product.ProductView;

public class OrderCheckActivity extends AppCompatActivity {

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

    private MyHelper myHelper = new MyHelper(this);
    private List<OrderItem> orderList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_check);

        //TODO: 注文DBのデータを読み込む
        loadOrderList();

        //TODO: 発注個数をリストに追加する
        for (OrderItem item : orderList) {
            item.quantity = 10;
        }

        // 発注確定ボタン
        Button btnConfirm = (Button) findViewById(R.id.btn_confirm);
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: 注文DBのデータを発注DBに登録
                for (OrderItem item : orderList) {
                    Log.d("OrderCheck", item.toString());
                    StringBuilder str = new StringBuilder();
                    str.append("orderId:" + item.orderId);
                    str.append("mailAddress:" + item.mailAddress + " ");
                    str.append("productName:" + item.productName + " ");
                    str.append("quantity:" + item.quantity + " ");
                    str.append("price:" + item.price + " ");
                    str.append("productId:" + item.productId);
                    Log.d("OrderCheck", str.toString());
                    insertRecord(item);
                }

                //TODO: 注文DBをクリア
                deleteAllRecord();

                // 注文確認画面へ繊遷移
                changeActivity();
            }
        });
    }

    private void changeActivity() {
        Intent intent = new Intent(this, ProductView.class);
        startActivity(intent);
    }

    /**
     * insertRecord Method
     * 注文テーブルにレコードを追加する
     */
    private boolean insertRecord(OrderItem item) {
        Log.d("insertRecord", "ins oId" + String.valueOf(item.orderId));
        SQLiteDatabase db = myHelper.getWritableDatabase();

        // 列に対応する値をセットする
        ContentValues values = new ContentValues();
        values.put(MyHelper.ColumnsOrderAfter.MAILADDRESS, item.mailAddress);
        values.put(MyHelper.ColumnsOrderAfter.PRODUCTNAME, item.productName);
        values.put(MyHelper.ColumnsOrderAfter.QUANTITY, item.quantity);
        values.put(MyHelper.ColumnsOrderAfter.PRICE, item.price);
        values.put(MyHelper.ColumnsOrderAfter.PRODUCTID, item.productId);

        // データベースに行を追加する
        long id = db.insert(MyHelper.TABLE_NAME_ORDER_AFTER, null, values);
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
     * loadOrderList Method
     * 注文DBからデータを読み込んで保存する
     * DEST orderList
     */
    private void loadOrderList() {
        SQLiteDatabase db = myHelper.getReadableDatabase();

        Cursor cursor = db.query(MyHelper.TABLE_NAME_ORDER, null, null, null, null, null,
                MyHelper.ColumnsOrder.ORDERID + " ASC");

        if (!cursor.moveToFirst()) {
            cursor.close();
            db.close();
            return;
        }

        // 注文リストの一時データを初期化する
        this.orderList.removeAll(this.orderList);

        int orderIdIndex = cursor.getColumnIndex(MyHelper.ColumnsOrder.ORDERID);
        int mailAddressIndex = cursor.getColumnIndex(MyHelper.ColumnsOrder.MAILADDRESS);
        int productNameIndex = cursor.getColumnIndex(MyHelper.ColumnsOrder.PRODUCTNAME);
        int quantityIndex = cursor.getColumnIndex(MyHelper.ColumnsOrder.QUANTITY);
        int priceIndex = cursor.getColumnIndex(MyHelper.ColumnsOrder.PRICE);
        int productIdIndex = cursor.getColumnIndex(MyHelper.ColumnsOrder.PRODUCTID);

        do {
            OrderItem item = new OrderItem();
            item.orderId = cursor.getInt(orderIdIndex);
            item.mailAddress = cursor.getString(mailAddressIndex);
            item.productName = cursor.getString(productNameIndex);
            item.quantity = cursor.getInt(quantityIndex);
            item.price = cursor.getInt(priceIndex);
            item.productId = cursor.getInt(productIdIndex);

            this.orderList.add(item);
        } while(cursor.moveToNext());

        cursor.close();

        db.close();
    }

    /**
     * deleteRecord Method
     * 注文DBを空にする
     */
    private void deleteAllRecord() {

    }

}
