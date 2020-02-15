package com.ndolpin.myapplication.dcar;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import androidx.core.view.MotionEventCompat;

import java.util.Random;

public class SpaceshipGameView extends View {
    static Context context;
    private GameThread mThread;
    static int Width, Height;

    static public Spaceship1 spaceship1;
    static public Spaceship2 spaceship2;

    // 버튼
    Button btnLeft1P;
    Button btnRight1P;

    Button btnLeft2P;
    Button btnRight2P;

    Bitmap missile;
    static int missileOk = 0; // 값이 0이면 미사일을 발사 할 수 있다.
    int missile_x = -100;
    int missile_y = 100;
    int mWidth;  //미사일의 가로 크기의 절반

    static int missileOk2 = 0; // 값이 0이면 미사일을 발사 할 수 있다.
    int missile_x2;
    int missile_y2;

    Bitmap balloon;
    int balloon_x, balloon_y;
    int bWidth;

    int player1Score = 0;
    int player2Score = 0;

    Button btnMissile1P;
    Button btnMissile2P;

    Paint paint = new Paint();

    Bitmap backImage;

    public SpaceshipGameView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Width = display.getWidth();
        Height = display.getHeight();

        Bitmap imgLeft = BitmapFactory.decodeResource(getResources(), R.drawable.btnleft);
        Bitmap imgRight = BitmapFactory.decodeResource(getResources(), R.drawable.btnright);
        Bitmap imgJump = BitmapFactory.decodeResource(getResources(), R.drawable.btn_attack);

        //화면배치등에 사용된 버튼크기의 기본값 설정
        int basicUnit = imgLeft.getWidth();

        // 1player 버튼 만들기
        btnLeft1P = new Button(imgLeft, 0, Height - basicUnit - basicUnit / 4);
        btnRight1P = new Button(imgRight, btnLeft1P.w, Height - basicUnit - basicUnit / 4);
        btnMissile1P = new Button(imgJump, Width - btnLeft1P.w, Height - basicUnit - basicUnit / 4);

        // 2player 버튼 만들기
        btnLeft2P = new Button(imgLeft, Width - btnLeft1P.w * 2, basicUnit / 4);
        btnRight2P = new Button(imgRight, Width - btnLeft1P.w, basicUnit / 4);
        btnMissile2P = new Button(imgJump, 0, basicUnit / 4);

        missile = BitmapFactory.decodeResource(context.getResources(), R.drawable.miss01);
        missile = Bitmap.createScaledBitmap(missile, Width / 20, Width / 20, true);
        mWidth = missile.getWidth() / 2;

        balloon = BitmapFactory.decodeResource(context.getResources(), R.drawable.balloon);
        balloon = Bitmap.createScaledBitmap(balloon, Width / 6, Width / 6, true);
        bWidth = balloon.getWidth() / 2;

        backImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.universe2);
        backImage = Bitmap.createScaledBitmap(backImage, Width , Height, true);

        spaceship1 = new Spaceship1(Width / 2, Height - basicUnit * 2);
        spaceship2 = new Spaceship2(Width / 2, basicUnit);
        missile_x = spaceship1.x + spaceship1.w;
        missile_y = spaceship1.y;

        missile_x2 = spaceship2.x + spaceship2.w;
        missile_y2 = spaceship2.y + spaceship2.h * 2;

        initBalloon();

        if (mThread == null) {
            mThread = new GameThread();
            mThread.start();
        }

    }

    @Override
    protected void onDetachedFromWindow() {
        mThread.operation = false;
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 우주선1이 미사일을 발사하고 우주선2을 맞추었는지 체크
        canvas.drawBitmap(backImage, 0, 0, null);

        if (isCollision(spaceship2.x + spaceship2.w, spaceship2.y + spaceship2.h, spaceship2.w, spaceship2.h, missile_x + mWidth, missile_y + mWidth, mWidth, mWidth)) {

            missileOk = 0;
            missile_y = spaceship1.y;
            player2Score -= 10;

        }

        // 우주선2가 미사일을 발사하고 우주선1을 맞추었는지 체크
        if (isCollision(spaceship1.x + spaceship1.w, spaceship1.y + spaceship1.h, spaceship1.w, spaceship1.h, missile_x2 + mWidth, missile_y2 + mWidth, mWidth, mWidth)) {

            missileOk2 = 0;
            missile_y2 = spaceship2.y + spaceship2.h * 2;
            player1Score -= 10;

        }


        if (isCollision(balloon_x + bWidth, balloon_y + bWidth, bWidth, bWidth, missile_x2 + mWidth, missile_y2 + mWidth, mWidth, mWidth)) {

            initBalloon();
            missileOk2 = 0;
            missile_y2 = spaceship2.y + spaceship2.h * 2;
            player2Score += 10;

        }

        if (isCollision(balloon_x + bWidth, balloon_y + bWidth, bWidth, bWidth, missile_x + mWidth, missile_y + mWidth, mWidth, mWidth)) {

            initBalloon();
            missileOk = 0;
            missile_y = spaceship1.y;
            player1Score += 10;
        }

        canvas.drawBitmap(spaceship1.image, spaceship1.x, spaceship1.y, null);
        canvas.drawBitmap(spaceship2.image, spaceship2.x, spaceship2.y, null);


        canvas.drawBitmap(balloon, balloon_x, balloon_y, null);

        if (missileOk == 1) {
            missile_x = spaceship1.x + spaceship1.w - mWidth;

            missile_y -= 15;

            canvas.drawBitmap(missile, missile_x, missile_y, null);

            if (missile_y < 0) {
                missileOk = 0;
                missile_y = spaceship1.y;
            }

        }

        if (missileOk2 == 1) {
            missile_x2 = spaceship2.x + spaceship2.w - mWidth;

            missile_y2 += 15;

            canvas.drawBitmap(missile, missile_x2, missile_y2, null);

            if (missile_y2 > Height) {
                missileOk2 = 0;
                missile_y2 = spaceship2.y + spaceship2.h * 2;
            }

        }

        // 1 player, 2 player 좌우버튼 및 미사일 버튼
        canvas.drawBitmap(btnLeft1P.img, btnLeft1P.x, btnLeft1P.y, null);
        canvas.drawBitmap(btnRight1P.img, btnRight1P.x, btnRight1P.y, null);
        canvas.drawBitmap(btnMissile1P.img, btnMissile1P.x, btnMissile1P.y, null);

        canvas.drawBitmap(btnLeft2P.img, btnLeft2P.x, btnLeft2P.y, null);
        canvas.drawBitmap(btnRight2P.img, btnRight2P.x, btnRight2P.y, null);
        canvas.drawBitmap(btnMissile2P.img, btnMissile2P.x, btnMissile2P.y, null);

        //점수 표시
        canvas.drawText("점수: " + player1Score + "", mWidth, Height / 2 + btnLeft1P.h, paint);
        canvas.rotate(180, Width / 2, Height / 2);
        canvas.drawText("점수: " + player2Score + "", mWidth, Height / 2 + btnLeft1P.h, paint);
        canvas.rotate(180, Width / 2, Height / 2);
    }

    boolean isCollision(int x, int y, int width, int height, int mx, int my, int mWidth, int mHeight) {

        if ((width + mWidth) > Math.abs(x - mx) && (height + mHeight) > Math.abs(y - my)) {
            return true;

        } else return false;
    }

    void initBalloon() {

        Random r1 = new Random();
        balloon_x = r1.nextInt(Width) - bWidth;
        balloon_y = r1.nextInt(bWidth) - r1.nextInt(bWidth) + Height / 2 - bWidth;
        //   balloon_y = Height / 2 - bWidth;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean isTouch = false;

        int action = MotionEventCompat.getActionMasked(event);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                isTouch = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                isTouch = false;
                break;
            default:
                return true;
        }

        // 터치 index, id
        int pIndex = MotionEventCompat.getActionIndex(event);
        int id = MotionEventCompat.getPointerId(event, pIndex);

        // 터치 좌표
        float x = MotionEventCompat.getX(event, pIndex);
        float y = MotionEventCompat.getY(event, pIndex);

        btnLeft1P.processButton(x, y, id, isTouch);
        btnRight1P.processButton(x, y, id, isTouch);
        btnMissile1P.processButton(x, y, id, isTouch);

        btnLeft2P.processButton(x, y, id, isTouch);
        btnRight2P.processButton(x, y, id, isTouch);
        btnMissile2P.processButton(x, y, id, isTouch);

        return true;
    }

    class GameThread extends Thread {
        public boolean operation = true;


        GameThread() {

            paint.setColor(Color.WHITE);
            paint.setAntiAlias(true);
            paint.setTypeface(Typeface.create("", Typeface.BOLD));
            paint.setTextSize(62);

        }

        @Override
        public void run() {
            while (operation) {
                try {
                    spaceship1.moveSpaceship(btnLeft1P.isTouch, btnRight1P.isTouch, btnMissile1P.isTouch);
                    spaceship2.moveSpaceship(btnLeft2P.isTouch, btnRight2P.isTouch, btnMissile2P.isTouch);

                    //화면 그리기
                    postInvalidate();
                    sleep(8);
                } catch (Exception e) {

                }
            }
        }
    } // Thread

}

