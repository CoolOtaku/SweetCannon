package com.example.sweetcannon;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.TimerTask;

public class Main extends Application {

    public static int COINS = 0;
    public static SharedPreferences sp;
    private static SharedPreferences.Editor editor;
    public static Animation animClick;

    @Override
    public void onCreate() {
        super.onCreate();
        sp = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sp.edit();
        COINS = sp.getInt("COINS", 0);
        GameMusic.volume = sp.getFloat("VOLUME", 1f);
        animClick = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.click);
        GameMusic.timer.schedule(new TimerTask() {
            @Override
            public void run() {
                GameMusic.isNotStop = false;
            }
        }, 0, 500);
    }

    public static void SaveCOINS() {
        editor.putInt("COINS", COINS);
        editor.apply();
    }

    public static void SaveMusicSetting() {
        editor.putBoolean("MUSIC_ON_OF", GameMusic.isPlaying());
        editor.apply();
    }

    public static void SaveVolume(int progress) {
        editor.putFloat("VOLUME", GameMusic.volume);
        editor.putInt("VOLUME_PROGRESS", progress);
        editor.apply();
    }

    public static float getAngle(double x2, double y2, double x1, double y1) {
        float angle = (float) Math.toDegrees(Math.atan2(y2 - y1, x2 - x1));
        if (angle < 0) {
            angle += 90;
        }
        return angle;
    }

    public static float[] getPosNearCannon(ImageView CannonStand, ImageView Cannon, double x1, double y1, double deviationCoefficient) {
        float[] res = new float[2];
        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) CannonStand.getLayoutParams();

        float centerX = (float) (x1 - (lp.leftMargin + 20));
        float centerY = (float) (y1 - Cannon.getHeight());

        float radius = (float) (Cannon.getHeight() / deviationCoefficient);

        float rotationRadians = (float) Math.toRadians(Cannon.getRotation() - 90);
        res[0] = centerX + radius * (float) Math.cos(rotationRadians);
        res[1] = centerY + radius * (float) Math.sin(rotationRadians);

        return res;
    }

}
