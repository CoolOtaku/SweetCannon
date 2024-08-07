package com.example.sweetcannon;

import static android.view.View.inflate;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.Toast;

public class LossToast {

    private static View view;
    private static Toast toast;

    @SuppressLint("ResourceAsColor")
    public static void showToast(Context context) {
        view = inflate(context, R.layout.loss_toast, null);

        toast = new Toast(context);
        toast.setView(view);
        toast.setGravity(Gravity.FILL, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

}
