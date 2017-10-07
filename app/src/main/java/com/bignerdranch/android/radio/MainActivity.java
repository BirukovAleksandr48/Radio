package com.bignerdranch.android.radio;

import android.app.FragmentManager;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{
    final static String DIR_SD = "RadioFolder";
    static final String urlStr = "http://online.radiorecord.ru:8101/rr_128";
    public MediaPlayer mMediaPlayer;
    ArrayList<Chanal> chanals;
    Chanal curChanal;
    DownloadTask mDownloadTask;
    boolean isLoading;
    static ArrayList<String> mNames;
    private static File sdFile;
    private static File sdPath = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + DIR_SD);
    RecyclerView recView;
    ImageButton btnPlay, btnPause, btnSave, btnList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        chanals = getChanals();

        btnList = (ImageButton) findViewById(R.id.btnList);
        btnSave = (ImageButton) findViewById(R.id.btnSave);
        btnPause = (ImageButton) findViewById(R.id.btnPause);
        btnPlay = (ImageButton) findViewById(R.id.btnPlay);

        recView = (RecyclerView) findViewById(R.id.recyclerView);
        recView.setLayoutManager(new GridLayoutManager(getApplication(), 2));
        recView.setAdapter(new RadioAdapter(chanals));

        mMediaPlayer = new MediaPlayer();

        btnPause.setOnClickListener(this);
        btnPlay.setOnClickListener(this);
        btnSave.setOnClickListener(this);
        btnList.setOnClickListener(this);
        sdPath.mkdirs();
        mDownloadTask = new DownloadTask();
        isLoading = false;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.btnPlay:
                radioPlay();
                break;
            case R.id.btnPause:
                radioPause();
                break;
            case R.id.btnSave:
                radioSave();
                break;
            case R.id.btnList:
                radioList();
                break;
        }
    }

    private void radioPause() {
        if(mMediaPlayer.isPlaying()){
            mMediaPlayer.pause();
        }
    }

    private void radioPlay(){
        if(!mMediaPlayer.isPlaying()){
            mMediaPlayer.start();
        }
    }

    private void radioSave(){
        if(isLoading){
            isLoading = false;
            mDownloadTask.cancel(true);
            mDownloadTask = null;
            btnSave.setImageResource(R.drawable.ic_action_save);
        }else {
            isLoading = true;
            mDownloadTask = new DownloadTask();
            mDownloadTask.execute(curChanal.getUrl());
            btnSave.setImageResource(R.drawable.ic_action_stop);
        }
    }

    private void radioList(){
        radioPause();

        FragmentManager manager = getFragmentManager();
        RadioDownloads dialog = RadioDownloads.newInstance(sdPath);
        dialog.show(manager, "1");

    }

    public class RadioHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView imgView;
        Chanal mChanal;

        public RadioHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            imgView = (ImageView) itemView.findViewById(R.id.radio_image_view);
        }

        public void bindViewHolder(Chanal chanal) {
            this.mChanal = chanal;
            imgView.setImageDrawable(Drawable.createFromPath(sdPath + "/" + mChanal.getImgName()));
        }

        @Override
        public void onClick(View v) {
            curChanal = mChanal;
            try {
                if(mMediaPlayer.isPlaying()){
                    mMediaPlayer.stop();
                }
                mMediaPlayer = new MediaPlayer();
                mMediaPlayer.setDataSource(mChanal.getUrl());
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.start();
                    }
                });
                mMediaPlayer.prepareAsync();
            } catch (Exception e) {e.printStackTrace();}
        }
    }

    public class RadioAdapter extends RecyclerView.Adapter<RadioHolder>{
        private ArrayList<Chanal> myChanals;

        public RadioAdapter(ArrayList<Chanal> chanals) {
            myChanals = chanals;
        }

        @Override
        public RadioHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(getApplication());
            View v = inflater.inflate(R.layout.radio_item, parent, false);
            return new RadioHolder(v);
        }

        @Override
        public void onBindViewHolder(RadioHolder holder, int position) {
            Chanal chanal = chanals.get(position);
            holder.bindViewHolder(chanal);
        }

        @Override
        public int getItemCount() {
            return myChanals.size();
        }
    }

    public ArrayList<Chanal> getChanals(){
        ArrayList<Chanal> myChanals = new ArrayList<>();

        myChanals.add(new Chanal("http://us1.internet-radio.com:11094/", "xitfm.jpg", "HitFM"));
        myChanals.add(new Chanal("http://uk1.internet-radio.com:8004/stream", "radioluks.jpg", "RadioLyuks"));
        myChanals.add(new Chanal("http://us2.internet-radio.com:8046/", "russkoeradio.jpg", "Ukraina"));
        myChanals.add(new Chanal("http://pulseedm.cdnstream1.com:8124/1373_128", "kissfm.jpg", "KissFM"));
        myChanals.add(new Chanal("http://airspectrum.cdnstream1.com:8114/1648_128", "2.jpg", "KissFM"));
        myChanals.add(new Chanal("http://live.radiotequila.ro:7000/", "3.jpg", "KissFM"));
        myChanals.add(new Chanal("http://sl128.hnux.com/listen.pls?sid=1", "4.jpg", "KissFM"));
        myChanals.add(new Chanal("http://toxxor.de:8000/listen.pls?sid=1", "5.jpg", "KissFM"));
        myChanals.add(new Chanal("http://158.69.227.214:8021/", "6.jpg", "KissFM"));
        myChanals.add(new Chanal("http://aska.ru-hoster.com:8053/autodj", "7.jpg", "KissFM"));

        return myChanals;
    }

    private class DownloadTask extends AsyncTask<String, Void, Void> {
        FileOutputStream fileOS;
        BufferedInputStream radioStream;
        ByteArrayOutputStream byteArray;

        protected Void doInBackground(String... urls) {
            try {
                if(curChanal == null)
                    cancel(true);
                String FILENAME_SD = "a_radio" + System.currentTimeMillis() + ".mp3";
                sdFile = new File(sdPath, FILENAME_SD);
                URL url = new URL(curChanal.getUrl());

                fileOS = new FileOutputStream(sdFile);
                radioStream = new BufferedInputStream(url.openStream());
                byteArray = new ByteArrayOutputStream();

                byte[] buffer = new byte[1024];
                int count;
                while((count = radioStream.read(buffer,0,1024)) != -1 && !isCancelled()){
                    byteArray.write(buffer, 0, count);
                }
            }catch(Exception e){e.printStackTrace();}

            return null;
        }

        protected void onProgressUpdate(Void... progress) {
        }

        protected void onPostExecute(Void result) {
        }

        @Override
        protected void onCancelled() {
            try {
                fileOS.write(byteArray.toByteArray());
                byteArray.close();
                fileOS.close();
                radioStream.close();
                Toast.makeText(getApplicationContext(), "The audio has loaded", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {e.printStackTrace();}

            super.onCancelled();
        }
    }
}
