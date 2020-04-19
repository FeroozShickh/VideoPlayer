package com.example.videoplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

public class VideoListAdapter extends RecyclerView.Adapter<VideoListAdapter.ViewHolder> {

    private Context context;
    ArrayList<File> videolist = new ArrayList<>();
    VideoThumbnailInterface videoThumbnailInterface;

    // RecyclerView recyclerView;
    public VideoListAdapter(Context context, ArrayList<File> videolist, VideoThumbnailInterface videoThumbnailInterface) {
        this.context = context;
        this.videolist = videolist;
        this.videoThumbnailInterface = videoThumbnailInterface;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.video_row, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        final File file = videolist.get(position);
        holder.textView.setText(videolist.get(position).getName());
        Bitmap bitmapThumnail= ThumbnailUtils.createVideoThumbnail(videolist.get(position).getPath(), MediaStore.Images.Thumbnails.MICRO_KIND);
        holder.imageView.setImageBitmap(bitmapThumnail);
        holder.video_row_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                videoThumbnailInterface.setVideoThubnail(file.getPath());
            }
        });
    }

    @Override
    public int getItemCount() {
        return videolist.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;
        public TextView textView;
        public LinearLayout video_row_layout;
        public ViewHolder(View itemView) {
            super(itemView);
            this.imageView = (ImageView) itemView.findViewById(R.id.image_view);
            this.textView = (TextView) itemView.findViewById(R.id.name);
            this.video_row_layout = itemView.findViewById(R.id.video_row_layout);
        }
    }
}
