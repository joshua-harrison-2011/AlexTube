package com.babablankie.alextube;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Handler provides mechanism to run something in the UI thread via a callback
    final Handler uiThreadCallbackHandler = new Handler();

    RecyclerView channelListView;
    RecyclerView videoListView;
    WebView videoPlayerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        ArrayList<String> channelIds = new ArrayList<String>();
        // PBS Kids
        channelIds.add("UCrNnk0wFBnCS1awGjq_ijGQ");
        // Andy and Ryden
        channelIds.add("UCeItv2gvphuLXrCdSMv60YA");
        // Ryan's toy review
        channelIds.add("UChGJGhZ9SOOHvBB0Y4DOO_w");
        // Combo Panda
        channelIds.add("UCb69PhsHzsorirJDlxaIXlg");
        // DC Kids / Combo Panda
        channelIds.add("UCyu8StPfZWapR6rfW_JgqcA");


        super.onCreate(savedInstanceState);
        setContentView(R.layout.alextube);

        channelListView = configureChannelListView();
        videoListView = configureVideoListView();
        videoPlayerView = configureWebView();

        loadChannelList(implode(",", channelIds));
    }

    public String implode(String delimiter, ArrayList<String> channelIds) {
        String channelIdsAsString = "";
        for (int i = 0; i < channelIds.size(); i++) {
            if (i > 0) {
                channelIdsAsString += delimiter;
            }
            channelIdsAsString += channelIds.get(i);
        }
        return channelIdsAsString;
    }

    public RecyclerView configureChannelListView() {
        RecyclerView view = (RecyclerView) findViewById(R.id.channelListContainer);
        view.setLayoutManager(new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.HORIZONTAL,
                false)
        );
        return view;
    }

    public RecyclerView configureVideoListView() {
        RecyclerView view = (RecyclerView) findViewById(R.id.videoListContainer);
        view.setLayoutManager(new LinearLayoutManager(
                getApplicationContext(),
                LinearLayoutManager.VERTICAL,
                false)
        );
        return view;
    }

    public WebView configureWebView() {
        WebView view = (WebView)findViewById (R.id.playerContainer);
        // Disable Links
        view.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
        });
        // JavaScript.  Not used with iframes
        view.getSettings().setJavaScriptEnabled(true);
        view.setWebChromeClient(new WebChromeClient());

        return view;
    }

    public void loadChannelList(final String channelIds) {
        new Thread(){
            public void run(){
                YoutubeConnector yc = new YoutubeConnector();
                ArrayList<ChannelItem> channels = yc.searchChannels(channelIds);

                final ChannelListViewAdapter adapter = new ChannelListViewAdapter(channels);
                // Post to the UI thread to update the UI
                uiThreadCallbackHandler.post(new Runnable(){
                    public void run() {
                        channelListView.setAdapter(adapter);
                    }
                });
                // TODO: What if size == 0?
                loadVideoList(channels.get(0).getId());
            }
        }.start();
    }

    public void loadVideoList(final String channelId) {
        new Thread() {
            public void run() {
                YoutubeConnector yc = new YoutubeConnector();
                ArrayList<VideoItem> videos = yc.searchVideos(channelId);

                final VideoListViewAdapter adapter = new VideoListViewAdapter(videos);
                // Post to the UI thread to update the UI
                uiThreadCallbackHandler.post(new Runnable(){
                    public void run() {
                        videoListView.setAdapter(adapter);
                    }
                });

                // TODO: What if size == 0?
                loadVideo(videos.get(0).getId());
            }
        }.start();
    }

    public void loadVideo(String videoId) {

        YoutubeConnector yc = new YoutubeConnector();
        yc.searchRelatedVideos(videoId);

        if (videoId == null) {
            return;
        }

        String url = "https://www.youtube.com/embed/" + videoId;
        // Don't start the video right away
        url += "?autoplay=1";
        // Disable keyboard recognition
        url += "&disablekb=1";
        // Don't display fullscreen button
        url += "&fs=0";
        // Try to disable YouTube logo
        url += "&modestbranding=1";
        // Try to limit related videos to same channe
        url += "&rel=0";

        String html = "<html>\n" +
                "<body>\n" +
                "<iframe width=\"100%\" height=\"100%\"\n" +
                "src=\"" + url + "\" frameborder=0>\n" +
                "</iframe>\n" +
                "</body>\n" +
                "</html>";

        // TODO: This should be on the UI thread
        videoPlayerView.loadData(html, "text/html", "UTF-8");
    }


    // Change the video when an item is clicked from the right panel
    public void onVideoSelected(VideoItem video) {
        loadVideo(video.getId());
    }

    // Change the video list when a channel is clicked
    public void onChannelSelected(ChannelItem channel) {
        loadVideoList(channel.getId());
    }
}
