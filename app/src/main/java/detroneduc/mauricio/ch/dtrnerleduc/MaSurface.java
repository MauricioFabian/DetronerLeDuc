package detroneduc.mauricio.ch.dtrnerleduc;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.SensorManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.jirbo.adcolony.AdColonyVideoAd;

import java.util.ArrayList;
import java.util.Random;


/**
 * Created by MauricioF on 12.09.2014.
 */
public class MaSurface extends SurfaceView implements SurfaceHolder.Callback, View.OnTouchListener {

    private Context context;
    private SurfaceHolder holder;
    private Canvas canvas;

    private Paint paint;
    private Paint paintTime;

    private Typeface fontTextTime;

    private TouchListener tl;

    private  SensorManager sm;

    private Bitmap imgPlayer;
    private Bitmap imgEnnemi;
    private Bitmap imgBackground;


    private int width;
    private int height;

    private int x;
    private int y;

    private int margeUp = 110;
    private int margeDown = 1;
    private int margeLeft = 1;
    private int margeRight = 1;

    private String txtTime;
    private AdColonyVideoAd ad;


    private Player player = new Player();

    private ArrayList<Ennemy> tabEnnemis = new ArrayList<Ennemy>();

    private DrawingThread dt;
    private TimeThread tt;

    private boolean gameIsOver;



    public MaSurface(Context context) {
        super(context);
        this.context = context;
        initParams();
        initListener();
        initBitmaps();
        initPlayer();
        initEnnemis();
        initThreads();
        initPaints();
        ad = new AdColonyVideoAd(Constantes.ZONEID);
    }


    private void detectBordPlayer() {

        x= tl.getPlayer().getX();
        y= tl.getPlayer().getY();

        if (x < margeLeft)
            x = margeLeft;
        else if (x + imgPlayer.getWidth() >= width - margeRight)
            x = width - imgPlayer.getWidth() - margeRight;
        if (y < margeUp)
            y = margeUp;
        else if (y + imgPlayer.getHeight() >= height - margeDown)
            y = height - imgPlayer.getHeight() - margeDown;


    }

    @Override
    public void draw(Canvas canvas) {
        if (checkCollisions()) {

            canvas.drawColor(Color.WHITE);

            paintTime.setTextSize(125);
            paintTime.setColor(Color.RED);
            paintTime.setStyle(Paint.Style.FILL);


            canvas.drawText("Game over", 150, height / 2, paintTime);
            paint.setTextSize(120);
            canvas.drawText("Votre score: " + txtTime, 150, height / 2 + 200, paintTime);

            gameOver();

        } else {

            canvas.drawColor(Color.WHITE);
            canvas.drawBitmap(imgBackground,0,0,paint);

            paintTime.setTextSize(25);
            paintTime.setColor(Color.RED);
            paintTime.setStyle(Paint.Style.FILL);

            canvas.drawLine(margeLeft, 60, width - 5, 60, paintTime);

            canvas.drawText("Temps: " + txtTime, width / 2, 40, paintTime);


            paint.setColor(Color.WHITE);

            detectBordPlayer();
            detectEnnemisOutOfScreen();
            for (Ennemy en : tabEnnemis) {


                canvas.drawBitmap(imgEnnemi, en.getX(), en.getY(), paint);
            }

                canvas.drawBitmap(imgPlayer, x - imgPlayer.getWidth() / 2, y - imgPlayer.getHeight() / 2, paint);
            }


        }

    @Override
    public boolean onTouch(View view, MotionEvent event) {

            Intent menuPrincpalIntent = new Intent(context, MainActivity.class);
            (context).startActivity(menuPrincpalIntent);
            return true;

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch (keyCode) {
            case KeyEvent.KEYCODE_APP_SWITCH:
            {
                gameOver();
                return true;
            }
            case KeyEvent.KEYCODE_HOME:
            {
                gameOver();
                return true;
            }
        }
        return super.onKeyDown(keyCode, event);
    }


    private void detectEnnemisOutOfScreen(){
        ArrayList<Ennemy> newTabEnnemis= new ArrayList<Ennemy>();
        for(Ennemy en: tabEnnemis){
            if(en.getX()+imgEnnemi.getWidth() <= 0){
                Random r = new Random();
                Ennemy newEn= new Ennemy();
                newEn= new Ennemy();
                newEn.setY(margeUp + r.nextInt(height - margeUp));
                newEn.setX(width+r.nextInt(width+400));
                newEn.setVitesse(r.nextInt(50 - 20));
                newTabEnnemis.add(newEn);
            }else{
                newTabEnnemis.add(en);
            }

            tabEnnemis=newTabEnnemis;
        }
    }

