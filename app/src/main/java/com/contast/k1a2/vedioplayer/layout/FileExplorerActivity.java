package com.contast.k1a2.vedioplayer.layout;

import android.app.Activity;
import android.os.Bundle;

import com.contast.k1a2.vedioplayer.R;

public class FileExplorerActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileex);
        //액션바 숨김
        getActionBar().hide();
    }
}
