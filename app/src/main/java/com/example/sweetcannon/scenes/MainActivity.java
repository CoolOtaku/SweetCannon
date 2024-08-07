package com.example.sweetcannon.scenes;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.sweetcannon.GameMusic;
import com.example.sweetcannon.GameSettings;
import com.example.sweetcannon.Main;
import com.example.sweetcannon.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.CoinsText)
    TextView CoinsText;

    private GameSettings gameSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        hideNavButtons();

        ButterKnife.bind(this);

        gameSettings = new GameSettings(MainActivity.this);
    }

    public void Play(View view) {
        view.startAnimation(Main.animClick);
        GameMusic.isNotStop = true;
        startActivity(new Intent(this, GameActivity.class));
    }

    public void Settings(View view) {
        view.startAnimation(Main.animClick);
        gameSettings.showSettings();
    }

    public void Quit(View view) {
        view.startAnimation(Main.animClick);
        GameMusic.isNotStop = false;
        finish();
        System.exit(0);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        hideNavButtons();
    }

    private void hideNavButtons() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CoinsText.setText(String.valueOf(Main.COINS));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (Main.sp.getBoolean("MUSIC_ON_OF", true)) GameMusic.play(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        GameMusic.pause();
    }

}