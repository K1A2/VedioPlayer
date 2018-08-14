package com.contast.k1a2.vedioplayer.layout;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.contast.k1a2.vedioplayer.R;
import com.contast.k1a2.vedioplayer.View.RecyclerView.FileExplorerItem;
import com.contast.k1a2.vedioplayer.View.RecyclerView.FileExpolorerAdapter;
import com.contast.k1a2.vedioplayer.View.RecyclerView.onFileClickListener;

import java.io.File;
import java.sql.Array;
import java.util.Arrays;
import java.util.Comparator;

public class FileExplorerActivity extends Activity {

    private RecyclerView recycler_file;
    private TextView text_Path;

    private FileExpolorerAdapter expolorerAdapter;
    private String root;

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

        expolorerAdapter = new FileExpolorerAdapter();

        root = Environment.getExternalStorageDirectory().getAbsolutePath();

        recycler_file.setAdapter(expolorerAdapter);

        setFile(root);

        recycler_file.addOnItemTouchListener(new onFileClickListener(this, recycler_file, new onFileClickListener.OnItemClickListener() {
            @Override
            public void onItemClicked(View view, int position) {
                FileExplorerItem fileExplorerItem = expolorerAdapter.getItem(position);
                String name = fileExplorerItem.getName();
                String path = fileExplorerItem.getPath();

                File f = new File(path);
                if (f.isDirectory()) {
                    setFile(path);
                } else if (name.equals("../")) {
                    setFile(f.getParentFile().getAbsolutePath());
                } else {
                    if (f.getName().endsWith(".mp4")||f.getName().endsWith(".hmp4")) {
                        Intent i = new Intent();
                        i.putExtra(ActivityKey.INTENT_FILE_NAME, name);
                        i.putExtra(ActivityKey.INTENT_FILE_PATH, path);
                        setResult(RESULT_OK, i);
                        finish();
                    } else {
                        Toast.makeText(FileExplorerActivity.this, "지원하지 않는 코덱이거나 동영상이 아닙니다", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onLongItemClicked(View view, int position) {

            }
        }));
    }

    private void setFile(String path) {
        text_Path.setText(path);

        File[] list_file = new File(path).listFiles();

        FileExplorerItem fileExplorerItem = new FileExplorerItem();

        if (!path.equals(root)) {
            fileExplorerItem.setDrawable(ContextCompat.getDrawable(this, R.drawable.blank));
            fileExplorerItem.setName("../");
            fileExplorerItem.setPath("");
            expolorerAdapter.addItem(new FileExplorerItem());
        }

        Arrays.sort(list_file, new Comparator<File>() {
            @Override
            public int compare(File file, File t1) {
                return file.getName().compareToIgnoreCase(t1.getName());
            }
        });

        for (File f:list_file) {
            if (f.isDirectory()) {
                fileExplorerItem.setDrawable(ContextCompat.getDrawable(this, R.drawable.outline_folder_open_black_48));
                fileExplorerItem.setName(f.getName());
                fileExplorerItem.setPath(f.getAbsolutePath());
                expolorerAdapter.addItem(fileExplorerItem);
            } else {
                if (f.getName().endsWith(".mp4")||f.getName().endsWith(".hmp4")) {
                    fileExplorerItem.setDrawable(ContextCompat.getDrawable(this, R.drawable.outline_movie_creation_black_48));
                    fileExplorerItem.setName(f.getName());
                    fileExplorerItem.setPath(f.getAbsolutePath());
                    expolorerAdapter.addItem(fileExplorerItem);
                } else {
                    fileExplorerItem.setDrawable(ContextCompat.getDrawable(this, R.drawable.outline_insert_drive_file_black_48));
                    fileExplorerItem.setName(f.getName());
                    fileExplorerItem.setPath(f.getAbsolutePath());
                    expolorerAdapter.addItem(fileExplorerItem);
                }
            }
        }
    }
}
