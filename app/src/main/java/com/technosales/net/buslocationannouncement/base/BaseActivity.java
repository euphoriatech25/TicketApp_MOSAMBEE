package com.technosales.net.buslocationannouncement.base;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import com.technosales.net.buslocationannouncement.R;
import com.technosales.net.buslocationannouncement.activity.TicketAndTracking;

public abstract class BaseActivity extends AppCompatActivity{
    private Toolbar mToolbar;
    private boolean  stopThread;
    protected Toolbar setUpToolbar(String title, boolean enableBack) {
        if (mToolbar == null) {
            mToolbar = findViewById(R.id.toolbar);
            TextView tvTitle = mToolbar.findViewById(R.id.tvToolbarTitle);
            if (mToolbar != null) {
                tvTitle.setText(title);
                setSupportActionBar(mToolbar);
                getSupportActionBar().setDisplayShowTitleEnabled(false);
                if (enableBack) {
                    mToolbar.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
                }
            }
        }
        return mToolbar;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                stopThread=true;
                Intent intent = new Intent(this, TicketAndTracking.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}