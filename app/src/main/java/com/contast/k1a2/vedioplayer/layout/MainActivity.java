package com.contast.k1a2.vedioplayer.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
                startActivityForResult(new Intent(MainActivity.this, FileExplorerActivity.class), ActivityKey.REQUEST_CODE_FILEEX);
            }
        });
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
