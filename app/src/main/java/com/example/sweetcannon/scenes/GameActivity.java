package com.example.sweetcannon.scenes;

import static com.example.sweetcannon.Main.getAngle;
import static com.example.sweetcannon.Main.getPosNearCannon;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sweetcannon.GameMusic;
import com.example.sweetcannon.LossToast;
import com.example.sweetcannon.Main;
import com.example.sweetcannon.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.GifImageView;

public class GameActivity extends AppCompatActivity implements View.OnTouchListener {

    @BindView(R.id.Cannon)
    ImageView Cannon;
    @BindView(R.id.CannonStand)
    ImageView CannonStand;
    @BindView(R.id.ItemsContainer)
    LinearLayout ItemsContainer;
    @BindView(R.id.FrameEffects)
    FrameLayout FrameEffects;
    @BindView(R.id.CoinsText)
    TextView CoinsText;

    private int widthScene, heightScene, itemSize, itemCount;
    private double x1, y1;
    private int idFruit = 0;
    private int countShot = 0;
    private LinkedHashSet<Integer> setsIndex = new LinkedHashSet();
    private boolean isShot = false;
    private Random random = new Random();
    private ImageView projectile;
    private Timer timer = new Timer();
    private final Handler handler = new Handler();
    private final Runnable projectileFlight = new Runnable() {
        public void run() {
            if (isShot) {
                checkPositionBullet();
                moveBullet();
            }
            if (ItemsContainer.getHeight() >= Cannon.getY() + (Cannon.getHeight() / 2)) {
                System.out.println("LOSE");
                LossToast.showToast(GameActivity.this);
                timer.cancel();
                timer.purge();
                finish();
            }
        }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        hideNavButtons();

        ButterKnife.bind(this);

        SetSizeElements();
        FrameEffects.setOnTouchListener(this);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        widthScene = displayMetrics.widthPixels;
        heightScene = displayMetrics.heightPixels;
        x1 = widthScene / 2;
        y1 = heightScene;

        itemCount = 15;
        itemSize = widthScene / itemCount;
        for (int i = 0; i < itemCount; i++) {
            LinearLayout newLinear = new LinearLayout(this);
            newLinear.setLayoutParams(new LinearLayout.LayoutParams(itemSize, ViewGroup.LayoutParams.WRAP_CONTENT));
            newLinear.setOrientation(LinearLayout.VERTICAL);
            ItemsContainer.addView(newLinear);
        }
        SpawnItem();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                handler.post(projectileFlight);
            }
        }, 0, 25);
    }

    private void SetSizeElements() {
        Cannon.getLayoutParams().width = 291 / 3;
        Cannon.getLayoutParams().height = 524 / 3;
        CannonStand.getLayoutParams().width = 252 / 3;
        CannonStand.getLayoutParams().height = 285 / 3;

        FrameLayout.LayoutParams lp = (FrameLayout.LayoutParams) CannonStand.getLayoutParams();
        lp.leftMargin = (int) (lp.leftMargin / 5.5);
        CannonStand.setLayoutParams(lp);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        double x2 = motionEvent.getX();
        double y2 = motionEvent.getY();
        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (isShot) return false;
                idFruit = random.nextInt(5) + 1;
                SpawnProjectile();
                break;
            case MotionEvent.ACTION_MOVE:
                Cannon.setRotation(getAngle(x2, y2, x1, y1));
                ChangePositionProjectile();
                break;
            case MotionEvent.ACTION_UP:
                Shot();
                break;
        }
        return true;
    }

    @SuppressLint("DiscouragedApi")
    private void SpawnItem() {
        for (int i = 0; i < ItemsContainer.getChildCount(); i++) {
            LinearLayout ChildLinear = (LinearLayout) ItemsContainer.getChildAt(i);
            ArrayList<View> tmpList = new ArrayList<>();

            ImageView img = new ImageView(GameActivity.this);
            int typeImg = random.nextInt(5) + 1;
            img.setImageResource(getResources().getIdentifier("i_" + typeImg, "drawable", getPackageName()));
            img.setTag(typeImg);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(itemSize, itemSize);
            img.setLayoutParams(layoutParams);
            tmpList.add(img);

            for (int j = 0; j < ChildLinear.getChildCount(); j++) {
                tmpList.add(ChildLinear.getChildAt(j));
            }
            ChildLinear.removeAllViews();
            for (View imgItem : tmpList) {
                ChildLinear.addView(imgItem);
            }
        }
    }

    private void Shot() {
        if (isShot) return;
        GifImageView flash = new GifImageView(GameActivity.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300 / 2, 300 / 2);
        flash.setLayoutParams(layoutParams);
        flash.setRotation(Cannon.getRotation());
        float[] res = getPosNearCannon(CannonStand, Cannon, x1, y1, 1.2);
        flash.setX(res[0]);
        flash.setY(res[1]);
        FrameEffects.addView(flash);
        flash.setImageResource(R.drawable.flash);
        isShot = true;
        GameMusic.sound(GameActivity.this, R.raw.shot);
    }

    private void moveBullet() {
        float radius = (float) 15;
        float centerX = projectile.getX();
        float centerY = projectile.getY();
        float rotationRadians = (float) Math.toRadians(Cannon.getRotation() - 90);
        projectile.setX(centerX + radius * (float) Math.cos(rotationRadians));
        projectile.setY(centerY + radius * (float) Math.sin(rotationRadians));
    }

    private void SpawnProjectile() {
        FrameEffects.removeAllViews();
        projectile = new ImageView(GameActivity.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) (150 / 2.5), (int) (150 / 2.5));
        projectile.setLayoutParams(layoutParams);
        projectile.setRotation(Cannon.getRotation());
        float[] res = getPosNearCannon(CannonStand, Cannon, x1 + 45, y1 + 55, 2);
        projectile.setX(res[0]);
        projectile.setY(res[1]);
        FrameEffects.addView(projectile);
        projectile.setImageResource(getResources().getIdentifier("i_" + idFruit, "drawable", getPackageName()));
    }

    private void ChangePositionProjectile() {
        projectile.setRotation(Cannon.getRotation());
        float[] res = getPosNearCannon(CannonStand, Cannon, x1 + 45, y1 + 55, 2);
        projectile.setX(res[0]);
        projectile.setY(res[1]);
    }

    private void checkPositionBullet() {
        float centerX = projectile.getX() + (projectile.getWidth() / 2);
        float centerY = projectile.getY() + (projectile.getHeight() / 2);

        if (centerX <= (0 - projectile.getWidth()) || centerY <= (0 - projectile.getHeight()) || centerX >= (widthScene + projectile.getWidth())) {
            FrameEffects.removeView(projectile);
            setsIndex.clear();
            isShot = false;
            return;
        } else if (centerY <= ItemsContainer.getHeight()) {
            int index = getIndexPositionX(centerX);
            setsIndex.add(index);
            if (index >= 0 && index <= 14) {
                LinearLayout ChildLinear = (LinearLayout) ItemsContainer.getChildAt(index);
                int index2 = getIndexPositionY(centerY);

                if (centerY <= ChildLinear.getHeight() && index2 == ChildLinear.getChildCount() - 1) {
                    addFruitToPlatform(ChildLinear, index2, index);
                } else if (centerY <= ChildLinear.getHeight() && index2 < ChildLinear.getChildCount() - 1 && ChildLinear.getChildAt(index2) instanceof ImageView) {
                    addFruitToPlatformInSpecificPosition(ChildLinear, index2, index);
                }
            }
        }
    }

    private int getIndexPositionX(float centerX) {
        int itemWidth = ItemsContainer.getWidth() / itemCount;
        int index = (int) (centerX / itemWidth);
        float offset = (centerX - projectile.getX()) % itemWidth / (float) itemWidth;
        if (offset > 0.5f) index++;
        return index;
    }

    private int getIndexPositionY(float centerY) {
        int countItem = 0;
        for (int i = 0; i < itemCount; i++) {
            LinearLayout tmpView = (LinearLayout) ItemsContainer.getChildAt(i);
            if (countItem < tmpView.getChildCount()) {
                countItem = tmpView.getChildCount();
            }
        }
        int itemHeight = ItemsContainer.getHeight() / countItem;
        int index = (int) (centerY / itemHeight);
        float offset = (centerY - projectile.getY()) % itemHeight / (float) itemHeight;
        if (offset > 0.5f) index++;
        return index;
    }

    private void addFruitToPlatform(LinearLayout ChildLinear, int index, int columnIndex) {
        ChildLinear.addView(getNewFruit());
        endShot(ChildLinear, index + 1, columnIndex);
    }

    private void addFruitToPlatformInSpecificPosition(LinearLayout ChildLinear, int index, int columnIndex) {
        Integer[] tmpArray = new Integer[setsIndex.size()];
        tmpArray = setsIndex.toArray(tmpArray);
        LinearLayout ChildLinear2 = (LinearLayout) ItemsContainer.getChildAt(tmpArray[setsIndex.size() - 2]);

        if (ChildLinear2.getChildCount() == index) {
            addFruitToPlatform(ChildLinear2, index, columnIndex);
        } else if (ChildLinear2.getChildCount() - 1 > index && ChildLinear.getChildAt(index) instanceof ImageView) {
            ArrayList<View> tempList = new ArrayList<>();
            for (int i = 0; i < ChildLinear2.getChildCount(); i++) {
                tempList.add(ChildLinear2.getChildAt(i));
            }
            if (tempList.get(index) instanceof ImageView && !(tempList.get(index + 1) instanceof ImageView))
                index += 1;
            tempList.set(index, getNewFruit());
            ChildLinear2.removeAllViews();
            for (View v : tempList) {
                ChildLinear2.addView(v);
            }
            endShot(ChildLinear2, index, columnIndex);
        } else if (ChildLinear2.getChildCount() - 1 < index) {
            for (int i = 0; i < index; i++) {
                View v = ChildLinear2.getChildAt(i);
                if (v == null) setNullItemFruit(ChildLinear2);
            }
            addFruitToPlatform(ChildLinear2, index, columnIndex);
        }
    }

    private void setNullItemFruit(LinearLayout ChildLinear) {
        View v = new View(GameActivity.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(itemSize, itemSize);
        v.setLayoutParams(layoutParams);
        ChildLinear.addView(v);
    }

    private ImageView getNewFruit() {
        ImageView img = new ImageView(GameActivity.this);
        img.setImageResource(getResources().getIdentifier("i_" + idFruit, "drawable", getPackageName()));
        img.setTag(idFruit);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(itemSize, itemSize);
        img.setLayoutParams(layoutParams);
        return img;
    }

    private void endShot(LinearLayout ChildLinear, int index, int columnIndex) {
        FrameEffects.removeView(projectile);
        setsIndex.clear();
        considerCombinations(ChildLinear, index, columnIndex);
        countShot++;
        if (countShot >= 5) {
            countShot = 0;
            SpawnItem();
        }
        isShot = false;
    }

    private void considerCombinations(LinearLayout ChildLinear, int index, int columnIndex) {
        Map<LinearLayout, ArrayList<Integer>> Combinations = new HashMap<>();
        Combinations.put(ChildLinear, checkColumn(ChildLinear, index));

        try {
            Combinations.putAll(checkLeftAndRightColumn(ChildLinear, columnIndex, Combinations.entrySet().iterator().next().getValue()));
        } catch (Exception e) {
        }

        boolean isWin = false;
        ArrayList<Integer> allResInt = new ArrayList<>();
        for (Map.Entry<LinearLayout, ArrayList<Integer>> item : Combinations.entrySet()) {
            allResInt.addAll(item.getValue());
        }
        if (allResInt.size() >= 3) {
            isWin = true;
            Main.COINS++;
            CoinsText.setText(String.valueOf(Main.COINS));
            Main.SaveCOINS();
            GameMusic.sound(GameActivity.this, R.raw.win);
        }

        for (Map.Entry<LinearLayout, ArrayList<Integer>> item : Combinations.entrySet()) {
            if (isWin) {
                ArrayList<View> tempList = new ArrayList<>();
                for (int i = 0; i < item.getKey().getChildCount(); i++) {
                    View v = item.getKey().getChildAt(i);
                    if (v instanceof ImageView && !item.getValue().contains(i)) {
                        tempList.add(v);
                    }
                }
                item.getKey().removeAllViews();
                for (View v : tempList) {
                    item.getKey().addView(v);
                }
            }
        }
    }

    private ArrayList<Integer> checkColumn(LinearLayout ChildLinear, int index) {
        ArrayList<Integer> res = new ArrayList<>();
        boolean isIn = true;
        for (int i = index; i >= 0; i--) {
            if (ChildLinear.getChildAt(i) != null && ChildLinear.getChildAt(index) != null
                    && ChildLinear.getChildAt(i).getTag() == ChildLinear.getChildAt(index).getTag() && isIn) {
                res.add(i);
            } else {
                isIn = false;
            }
        }
        isIn = true;
        for (int i = index + 1; i < ChildLinear.getChildCount(); i++) {
            if (ChildLinear.getChildAt(i) != null && ChildLinear.getChildAt(index) != null
                    && ChildLinear.getChildAt(i).getTag() == ChildLinear.getChildAt(index).getTag() && isIn) {
                res.add(i);
            } else {
                isIn = false;
            }
        }
        return res;
    }

    private HashMap checkLeftAndRightColumn(LinearLayout ChildLinear, int columnIndex, List<Integer> resInt) {
        Map<LinearLayout, ArrayList<Integer>> Combinations = new HashMap<>();
        if (resInt.size() == 0) return null;
        int tagInt = (int) ChildLinear.getChildAt(resInt.get(0)).getTag();
        LinearLayout cl;
        if (columnIndex > 0) {
            columnIndex--;
            cl = (LinearLayout) ItemsContainer.getChildAt(columnIndex);
            for (Integer i : resInt) {
                if (cl.getChildAt(i) instanceof ImageView && tagInt == (int) cl.getChildAt(i).getTag()) {
                    Combinations.put(cl, checkColumn(cl, i));
                }
            }
        }
        if (columnIndex < itemCount - 1) {
            columnIndex+=2;
            cl = (LinearLayout) ItemsContainer.getChildAt(columnIndex);
            for (Integer i : resInt) {
                if (cl.getChildAt(i) instanceof ImageView && tagInt == (int) cl.getChildAt(i).getTag()) {
                    Combinations.put(cl, checkColumn(cl, i));
                }
            }
        }

        return (HashMap) Combinations;
    }

    public void Exit(View view) {
        view.startAnimation(Main.animClick);
        GameMusic.isNotStop = true;
        onBackPressed();
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