package com.example.videoplayer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.VideoView;
import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener, VideoThumbnailInterface {

    private VideoView vw;
    private ArrayList<File> videolist = new ArrayList<>();
    private int currvideo = 0;
    private MediaController mc;
    private RecyclerView video_rv;
    private RelativeLayout relativeLayout;
    private LinearLayout linearLayout;
    private VideoListAdapter videoListAdapter;
    public static int REQUEST_PERMISSION=1;
    private File directory;
    private boolean booleanPermission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        vw = (VideoView)findViewById(R.id.video_v_id);
        video_rv = findViewById(R.id.video_rv);
        relativeLayout = findViewById(R.id.relative_layout);
        linearLayout = (LinearLayout) findViewById(R.id.linear);
        vw.setMediaController(new MediaController(this));
        vw.setOnCompletionListener(this);

        //phone and sd card
        directory=new File("/mnt/");
        permissionForVideo();
    }

    private void permissionForVideo() {

        if((ContextCompat.checkSelfPermission(getApplicationContext() ,Manifest.permission.READ_EXTERNAL_STORAGE) !=
        PackageManager.PERMISSION_GRANTED)) {
            if((ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE))) {

            } else {
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION);
            }
        }
        else{
            booleanPermission=true;
            getFile(directory);
            ArrayList<File> fileList = new ArrayList<>();
            if(fileList != null && videolist.size() > 10) {
                for(int i = 0; i<10; i++) {
                    fileList.add(videolist.get(i));
                }
            }
            videoListAdapter = new VideoListAdapter(this, fileList, this);
            video_rv.setHasFixedSize(true);
            video_rv.setLayoutManager(new LinearLayoutManager(this));
            video_rv.setAdapter(videoListAdapter);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode==REQUEST_PERMISSION){
            if(grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                booleanPermission=true;
                getFile(directory);
                ArrayList<File> fileList = new ArrayList<>();
                if(fileList != null && videolist.size() > 10) {
                    for(int i = 0; i<10; i++) {
                        fileList.add(videolist.get(i));
                    }
                }
                videoListAdapter = new VideoListAdapter(this, fileList, this);
                video_rv.setHasFixedSize(true);
                video_rv.setLayoutManager(new LinearLayoutManager(this));
                video_rv.setAdapter(videoListAdapter);
            }
        }
        else{
            Toast.makeText(this, "Please allow permission", Toast.LENGTH_SHORT).show();
        }
    }

    public ArrayList<File> getFile(File directory) {

        File listFile[]= directory.listFiles();
        if(listFile!=null && listFile.length>0){
            for(int i=0; i<listFile.length;i++){
                if(listFile[i].isDirectory()){
                    getFile(listFile[i]);
                }else{
                    booleanPermission=false;
                    if(listFile[i].getName().endsWith(".mp4")){
                        for(int j=0;j<videolist.size();j++){
                            if (videolist.get(j).getName().equals(listFile[i].getName())){
                                booleanPermission=true;
                            }
                        }
                        if(booleanPermission){
                            booleanPermission=false;
                        } else {
                            videolist.add(listFile[i]);
                        }

                    }
                }
            }
        }
        return videolist;
    }


    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        LinearLayout.LayoutParams lParams = ((LinearLayout.LayoutParams) (linearLayout.getLayoutParams())); //or create new LayoutParams...
        if(getResources().getConfiguration().orientation ==
                Configuration.ORIENTATION_LANDSCAPE) {
            video_rv.setVisibility(View.GONE);
            relativeLayout.setLayoutParams(lParams);
        } else {
            video_rv.setVisibility(View.VISIBLE);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams lParams1 = (LinearLayout.LayoutParams) linearLayout.getLayoutParams();
            linearLayout.setWeightSum(10);
            lParams.weight = 6.5f;
            lParams1.weight = 3.5f;
            relativeLayout.setLayoutParams(lParams);
            video_rv.setLayoutParams(lParams1);
        }
    }

    public void setVideo(String path) {
        Uri uri = Uri.parse(path);
        vw.setVideoURI(uri);
        vw.start();
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        AlertDialog.Builder obj = new AlertDialog.Builder(this);
        obj.setTitle("Playback Finished!");
        obj.setIcon(R.mipmap.ic_launcher);
        MyListener m = new MyListener();
        obj.setPositiveButton("Replay", m);
        obj.setNegativeButton("Next", m);
        obj.setMessage("Want to replay or play next video?");
        obj.show();
    }

    @Override
    public void setVideoThubnail(String path) {
        setVideo(path);
    }

    class MyListener implements DialogInterface.OnClickListener {
        public void onClick(DialogInterface dialog, int which)
        {
            if (which == -1) {
                vw.seekTo(0);
                vw.start();
            }
            else {
                ++currvideo;
                if (currvideo == videolist.size())
                    currvideo = 0;
               setVideo(videolist.get(currvideo).getPath());
            }
        }
    }
}
