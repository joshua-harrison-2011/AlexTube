package com.babablankie.alextube;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ChannelListViewAdapter extends RecyclerView.Adapter<ChannelListViewAdapter.ChannelItemViewHolder> {
    private ArrayList<ChannelItem> channelList;

    public ChannelListViewAdapter(ArrayList<ChannelItem> channelList) {
        this.channelList = channelList;
    }

    @Override
    public ChannelItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.channel_item, parent, false);
        ChannelItemViewHolder videoItemViewHolder = new ChannelItemViewHolder(view);
        return videoItemViewHolder;
    }

    @Override
    public void onBindViewHolder(ChannelItemViewHolder holder, int position) {
        holder.setChannelItem(channelList.get(position));
        Picasso.get().load(channelList.get(position).getThumbnailURL()).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return channelList.size();
    }

    public static class ChannelItemViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        ImageView thumbnail;
        ChannelItem channelItem;

        public ChannelItemViewHolder(View itemView) {
            super(itemView);
            thumbnail = (ImageView)itemView.findViewById(R.id.channelThumbnail);
            itemView.setOnClickListener(this);
        }

        public void setChannelItem(ChannelItem channelItem) {
            this.channelItem = channelItem;
        }

        @Override
        public void onClick(View v) {
            MainActivity activity = (MainActivity)v.getContext();
            activity.onChannelSelected(channelItem);
        }
    }


}