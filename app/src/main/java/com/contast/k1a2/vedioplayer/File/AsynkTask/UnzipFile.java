package com.contast.k1a2.vedioplayer.File.AsynkTask;

import android.content.Context;
import android.os.AsyncTask;

public class UnzipFile extends AsyncTask<String, String, String> {

    private Context context;

    public UnzipFile (Context context) {
        this.context = context;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... strings) {
        return null;
    }

    @Override
    protected void onProgressUpdate(String... values) {

    }

    @Override
    protected void onPostExecute(String s) {

    }
}
