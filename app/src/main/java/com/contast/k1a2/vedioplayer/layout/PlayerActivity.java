package com.contast.k1a2.vedioplayer.layout;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.contast.k1a2.vedioplayer.R;

public class PlayerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getActionBar().hide();
    }
}
