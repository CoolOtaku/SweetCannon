package com.example.sweetcannon;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

public class GameSettings implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {
    private Context context;
    private Dialog dialog;

    private SeekBar seekVolume;
    private ImageView musicOnOfImage;

    public GameSettings(Context context) {
        this.context = context;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        Window windowAlDl = dialog.getWindow();

        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        windowAlDl.setAttributes(layoutParams);

        dialog.setContentView(R.layout.game_settings);
        seekVolume = dialog.findViewById(R.id.seekVolume);
        musicOnOfImage = dialog.findViewById(R.id.musicOnOfImage);
        seekVolume.setOnSeekBarChangeListener(this);
        musicOnOfImage.setOnClickListener(this);

        if (Main.sp.getBoolean("MUSIC_ON_OF", true)) {
            musicOnOfImage.setImageResource(R.drawable.music_on);
        } else {
            musicOnOfImage.setImageResource(R.drawable.music_off);
        }
        seekVolume.setMax(100);
        seekVolume.setProgress(Main.sp.getInt("VOLUME_PROGRESS", 100));
        initBtnOk();
    }

    public void showSettings() {
        dialog.show();
    }

    private void initBtnOk() {
        Button yes = dialog.findViewById(R.id.alertbox_yes);
        yes.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        view.startAnimation(Main.animClick);
        switch (view.getId()) {
            case R.id.alertbox_yes:
                dialog.cancel();
                break;
            case R.id.musicOnOfImage:
                if (GameMusic.isPlaying()) {
                    GameMusic.stop();
                    musicOnOfImage.setImageResource(R.drawable.music_off);
                } else {
                    GameMusic.play(context, R.raw.main);
                    musicOnOfImage.setImageResource(R.drawable.music_on);
                }
                Main.SaveMusicSetting();
                break;
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
        GameMusic.volume = (float) (1 - (Math.log(100 - progress) / Math.log(100)));
        if (GameMusic.mp != null) {
            GameMusic.mp.setVolume(GameMusic.volume, GameMusic.volume);
        }
    }
    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Main.SaveVolume(seekBar.getProgress());
    }

}
