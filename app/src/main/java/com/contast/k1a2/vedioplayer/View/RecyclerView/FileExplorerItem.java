package com.contast.k1a2.vedioplayer.View.RecyclerView;

import android.graphics.drawable.Drawable;

public class FileExplorerItem {

    private Drawable drawable;
    private String name;
    private String path;

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}
