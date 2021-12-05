package com.alpez.pingponglite;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import com.google.android.gms.ads.AdView;

import java.util.Random;

public class GameView extends View {
    Context context;
    float ballX, ballY;
    Velocidad velocidad = new Velocidad(25, 32);
    android.os.Handler handler;
    final long UPDATE_MILLIS = 30;
    Runnable runnable;
    Paint texPaint = new Paint();
    Paint saludPaint = new Paint();
    Paint texVida = new Paint();
    float TEXT_SIZE = 120, paddleX, paddleY, oldX, oldPaddleX;
    int puntos = 0, vidas = 3, dWidth, dHeight;
    Bitmap ball, paddle, fondo;
    Rect screen;
    MediaPlayer mpHit, mpMiss;
    Random random;
    SharedPreferences sharedPreferences;
    Boolean estadoAudio;
    private AdView mAdView;

    public GameView(Context context) {
        super(context);
        this.context = context;
        ball = BitmapFactory.decodeResource(getResources(), R.drawable.ball);
        paddle = BitmapFactory.decodeResource(getResources(), R.drawable.paddle);
        fondo = BitmapFactory.decodeResource(getResources(), R.drawable.amongfondo);
        handler = new android.os.Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        mpHit = MediaPlayer.create(context, R.raw.hit);
        mpMiss = MediaPlayer.create(context, R.raw.miss);
        texPaint.setColor(Color.RED);
        texPaint.setTextSize(TEXT_SIZE);
        texPaint.setTextAlign(Paint.Align.LEFT);

        texVida.setColor(Color.GREEN);
        texVida.setTextSize(70);
        texVida.setTextAlign(Paint.Align.RIGHT);

        saludPaint.setColor(Color.GREEN);
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;
        screen = new Rect(0, 0, dWidth, dHeight);
        random = new Random();
        ballX = random.nextInt(dWidth);
        paddleY = (dHeight * 4) / 5;
        paddleX = dWidth / 2 - paddle.getWidth() / 2;
        sharedPreferences = context.getSharedPreferences("my_pref", 0);
        estadoAudio = sharedPreferences.getBoolean("estadoAudio", true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.BLACK);
        canvas.drawBitmap(fondo, null, screen, null);

        ballX += velocidad.getX();
        ballY += velocidad.getY();

        if(ballX > dWidth - ball.getWidth() || ballX <= 0){
            velocidad.setX(velocidad.getX() * -1);
        }
        if(ballY <= 0){
            velocidad.setY(velocidad.getY() * -1);
        }
        if(ballY > paddleY + paddle.getHeight()){
            ballX = 1 + random.nextInt(dWidth - ball.getWidth() -1);
            ballY = 0;
            if(mpMiss != null && estadoAudio){
                mpMiss.start();
            }
            velocidad.setX(xVelocidad());
            velocidad.setY(32);
            vidas--;
            if(vidas == 0){
                Intent intent = new Intent(context, GameOver.class);
                intent.putExtra("puntos", puntos);
                context.startActivity(intent);
                ((Activity)context).finish();
            }
        }
        if(((ballX + ball.getWidth()) >= paddleX) && (ballX <= paddleX + paddle.getWidth()) && (ballY + ball.getHeight() >= paddleY)
            && ballY + ball.getHeight() <= paddleY + paddle.getHeight()){
            if(mpHit != null && estadoAudio){
                mpHit.start();
            }
            velocidad.setX(velocidad.getX() + 1);
            velocidad.setY((velocidad.getY() + 1) * -1);
            puntos++;
        }
        canvas.drawBitmap(ball, ballX, ballY, null);
        canvas.drawBitmap(paddle, paddleX, paddleY, null);
        canvas.drawText(""+puntos, 20, TEXT_SIZE, texPaint);
        if(vidas == 2){
            saludPaint.setColor(Color.YELLOW);
        }else if(vidas == 1){
            saludPaint.setColor(Color.RED);
        }
        canvas.drawText("Salud", dWidth-220, 70, texVida);
        canvas.drawRect(dWidth-200, 30, dWidth-200 + 60*vidas, 80, saludPaint);
        handler.postDelayed(runnable, UPDATE_MILLIS);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float tocarX = event.getX(), tocarY = event.getY();
        if(tocarY >= paddleY){
            int accion = event.getAction();
            if(accion == MotionEvent.ACTION_DOWN){
                oldX = event.getX();
                oldPaddleX = paddleX;
            }
            if(accion == MotionEvent.ACTION_MOVE){
                float shift = oldX - tocarX;
                float newPaddleX = oldPaddleX - shift;
                if(newPaddleX <= 0){
                    paddleX = 0;
                }else if(newPaddleX >= dWidth - paddle.getWidth()){
                    paddleX = dWidth - paddle.getWidth();
                }else{
                    paddleX = newPaddleX;
                }
            }
        }

        return true;
    }

    private int xVelocidad() {
        int[] valores = {-35, -30, -25, 25, 30, 35};
        int index = random.nextInt(6);

        return valores[index];
    }
}
