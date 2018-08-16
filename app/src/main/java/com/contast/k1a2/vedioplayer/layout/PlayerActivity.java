package com.contast.k1a2.vedioplayer.layout;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.contast.k1a2.vedioplayer.R;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class PlayerActivity extends Activity {

    private VideoView videoView;
    private File vedio = null, xml = null;
    private String name, path;
    private int ratio;
    private LinearLayout lenar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_player);

        getActionBar().hide();
        Intent i = getIntent();

        name = i.getStringExtra(ActivityKey.INTENT_FILE_NAME);
        path = i.getStringExtra(ActivityKey.INTENT_FILE_PATH);

        if (!i.getBooleanExtra(ActivityKey.IS_MP4, false)) {
            File[] video_list = new File(path).listFiles();

            for (File f:video_list) {
                if (f.getName().endsWith(".mp4")) {
                    if (vedio == null) vedio = f;
                } else if (f.getName().endsWith(".xml")) {
                    if (xml == null) xml = f;
                    new xmlParshing(this).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, xml);
                }
            }
        } else {
            vedio = new File(path);
        }

        lenar = (LinearLayout) findViewById(R.id.absolute_box);
        videoView = (VideoView) findViewById(R.id.video_play);

        final MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);

        videoView.setVideoPath(vedio.getAbsolutePath());
        videoView.requestFocus();

        setInfoLayout();
        videoView.start();
    }

    //화면회전 감지
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            setInfoLayout();
        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            setInfoLayout();
        }
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                PlayerActivity.this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
            }
        }, 1000);
    }

    //정보표시 레이아웃 크기 설정
    private void setInfoLayout() {
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams1.addRule(RelativeLayout.CENTER_IN_PARENT);
        lenar.setLayoutParams(layoutParams1);

        ViewTreeObserver viewTreeObserver = lenar.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(vedio.getAbsolutePath());
                final Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime();

                int rito[] = reduceFraction(bitmap.getWidth(), bitmap.getHeight());

                RelativeLayout.LayoutParams layoutParams;
                int h = lenar.getHeight();
                int w = lenar.getWidth();
                int rito_h = lenar.getHeight() / rito[1];
                layoutParams = new RelativeLayout.LayoutParams(rito[0] * rito_h , lenar.getHeight());
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                lenar.setLayoutParams(layoutParams);

                ViewTreeObserver treeObserver = lenar.getViewTreeObserver();
                treeObserver.removeOnGlobalLayoutListener(this);
            }
        });
    }

    //기약분수 함수(화면비 구할떄 사용)
    private int[] reduceFraction(int bunja, int bunmo) {
        int[] frac = new int[2];
        frac[0] = bunja;
        frac[1] = bunmo;

        if (frac[1] == 0) { // 분모가 0일 경우에 에러 반환
            frac[0] = 0;
            frac[1] = 0;
            return frac;
        }

        int gcd_result = gcd(frac[0], frac[1]);

        frac[0] = frac[0] / gcd_result;
        frac[1] = frac[1] / gcd_result;

        return frac;
    }

    // 최대 공약수 계산 메서드
    private int gcd(int a, int b) {

        while (b != 0) {
            int temp = a % b;
            a = b;
            b = temp;
        }

        return Math.abs(a);
    }


    //정보분석 백그라운드
    private class xmlParshing extends AsyncTask<Object, String, ArrayList<String[]>> {

        private Context context;
        private String xml;
        private ProgressDialog progressDialog;


        public xmlParshing(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            progressDialog = new ProgressDialog(context);
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progressDialog.setCancelable(false);
            progressDialog.setCanceledOnTouchOutside(false);
            progressDialog.setTitle("정보파일 분석중..");
            progressDialog.setMessage("분석중..");
            progressDialog.show();
        }

        @Override
        protected ArrayList<String[]> doInBackground(Object... objects) {
            xml = getXml((File) objects[0]);
            if (xml == null) {
                return null;
            }
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            try {
                DocumentBuilder builder = factory.newDocumentBuilder();
                InputStream inputStream = new ByteArrayInputStream(xml.getBytes("utf-8"));
                Document doc = builder.parse(inputStream);

                Element item = doc.getDocumentElement();

                //하이퍼링크
                NodeList hyperlink = item.getElementsByTagName("hyperlink");

                ArrayList<String[]> arrayList = new ArrayList<String[]>();

                //attr(시간)
                String name = "", v = "", link = "";
                String[] type = new String[2];

                for (int i = 0;i < hyperlink.getLength();i++) {
                    Node hyper = hyperlink.item(i);
                    Node text = hyper.getFirstChild();
                    link = text.getNodeValue();

                    NamedNodeMap attrs = hyper.getAttributes();
                    for (int j = 0; j < attrs.getLength();j++) {
                        Node attr =  attrs.item(j);
                        name = attr.getNodeName();
                        v = attr.getNodeValue();
                        if (name.equals("start")) {
                            String[] split = v.split(":");
                            int h = Integer.parseInt(split[0]) * 3600000;
                            int m = Integer.parseInt(split[1]) * 60000;
                            int s = Integer.parseInt(split[2]) * 1000;
                            type[0] = String.valueOf(h+m+s);
                        } else if (name.equals("for")) {
                            String[] split = v.split(":");
                            int h = Integer.parseInt(split[0]) * 3600000;
                            int m = Integer.parseInt(split[1]) * 60000;
                            int s = Integer.parseInt(split[2]) * 1000;
                            type[1] = String.valueOf(h+m+s);
                        }
                    }
                    arrayList.add(new String[] {"hyperlink", link, type[0], type[1]});//type link time duration
                }

                String el = "";
                String[] p = new String[3];
                //인물
                NodeList peoples = item.getElementsByTagName("people");

                for (int i = 0;i < peoples.getLength();i++) {
                    Node people = peoples.item(i);
                    NodeList list = people.getChildNodes();
                    for (int j = 0;j < list.getLength();j++) {
                        Node inform = list.item(j);

                        el = inform.getNodeName();
                        if (el.equals("name")) {
                            p[0] = inform.getNodeValue();
                        } else if (el.equals("job")) {
                            p[1] = inform.getNodeValue();
                        } else if (el.equals("born")) {
                            p[2] = inform.getNodeValue();
                        }
                    }

                    NamedNodeMap attrs = people.getAttributes();
                    for (int j = 0; j < attrs.getLength();j++) {
                        Node attr =  attrs.item(j);
                        name = attr.getNodeName();
                        v = attr.getNodeValue();
                        if (name.equals("start")) {
                            String[] split = v.split(":");
                            int h = Integer.parseInt(split[0]) * 3600000;
                            int m = Integer.parseInt(split[1]) * 60000;
                            int s = Integer.parseInt(split[2]) * 1000;
                            type[0] = String.valueOf(h+m+s);
                        } else if (name.equals("for")) {
                            String[] split = v.split(":");
                            int h = Integer.parseInt(split[0]) * 3600000;
                            int m = Integer.parseInt(split[1]) * 60000;
                            int s = Integer.parseInt(split[2]) * 1000;
                            type[1] = String.valueOf(h+m+s);
                        }
                    }
                    arrayList.add(new String[] {"people", p[0], p[1], p[2], type[0], type[1]});//type name job bor time duration
                }

                return arrayList;
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
                return null;
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return null;
            } catch (SAXException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onProgressUpdate(String... values) {

        }

        @Override
        protected void onPostExecute(ArrayList<String[]> s) {
            progressDialog.dismiss();
            if (s != null) {
                new showBox().executeOnExecutor(THREAD_POOL_EXECUTOR, s);
            } else {
                Toast.makeText(PlayerActivity.this, "정보파일 분석 실패", Toast.LENGTH_LONG).show();
            }
        }

        private String getXml(File file) {
            StringBuffer strBuffer = new StringBuffer();
            try{
                InputStream is = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                String line="";
                while((line=reader.readLine())!=null){
                    strBuffer.append(line+"\n");
                }

                reader.close();
                is.close();
            }catch (IOException e){
                e.printStackTrace();
                return null;
            }
            return strBuffer.toString();
        }
    }


    //정보표시 백그라운드
    private class showBox extends AsyncTask<Object, String ,String> {

        boolean[] isShowing;
        boolean isPlaying = false;

        @Override
        protected String doInBackground(Object... objects) {
            ArrayList<String[]> arrayList = (ArrayList<String[]>) objects[0];

            isShowing = new boolean[arrayList.size()];

            for (int i = 0;i < isShowing.length;i++) {
                isShowing[i] = false;
            }

            while (true) {
                synchronized (this) {
                    try {
                        wait(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                isPlaying = videoView.isPlaying();
                if (!isPlaying) {
                    continue;
                }
                for (int i = 0;i < arrayList.size();i++) {
                    String s[] = arrayList.get(i);
                    //현재시간 +-250 == 지정시간
                    if ((videoView.getCurrentPosition() + 250 >= Integer.parseInt(s[2])&&Integer.parseInt(s[2]) >= videoView.getCurrentPosition() - 250)&&!isShowing[i]) {
                        isShowing[i] = true;
                        publishProgress(s[0], s[1], s[3], String.valueOf(i));
                    }
                }
            }
        }

        @Override
        protected void onProgressUpdate(final String... values) {
            if (values[0].equals("hyperlink")) {
                final View v = getLayoutInflater().inflate(R.layout.view_hyperlink, null, false);
                TextView t = (TextView) v.findViewById(R.id.box_hyperlink);
                t.setText(values[1]);
                lenar.addView(v);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                v.setLayoutParams(layoutParams);

//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                lenar.removeView(v);
//                            }
//                        });
//                        isShowing[Integer.parseInt(values[3])] = false;
//                    }
//                }, Integer.parseInt(values[2]));

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        int count = 0;
                        while (count < Integer.parseInt(values[2])) {
                            if (isPlaying) {
                                count = count + 500;
                            }
                            synchronized (this) {
                                try {
                                    wait(500);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                        }

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                lenar.removeView(v);
                            }
                        });
                        isShowing[Integer.parseInt(values[3])] = false;
                    }
                }).start();
            }
        }
    }
}
