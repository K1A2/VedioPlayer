package com.contast.k1a2.vedioplayer.layout;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.contast.k1a2.vedioplayer.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startEx();
                } else {
                    Toast.makeText(MainActivity.this, "이 앱을 사용하실수 없습니다", Toast.LENGTH_SHORT).show();//
                    finish();
                }
                return;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case ActivityKey.REQUEST_CODE_FILEEX:
                    //파일 익스플로러 결과값 반환
                    String name = data.getStringExtra(ActivityKey.INTENT_FILE_NAME);
                    String path = data.getStringExtra(ActivityKey.INTENT_FILE_PATH);
                    if (name.endsWith(".mp4")) {
                        Intent i = new Intent(MainActivity.this, PlayerActivity.class);
                        i.putExtra(ActivityKey.INTENT_FILE_NAME, data.getStringExtra(ActivityKey.INTENT_FILE_NAME));
                        i.putExtra(ActivityKey.INTENT_FILE_PATH, data.getStringExtra(ActivityKey.INTENT_FILE_PATH));
                        i.putExtra(ActivityKey.IS_MP4, true);
                        startActivity(i);
                        finish();
                    } else if (name.endsWith(".hmp4")) {
                        new UnzipFile(MainActivity.this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, name, path);
                    }
                    break;
            }
        } else {
            //뒤로가기, 취소했울때
        }
    }
    public void finishUnzip(Object[] result) {
        if ((boolean) result[0]) {
            Intent i = new Intent(MainActivity.this, PlayerActivity.class);
            i.putExtra(ActivityKey.INTENT_FILE_NAME, (String) result[2]);
            i.putExtra(ActivityKey.INTENT_FILE_PATH, (String) result[1]);
            i.putExtra(ActivityKey.IS_MP4, false);
            startActivity(i);
            finish();
        } else {
            Toast.makeText(this, "실패", Toast.LENGTH_SHORT).show();
        }
    }

    public class UnzipFile extends AsyncTask<String, String, Object[]> {

        private Context context;
        private ProgressDialog progressDialog;
        private String name, path, target, finish;

        public UnzipFile (Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
        }

        @Override
        protected Object[] doInBackground(String... strings) {
            name = strings[0];
            path = strings[1];
            target = Environment.getExternalStorageDirectory().getAbsolutePath() + "/HVideoPlayer/" + name;
            new File(target).mkdirs();

            publishProgress("initial", name);

            try {
                FileInputStream fileInputStream = new FileInputStream(path);
                ZipInputStream zipInputStream = new ZipInputStream(fileInputStream);
                ZipEntry zipEntry = null;

                File targetFile = null;
                while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                    String filenameTounzip = zipEntry.getName();
                    targetFile = new File(target, filenameTounzip);

                    if (zipEntry.isDirectory()) {
                        File pathF = new File(targetFile.getAbsolutePath());
                        pathF.mkdirs();
                    } else {
                        File pathF = new File(targetFile.getParent());
                        pathF.mkdirs();
                        Unzip(zipInputStream, targetFile);
                    }
                }

                fileInputStream.close();
                zipInputStream.close();
                if (targetFile != null) finish = targetFile.getAbsolutePath();
                return new Object[] {true, target, name};
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                publishProgress(e.getMessage());
                return new Object[] {false};
            } catch (IOException e) {
                e.printStackTrace();
                publishProgress(e.getMessage());
                return new Object[] {false};
            } catch (Exception e) {
                e.printStackTrace();
                publishProgress(e.getMessage());
                return new Object[] {false};
            }
        }


        @Override
        protected void onProgressUpdate(String... values) {
            if (values[0].equals("initial")) {
                progressDialog.setTitle(String.format("%s 읽는중..", values[1]));
                progressDialog.show();
            } else {

            }
        }

        @Override
        protected void onPostExecute(Object[] s) {
            progressDialog.dismiss();
            finishUnzip(s);
        }

        private File Unzip(ZipInputStream zipInputStream, File targetFile) throws IOException {
            FileOutputStream fileOutputStream = null;

            final int BUFFER_SIZE = 1024 * 2;

            try {
                fileOutputStream = new FileOutputStream(targetFile);

                byte[] buffer = new byte[BUFFER_SIZE];
                int len = 0;
                try {
                    while ((len = zipInputStream.read(buffer)) != -1) {
                        fileOutputStream.write(buffer, 0, len);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    fileOutputStream.close();
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            return targetFile;
        }
    }
}
