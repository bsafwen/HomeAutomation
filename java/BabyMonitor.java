package com.iot.homeautomation;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class BabyMonitor extends AppCompatActivity {
    VideoView videoView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_baby_monitor);
        videoView = (VideoView)findViewById(R.id.videoView);
        MediaController mediaController= new MediaController(this);
        mediaController.setAnchorView(videoView);

        Uri uri = Uri.parse("rtsp://192.168.1.4:5454/mystream.sdp");

        videoView.setMediaController(mediaController);
        videoView.setVideoURI(uri);
        //videoView.setVideoPath("/sdcard/20160401_144649.mp4");
        videoView.requestFocus();
        videoView.start();
    }
}
