package com.bignerdranch.android.radio;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;
import java.io.IOException;

import static android.R.layout.simple_list_item_1;

public class RadioDownloads extends DialogFragment{
    public static final String ARG_PATH = "ARG_PATH";
    public MediaPlayer mMediaPlayer;
    public File path;
    public static RadioDownloads newInstance(File path){
        Bundle args = new Bundle();
        args.putSerializable(ARG_PATH, path);
        RadioDownloads dialog = new RadioDownloads();
        dialog.setArguments(args);
        return dialog;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        path = (File) getArguments().getSerializable(ARG_PATH);
        File[] filesArray = path.listFiles();
        final String [] names  = new String [filesArray.length];
        int i = 0;
        for (File f: filesArray) {
            if (f.isFile()) {
                names[i] = f.getName();
                i++;
            }
        }

        View v = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_downloads, null);

        ListView listView = (ListView) v.findViewById(R.id.list_downloads);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), simple_list_item_1, names );
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                try {
                    if(mMediaPlayer == null){
                        mMediaPlayer = new MediaPlayer();
                    }else if(mMediaPlayer.isPlaying()){
                        mMediaPlayer.stop();
                    }
                    mMediaPlayer = new MediaPlayer();
                    mMediaPlayer.setDataSource(path.getPath() + "/" + names[position]);
                    mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                    mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                        @Override
                        public void onPrepared(MediaPlayer mp) {
                            mp.start();
                        }
                    });
                    mMediaPlayer.prepareAsync();
                } catch (IOException e) {e.printStackTrace();}
            }
        });
        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle("Сохраненные аудио:")
                .create();
    }
}
