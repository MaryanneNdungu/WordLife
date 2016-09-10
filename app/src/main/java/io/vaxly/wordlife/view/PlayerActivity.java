package io.vaxly.wordlife.view;

import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.truizlop.fabreveallayout.FABRevealLayout;
import com.truizlop.fabreveallayout.OnRevealChangeListener;

import java.io.IOException;

import io.vaxly.wordlife.R;

public class PlayerActivity extends AppCompatActivity {

    private FABRevealLayout fabRevealLayout;
    private TextView TitleText;
    private TextView TitleVerse;
    private SeekBar songProgress;
    private TextView songTitleText;
    private ImageView prev;
    private ImageView stop;
    private ImageView next;
    private WebView wordText;
    final String url1 = "http://www.esvapi.org/v2/rest/verse?key=IP&passage=";
    final String url2 = "&include-footnotes=false&include-headings=true&include-audio-link=false";


    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        findViews();

        configureFABReveal();
        myMediaPlayer();
    }

    private void findViews() {
        wordText = (WebView) findViewById(R.id.webview_text);
        fabRevealLayout = (FABRevealLayout) findViewById(R.id.fab_reveal_layout);
        TitleText = (TextView) findViewById(R.id.album_title_text);
        TitleVerse = (TextView) findViewById(R.id.artist_name_text);
        songProgress = (SeekBar) findViewById(R.id.song_progress_bar);
        styleSeekbar(songProgress);

        songTitleText = (TextView) findViewById(R.id.song_title_text);
        prev = (ImageView) findViewById(R.id.previous);
        stop = (ImageView) findViewById(R.id.stop);
        next = (ImageView) findViewById(R.id.next);

    }

    private void myMediaPlayer(){
        // get data from main activity intent
        Intent intent = getIntent();
        final String verse = getIntent().getStringExtra("verse");
        final String title = getIntent().getStringExtra("title");
        final String audioFile = getIntent().getStringExtra("link");
        String url = url1 + verse + url2;

        mediaPlayer = new MediaPlayer();

        try {

            mediaPlayer.setDataSource(audioFile);
            mediaPlayer.prepareAsync();

            TitleText.setText(title);
            TitleVerse.setText(verse);
            songTitleText.setText(title);

            prev.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seekBackward();
                }
            });
            next.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    seekForward();
                }
            });

            wordText.getSettings().setJavaScriptEnabled(true);
            wordText.loadUrl(url);

        } catch (IOException e) {
            Activity a = this;
            a.finish();
            Toast.makeText(this, getString(R.string.file_not_found), Toast.LENGTH_SHORT).show();
        }

    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void styleSeekbar(SeekBar songProgress) {
        int color = getResources().getColor(R.color.colorPrimary);
        songProgress.getProgressDrawable().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
        songProgress.getThumb().setColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    private void configureFABReveal() {
        fabRevealLayout.setOnRevealChangeListener(new OnRevealChangeListener() {
            @Override
            public void onMainViewAppeared(FABRevealLayout fabRevealLayout, View mainView) {
                showMainViewItems();
            }

            @Override
            public void onSecondaryViewAppeared(final FABRevealLayout fabRevealLayout, View secondaryView) {
                showSecondaryViewItems();

                mediaPlayer.start();
                mRunnable.run();
                stop.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mediaPlayer.pause();
                        fabRevealLayout.revealMainView();
                    }
                });
            }
        });
    }

    private void showMainViewItems() {
        scale(TitleText, 50);
        scale(TitleVerse, 150);
    }

    private void showSecondaryViewItems() {
        scale(songProgress, 0);
        animateSeekBar(songProgress);
        scale(songTitleText, 100);
        scale(prev, 150);
        scale(stop, 100);
        scale(next, 200);
    }

    private void scale(View view, long delay){
        view.setScaleX(0);
        view.setScaleY(0);
        view.animate()
                .scaleX(1)
                .scaleY(1)
                .setDuration(500)
                .setStartDelay(delay)
                .setInterpolator(new OvershootInterpolator())
                .start();
    }

    private void animateSeekBar(SeekBar seekBar){
        seekBar.setProgress(15);
        ObjectAnimator progressAnimator = ObjectAnimator.ofInt(seekBar, "progress", 15, 0);
        progressAnimator.setDuration(300);
        progressAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        progressAnimator.start();
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        finish();
    }

    public void seekForward(){
        //set seek time
        int seekForwardTime = 9000;
        // get current song position
        int currentPosition = mediaPlayer.getCurrentPosition();
        // check if seekForward time is lesser than song duration
        if(currentPosition + seekForwardTime <= mediaPlayer.getDuration()){
            // forward song
            mediaPlayer.seekTo(currentPosition + seekForwardTime);
        }else{
            // forward to end position
            mediaPlayer.seekTo(mediaPlayer.getDuration());
        }
    }
    public void seekBackward(){
        //set seek time
        int seekBackwardTime = 9000;
        // get current song position
        int currentPosition = mediaPlayer.getCurrentPosition();
        // check if seekBackward time is greater than 0 sec
        if(currentPosition - seekBackwardTime >= 0){
            // forward song
            mediaPlayer.seekTo(currentPosition - seekBackwardTime);
        }else{
            // backward to starting position
            mediaPlayer.seekTo(0);
        }
    }

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if(mediaPlayer != null) {
                //set max value
                int mDuration = mediaPlayer.getDuration();
                songProgress.setMax(mDuration);

                int mCurrentPosition = mediaPlayer.getCurrentPosition();
                songProgress.setProgress(mCurrentPosition);

                //handle drag on seekbar
                songProgress.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                    @Override
                    public void onStopTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onStartTrackingTouch(SeekBar seekBar) {
                    }
                    @Override
                    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                        if(mediaPlayer != null && fromUser){
                            mediaPlayer.seekTo(progress);
                        }
                    }
                });
            }
            //repeat above code every second
            mHandler.postDelayed(this, 10);
        }
    };
}