    private boolean checkCollisions(){
        RectF rectPlayer= new RectF();
        RectF rectEn= new RectF();
        rectPlayer.set(x - imgPlayer.getWidth() / 2, y - imgPlayer.getHeight() / 2, x + imgPlayer.getWidth() / 2, y + imgPlayer.getHeight() / 2);
        for(Ennemy en: tabEnnemis){
            rectEn.set(en.getX(),en.getY(),en.getX()+imgEnnemi.getWidth(),en.getY()+imgEnnemi.getHeight());
            if(RectF.intersects(rectEn,rectPlayer)){
                return true;
            }
        }
         return false;
    }



    private void gameOver(){
        dt.keepDrawing=false;
        tt.keepRunning=false;

        for (Ennemy en: tabEnnemis){
            en=null;
        }
        player = null;
        txtTime=null;
        gameIsOver=true;
        setOnTouchListener(this);

    }



    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        dt.keepDrawing = true;
        tt.keepRunning = true;
        float scale = (float) imgBackground.getHeight() / height;
        int newWidth = Math.round(imgBackground.getWidth() / scale);
        int newHeight = Math.round(imgBackground.getHeight() / scale);
        imgBackground= Bitmap.createScaledBitmap(imgBackground,newWidth,newHeight,true);
        dt.start();
        tt.start();
     }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i2, int i3) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        gameOver();
    }

    private void initParams(){
        holder = this.getHolder();
        holder.addCallback(this);

        width = getContext().getResources().getDisplayMetrics().widthPixels;
        height = getContext().getResources().getDisplayMetrics().heightPixels;
    }

    private void initListener(){
        setOnTouchListener(null);
        tl= new TouchListener();
        this.setOnTouchListener(tl);
    }



    private void initBitmaps(){
        imgPlayer = ((BitmapDrawable) getResources().getDrawable(R.drawable.player)).getBitmap();
        imgEnnemi = ((BitmapDrawable) getResources().getDrawable(R.drawable.ennemy)).getBitmap();
        imgBackground = ((BitmapDrawable) getResources().getDrawable(R.drawable.background)).getBitmap();
    }

    private void initPlayer(){
        x = imgPlayer.getWidth() / 2;
        y = imgPlayer.getHeight() / 2;
        player= new Player();
        player.setX(1);
        player.setY(height/2);
        tl.setPlayer(player);

    }

    private void initEnnemis(){
        for (int i = 0; i < 20; i++) {
            tabEnnemis.add(new Ennemy());
        }
        for (Ennemy en : tabEnnemis) {
            Random r = new Random();

            en.setY(margeUp + r.nextInt(height - margeUp));
            en.setX(width+r.nextInt(width+400));
            en.setVitesse(r.nextInt(6 - 2));
        }
    }

    private void initThreads(){
        dt = new DrawingThread();
        tt = new TimeThread();
    }

    private void initPaints(){
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintTime = new Paint();
        paintTime.setAntiAlias(true);
    }




    private class DrawingThread extends Thread {
        private volatile boolean keepDrawing = true;

        @Override
        public void run() {
            while (keepDrawing) {
                canvas = null;
                try {
                    canvas = holder.lockCanvas();
                    for (Ennemy en : tabEnnemis) {
                        if(en.getX()+imgEnnemi.getWidth()==0){

                            Random r = new Random();
                            en.setY(margeUp + r.nextInt(height - margeUp));
                            en.setX(width+r.nextInt(width+400));
                            en.setVitesse(r.nextInt(6 - 2));
                        }else{
                            Random r= new Random();
                            en.setX(en.getX() - en.getVitesse());
                        }

                    }

                        draw(canvas);

                } catch (Exception e) {
                    Log.d("ErrorThread:", "Erreur dans le Thread DRAWING");

                } finally {
                    holder.unlockCanvasAndPost(canvas);
                }
            }
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                Log.d("ErrorThread:", "Erreur dans le Thread DRAWING");
            }
        }
    }

    private class TimeThread extends Thread {
       private volatile boolean keepRunning = true;



        @Override
        public void run() {
            Log.d("OK:","Thread temps lancé");
            int i = 0;
            while (keepRunning) {
            txtTime = i+"";
                try {
                    sleep(1000);
                    i++;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        }
    }




}

