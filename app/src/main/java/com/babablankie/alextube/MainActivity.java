package com.babablankie.alextube;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // Handler provides mechanism to run something in the UI thread via a callback
    final Handler uiThreadCallbackHandler = new Handler();

    RecyclerView channelListView;
    RecyclerView videoListView;
    WebView videoPlayerView;

    private String googleApiKey;
    private final static String googleApiKeyFileName = "api";
    private final static String googleApiKeyFileDirectory = "raw";

    private final static int RELEVANT_VIDEOS_LIMIT = 5;

    private ArrayList<String> channelIds = new ArrayList<String>();

    public void initChannelIdList() {
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
        // Nickelodean
        channelIds.add("UC5M_h2S8Ldoc9M6f7B-_m6A");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initChannelIdList();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.alextube);

        channelListView = configureChannelListView();
        videoListView = configureVideoListView();
        videoPlayerView = configureWebView();

        googleApiKey = loadGoogleApiKey(googleApiKeyFileName, googleApiKeyFileDirectory);

//        new Thread() {
//            public void run() {
//                final String channelId = "UCeItv2gvphuLXrCdSMv60YA";
//                final String videoId = "KTh-acN3Kk8";
//                YoutubeConnector yc = new YoutubeConnector(googleApiKey);
//                ArrayList<VideoItem> videos = yc.searchRelatedVideos(videoId, channelId);
//
//                final VideoListViewAdapter adapter = new VideoListViewAdapter(videos);
//                // Post to the UI thread to update the UI
//                uiThreadCallbackHandler.post(new Runnable(){
//                    public void run() {
//                        videoListView.setAdapter(adapter);
//                    }
//                });
//            }
//        }.start();



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
                YoutubeConnector yc = new YoutubeConnector(googleApiKey);
                ArrayList<ChannelItem> channels = yc.searchChannels(channelIds);

                final ChannelListViewAdapter adapter = new ChannelListViewAdapter(channels);
                // Post to the UI thread to update the UI
                uiThreadCallbackHandler.post(new Runnable(){
                    public void run() {
                        channelListView.setAdapter(adapter);
                    }
                });

                if (channels.size() > 0) {
                    int randIndex = (new Random()).nextInt(channels.size());
                    loadVideoList(channels.get(randIndex).getId());
                }
            }
        }.start();
    }

    public void loadVideoList(final String channelId) {
        new Thread() {
            public void run() {
                YoutubeConnector yc = new YoutubeConnector(googleApiKey);
                ArrayList<VideoItem> videos = yc.searchVideos(channelId);

                final VideoListViewAdapter adapter = new VideoListViewAdapter(videos);
                // Post to the UI thread to update the UI
                uiThreadCallbackHandler.post(new Runnable(){
                    public void run() {
                        videoListView.setAdapter(adapter);
                    }
                });

                if (videos.size() > 0) {
                    int randIndex = (new Random()).nextInt(videos.size());
                    loadVideo(videos.get(randIndex).getId());
                }
            }
        }.start();
    }


    public void loadVideo(final String videoId) {
        uiThreadCallbackHandler.post(new Runnable() {
            public void run() {
                loadVideoWorker(videoId);
            }
        });
    }

    public void loadVideoWorker(String videoId) {

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
        // Try to limit related videos to same channel
        url += "&rel=0";
        // Try to disable closed captioning
        url += "cc_load_policy=0";


        String html = "<html>\n" +
                "<body>\n" +
                "<iframe width=\"100%\" height=\"100%\"\n" +
                "src=\"" + url + "\" frameborder=0>\n" +
                "</iframe>\n" +
                "</body>\n" +
                "</html>";

        videoPlayerView.loadData(html, "text/html", "UTF-8");
    }


    // Change the video when an item is clicked from the right panel
    public void onVideoSelected(VideoItem video) {
        loadVideo(video.getId());

        final String videoId = video.getId();
        new Thread() {
            public void run() {
                YoutubeConnector yc = new YoutubeConnector(googleApiKey);
                ArrayList<VideoItem> videos = yc.searchRelatedVideos(videoId, channelIds);

                if (videos.size() > RELEVANT_VIDEOS_LIMIT) {
                    // Replace the video list with relevant videos if there are enough of them

                    final VideoListViewAdapter adapter = new VideoListViewAdapter(videos);
                    // Post to the UI thread to update the UI
                    uiThreadCallbackHandler.post(new Runnable() {
                        public void run() {
                            videoListView.setAdapter(adapter);
                        }
                    });
                }
            }
        }.start();

    }

    // Change the video list when a channel is clicked
    public void onChannelSelected(ChannelItem channel) {
        loadVideoList(channel.getId());
    }

    private String loadGoogleApiKey(final String fileName, final String fileDirectory){
        InputStream ins = getResources().openRawResource(
            getResources().getIdentifier(fileName, fileDirectory, getPackageName())
        );

        ByteArrayOutputStream byteStream = null;
        try {
            byte[] buffer = new byte[ins.available()];
            ins.read(buffer);
            byteStream = new ByteArrayOutputStream();
            byteStream.write(buffer);
            byteStream.close();
            ins.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return byteStream.toString();
    }
}
