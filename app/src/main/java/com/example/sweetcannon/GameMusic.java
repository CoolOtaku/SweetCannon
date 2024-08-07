package com.example.sweetcannon;

import android.content.Context;
import android.media.MediaPlayer;

import java.util.Timer;

public class GameMusic {

    public static MediaPlayer mp = null;
    private static MediaPlayer sp = null;

    public static float volume = 1f;

    public static Timer timer = new Timer();
    public static boolean isNotStop = false;

    public static void play(Context context, int resource) {
        stop();
        mp = MediaPlayer.create(context, resource);
        mp.setVolume(volume, volume);
        mp.setLooping(true);
        mp.start();
    }

    public static void sound(Context context, int resource) {
        stopSound();
        sp = MediaPlayer.create(context, resource);
        sp.setVolume(volume, volume);
        sp.start();
    }

    public static void stop() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    public static void stopSound() {
        if (sp != null) {
            sp.stop();
            sp.release();
            sp = null;
        }
    }

    public static void play(Context context) {
        if (mp != null) {
            mp.start();
        } else {
            play(context, R.raw.main);
        }
    }

    public static void pause() {
        if (isNotStop) return;
        if (mp != null) {
            mp.pause();
        }
    }

    public static boolean isPlaying() {
        if (mp != null) {
            return mp.isPlaying();
        } else {
            return false;
        }
    }

}
