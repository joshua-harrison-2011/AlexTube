package com.babablankie.alextube;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class VideoListViewAdapter extends RecyclerView.Adapter<VideoListViewAdapter.VideoItemViewHolder> {
    private ArrayList<VideoItem> videoList;

    public VideoListViewAdapter(ArrayList<VideoItem> videoList) {
        this.videoList = videoList;
    }

    @Override
    public VideoItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.video_item, parent, false);
        VideoItemViewHolder videoItemViewHolder = new VideoItemViewHolder(view);
        return videoItemViewHolder;
    }

    @Override
    public void onBindViewHolder(VideoItemViewHolder holder, int position) {
        holder.setVideoItem(videoList.get(position));
        Picasso.get().load(videoList.get(position).getThumbnailURL()).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public static class VideoItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView thumbnail;
        VideoItem videoItem;

        public VideoItemViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView)itemView.findViewById(R.id.videoThumbnail);
            itemView.setOnClickListener(this);
        }

        public void setVideoItem(VideoItem videoItem) {
            this.videoItem = videoItem;
        }

        @Override
        public void onClick(View v) {
            MainActivity activity = (MainActivity)v.getContext();
            activity.onVideoSelected(videoItem);
        }
    }


}