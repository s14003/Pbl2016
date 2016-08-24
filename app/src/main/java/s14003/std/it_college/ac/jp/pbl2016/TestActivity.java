package s14003.std.it_college.ac.jp.pbl2016;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class TestActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("NOW", "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu m) {
        Log.d("NOW", "onCreateOptionsMenu");
        m.add(Menu.NONE, 0, Menu.NONE, "Menu1");
        m.add(Menu.NONE, 0, Menu.NONE, "Menu2");
        return super.onCreateOptionsMenu(m);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem mi) {
        Log.d("NOW", "onOptionsItemSelected");
        switch(mi.getItemId()) {
            case 0:
                //TODO: move to each activities.
                return true;
            default:
                // failed
                return false;
        }
    }
}
