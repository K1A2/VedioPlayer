package com.contast.k1a2.vedioplayer.layout;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.contast.k1a2.vedioplayer.R;

public class MainActivity extends Activity {

    private Button button_file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //액션바 숨김
        getActionBar().hide();

        button_file = (Button) findViewById(R.id.button_main_file);

        //이 버튼 누르면 파일찾는 액티비티 등장
        button_file.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //파일 익스플로러 실행, 파일 익스플로러에서 결과값 반환
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    int permisionRequest = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
                    int permisionRequest2 = ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE);

                    if (permisionRequest == PackageManager.PERMISSION_DENIED||permisionRequest2 == PackageManager.PERMISSION_DENIED)
                        if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)||shouldShowRequestPermissionRationale(Manifest.permission.READ_EXTERNAL_STORAGE)) {
                            AlertDialog.Builder permissioCheck = new AlertDialog.Builder(MainActivity.this);
                            permissioCheck.setTitle("저장소 읽기/쓰기 권한 허가")
                                    .setMessage("동영상을 읽고, 파일을 읽으려면 저장소 읽기/쓰기 권한을 허가해야합니다")
                                    .setCancelable(false)
                                    .setPositiveButton("동의", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                                            }
                                        }
                                    })
                                    .setNegativeButton("취소", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Toast.makeText(MainActivity.this, "이 앱을 사용하실수 없습니다", Toast.LENGTH_SHORT).show();
                                            finish();
                                        }
                                    })
                                    .create()
                                    .show();
                        } else {
                            ActivityCompat.requestPermissions(MainActivity.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
                        } else {
                        startEx();
                    }
                } else {
                    startEx();
                }
            }
        });
    }

    private void startEx() {
        startActivityForResult(new Intent(MainActivity.this, FileExplorerActivity.class), ActivityKey.REQUEST_CODE_FILEEX);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ActivityKey.REQUEST_CODE_FILEEX:
                    //파일 익스플로러 결과값 반환
                    break;
            }
        } else {
            //뒤로가기, 취소했울때
        }
    }
}
