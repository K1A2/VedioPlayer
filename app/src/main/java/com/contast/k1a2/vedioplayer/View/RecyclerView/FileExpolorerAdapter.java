package com.contast.k1a2.vedioplayer.View.RecyclerView;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.contast.k1a2.vedioplayer.R;

import java.util.ArrayList;
import java.util.List;

public class FileExpolorerAdapter extends RecyclerView.Adapter<FileExpolorerAdapter.ViewHolder> {

    private List<FileExplorerItem> fileExplorerItems;

    public FileExpolorerAdapter () {
        fileExplorerItems = new ArrayList<FileExplorerItem>();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_filelist, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        FileExplorerItem item = fileExplorerItems.get(position);
        holder.imageType.setImageDrawable(item.getDrawable());
        holder.textName.setText(item.getName());
        holder.textPath.setText(item.getPath());
    }

    @Override
    public int getItemCount() {
        return fileExplorerItems.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imageType;
        public TextView textName;
        public TextView textPath;

        public ViewHolder (View view) {
            super(view);

            imageType = (ImageView) view.findViewById(R.id.image_type);
            textName = (TextView) view.findViewById(R.id.text_file_name);
            textPath = (TextView) view.findViewById(R.id.text_file_path);
        }
    }

    public void removeItem(int position) {
        try {
            fileExplorerItems.remove(position);
            notifyItemRemoved(position);
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }
    }

    public void addItem(FileExplorerItem item) {
        fileExplorerItems.add(item);
        notifyItemInserted(fileExplorerItems.size());
    }

    public FileExplorerItem getItem(int position) {
        return fileExplorerItems.get(position);
    }

    public void clearItem() {
        final int count = fileExplorerItems.size();
        fileExplorerItems.clear();
        notifyItemRangeRemoved(0, count);
    }
}
