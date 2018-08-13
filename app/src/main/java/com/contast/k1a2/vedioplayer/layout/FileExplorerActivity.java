package com.contast.k1a2.vedioplayer.layout;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.contast.k1a2.vedioplayer.R;
import com.contast.k1a2.vedioplayer.View.RecyclerView.FileExplorerItem;
import com.contast.k1a2.vedioplayer.View.RecyclerView.FileExpolorerAdapter;

public class FileExplorerActivity extends Activity {

    private RecyclerView recycler_file;
    private TextView text_Path;

    private FileExpolorerAdapter expolorerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fileex);
        //액션바 숨김
        getActionBar().hide();

        recycler_file = (RecyclerView) findViewById(R.id.recycler_file_list);
        text_Path = (TextView) findViewById(R.id.text_path);


        recycler_file.setLayoutManager(new LinearLayoutManager(this));
        recycler_file.setItemAnimator(new DefaultItemAnimator());

        text_Path.setText("dd");

        expolorerAdapter = new FileExpolorerAdapter();

        FileExplorerItem fileExplorerItem = new FileExplorerItem();
        fileExplorerItem.setDrawable(ContextCompat.getDrawable(this, R.drawable.outline_folder_open_black_48));
        fileExplorerItem.setName("Test");
        fileExplorerItem.setPath("fbisfbrnvgwghwuohvsvnsbvgvusbvhdbihihv");

        expolorerAdapter.addItem(fileExplorerItem);
        expolorerAdapter.addItem(fileExplorerItem);
        expolorerAdapter.addItem(fileExplorerItem);
        expolorerAdapter.addItem(fileExplorerItem);
        expolorerAdapter.addItem(fileExplorerItem);
        expolorerAdapter.addItem(fileExplorerItem);
        expolorerAdapter.addItem(fileExplorerItem);
        expolorerAdapter.addItem(fileExplorerItem);
        expolorerAdapter.addItem(fileExplorerItem);
        expolorerAdapter.addItem(fileExplorerItem);
        expolorerAdapter.addItem(fileExplorerItem);
        expolorerAdapter.addItem(fileExplorerItem);
        expolorerAdapter.addItem(fileExplorerItem);
        expolorerAdapter.addItem(fileExplorerItem);
        expolorerAdapter.addItem(fileExplorerItem);
        expolorerAdapter.addItem(fileExplorerItem);
        expolorerAdapter.addItem(fileExplorerItem);
        expolorerAdapter.addItem(fileExplorerItem);

        recycler_file.setAdapter(expolorerAdapter);
    }
}
