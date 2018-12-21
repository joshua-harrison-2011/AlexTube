package com.babablankie.alextube;

import android.util.Log;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.youtube.YouTube;
import com.google.api.services.youtube.model.Channel;
import com.google.api.services.youtube.model.ChannelListResponse;
import com.google.api.services.youtube.model.SearchListResponse;
import com.google.api.services.youtube.model.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

// Class to proxy communication to the YouTube API
class YoutubeConnector {
    private YouTube youtube;
    private YouTube.Search.List query;

    private String googleDeveloperAPIKey;

    YoutubeConnector(String googleDeveloperAPIKey) {
        this.googleDeveloperAPIKey = googleDeveloperAPIKey;

        youtube = new YouTube.Builder(new NetHttpTransport(),
                new JacksonFactory(), new HttpRequestInitializer() {
            @Override
            public void initialize(HttpRequest hr) throws IOException {}
        }).setApplicationName("AlexTube").build();
    }

    // Fetches the first 50 videos for a given channel
    ArrayList<VideoItem> searchVideos(String channelId){
        ArrayList<VideoItem> items = new ArrayList<>();
        try{
            query = youtube.search().list("id,snippet");
            query.setKey(googleDeveloperAPIKey);
            query.setType("video");
            query.setFields("items(id/videoId,snippet/title,snippet/thumbnails/high)");
            query.setMaxResults(50L);
            query.setChannelId(channelId);
            query.setOrder("viewCount");

            SearchListResponse response = query.execute();
            List<SearchResult> results = response.getItems();

            for(SearchResult result:results) {
                VideoItem item = new VideoItem();
                item.setTitle(result.getSnippet().getTitle());
                item.setThumbnailURL(result.getSnippet().getThumbnails().getHigh().getUrl());
                item.setId(result.getId().getVideoId());
                items.add(item);
            }
        } catch(IOException e) {
            Log.d("YC", "Could not search videos: "+e);
        }
        return items;
    }

    // Fetches videos related to videoId.  It appears channelId isn't respected by the API.
    // This method is currently not used.
    ArrayList<VideoItem> searchRelatedVideos(String videoId, ArrayList<String> channelIds) {
        ArrayList<VideoItem> items = new ArrayList<>();
        try {
            query = youtube.search().list("id,snippet");
            query.setKey(googleDeveloperAPIKey);
            query.setType("video");
            query.setMaxResults(50L);
            query.setRelatedToVideoId(videoId);

            SearchListResponse response = query.execute();
            List<SearchResult> results = response.getItems();

            for(SearchResult result:results){
                if (channelIds.contains(result.getSnippet().getChannelId())) {
                    VideoItem item = new VideoItem();
                    item.setTitle(result.getSnippet().getTitle());
                    item.setThumbnailURL(result.getSnippet().getThumbnails().getHigh().getUrl());
                    item.setId(result.getId().getVideoId());
                    items.add(item);
                }
            }
        } catch(IOException e) {
            Log.d("YC", "Could not search related videos: "+e);
        }
        return items;
    }

    // Fetch channels based on a list of channel ids
    ArrayList<ChannelItem> searchChannels(String channelIds) {
        ArrayList<ChannelItem> items = new ArrayList<>();
        try {
            YouTube.Channels.List query = youtube.channels().list("snippet");
            query.setId(channelIds);
            query.setKey(googleDeveloperAPIKey);
            ChannelListResponse response = query.execute();
            List<Channel> results = response.getItems();

            for(Channel result:results) {
                ChannelItem channel = new ChannelItem();
                channel.setId(result.getId());
                channel.setThumbnailURL(result.getSnippet().getThumbnails().getDefault().getUrl());
                items.add(channel);
            }
        } catch(IOException e) {
            Log.d("YC", "Could not search channels: "+e);
        }
        return items;
    }
}
