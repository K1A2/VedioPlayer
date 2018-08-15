package com.contast.k1a2.vedioplayer.layout;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.contast.k1a2.vedioplayer.R;

import java.io.File;
import java.io.FileFilter;

public class PlayerActivity extends Activity {

    private VideoView videoView;
    private File vedio = null, xml = null;
    private String name, path;
    private int ratio;
    private AbsoluteLayout absoluteLayout;

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
                }
            }
        } else {
            vedio = new File(path);
        }

        absoluteLayout = (AbsoluteLayout)findViewById(R.id.absolute_box);
        videoView = (VideoView) findViewById(R.id.video_play);

        final MediaController mediaController = new MediaController(this);
        videoView.setMediaController(mediaController);

        videoView.setVideoPath(vedio.getAbsolutePath());
        videoView.requestFocus();

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        View view = getLayoutInflater().inflate(R.layout.view_hyperlink, null, false);
        TextView textView = (TextView) view.findViewById(R.id.box_hyperlink);
        textView.setText("https://www.naver.com/");
        absoluteLayout.addView(view);

        setInfoLayout();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setInfoLayout();
//        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
//            setInfoLayout();
//        } else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            setInfoLayout();
//        }
    }

    private void setInfoLayout() {
        RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT , ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams1.addRule(RelativeLayout.CENTER_IN_PARENT);
        absoluteLayout.setLayoutParams(layoutParams1);

        ViewTreeObserver viewTreeObserver = absoluteLayout.getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                MediaMetadataRetriever mediaMetadataRetriever = new MediaMetadataRetriever();
                mediaMetadataRetriever.setDataSource(vedio.getAbsolutePath());
                final Bitmap bitmap = mediaMetadataRetriever.getFrameAtTime();

                int rito[] = reduceFraction(bitmap.getWidth(), bitmap.getHeight());

                Toast.makeText(PlayerActivity.this, "동영상: " + bitmap.getWidth() + "*" + bitmap.getHeight() + "\n비율: " + rito[0] + "*" + rito[1], Toast.LENGTH_LONG).show();
                RelativeLayout.LayoutParams layoutParams;
                int h = absoluteLayout.getHeight();
                int w = absoluteLayout.getWidth();
                int rito_h = absoluteLayout.getHeight() / rito[1];
                layoutParams = new RelativeLayout.LayoutParams(rito[0] * rito_h , absoluteLayout.getHeight());
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                absoluteLayout.setLayoutParams(layoutParams);

                ViewTreeObserver treeObserver = absoluteLayout.getViewTreeObserver();
                treeObserver.removeOnGlobalLayoutListener(this);
            }
        });
    }

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
}
