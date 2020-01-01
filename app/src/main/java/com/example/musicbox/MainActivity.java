package com.example.musicbox;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private MediaPlayer mediaPlayer;
    private ImageView artistImage;
    private TextView runningTime;
    private TextView remainingTime;
    private SeekBar seekBar;
    private Button prevButton;
    private Button playButton;
    private Button nextButton;
    private Thread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setUpUI();

        seekBar.setMax(mediaPlayer.getDuration());
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mediaPlayer.seekTo(progress);

                }

                SimpleDateFormat simpleDate = new SimpleDateFormat("mm:ss");
                int currentPos = mediaPlayer.getCurrentPosition();
                int duration = mediaPlayer.getDuration();

                runningTime.setText(simpleDate.format(new Date(currentPos)));
                remainingTime.setText(simpleDate.format(new Date(duration - currentPos)));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {


            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    public void setUpUI(){

        mediaPlayer = new MediaPlayer();
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.cscott);

        artistImage = (ImageView) findViewById(R.id.artistImage);
        runningTime = (TextView) findViewById(R.id.runningTime);
        remainingTime = (TextView) findViewById(R.id.remainingTime);
        seekBar = (SeekBar) findViewById(R.id.seekBar);
        prevButton = (Button) findViewById(R.id.prevButton);
        playButton = (Button) findViewById(R.id.playButton);
        nextButton = (Button) findViewById(R.id.nextButton);

        prevButton.setOnClickListener(this);
        playButton.setOnClickListener(this);
        nextButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){

            case R.id.prevButton:
                prevMusic();
                break;

            case R.id.playButton:

                if(mediaPlayer.isPlaying()){
                    pauseMusic();
                }else{
                    startMusic();
                }

                break;

            case R.id.nextButton:
                nextMusic();
                break;
        }
    }

    public void pauseMusic(){

        if(mediaPlayer != null){
            mediaPlayer.pause();

        }
    }

    public void  startMusic(){

        if(mediaPlayer != null){
            mediaPlayer.start();
            updateThread();
        }
    }

    public void prevMusic(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(0);
        }
    }

    public void nextMusic(){
        if(mediaPlayer.isPlaying()){
            mediaPlayer.seekTo(mediaPlayer.getDuration() - 1000);
        }
    }



    public void updateThread(){

        thread = new Thread(){
            @Override
            public void run(){
                try{
                    while(mediaPlayer != null && mediaPlayer.isPlaying()) {
                        Thread.sleep(50);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                int newPosition = mediaPlayer.getCurrentPosition();
                                int newMax = mediaPlayer.getDuration();
                                seekBar.setMax(newMax);
                                seekBar.setProgress(newPosition);


                                    try {
                                        runningTime.setText(String.valueOf(new SimpleDateFormat("mm:ss").format(new Date(mediaPlayer.getCurrentPosition()))));
                                        remainingTime.setText(String.valueOf(new SimpleDateFormat("mm:ss").format(new Date(mediaPlayer.getCurrentPosition()))));

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                            }
                        });
                    }
                }   catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        };
        thread.start();
    }

    @Override
    protected void onDestroy(){

        if(mediaPlayer != null && mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        thread.interrupt();
        thread = null;
        super.onDestroy();
    }
}
