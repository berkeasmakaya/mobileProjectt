package com.example.mobileprojectt;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;
public class SpaceInvader extends View {
    Context context;
    Bitmap background, lifeImage;
    Handler handler;
    long UPDATE_INTERVAL = 30;
    static int screenWidth, screenHeight;
    int score = 0;
    int lives = 5;
    Paint scorePaint;
    int TEXT_SIZE = 80;
    boolean paused = false;
    OurShip ourSpaceship;
    EnemyUfo enemySpaceship;
    Random random;
    ArrayList<Shooting> enemyShots, ourShots;
    Explosion explosion;
    ArrayList<Explosion> explosions;
    boolean enemyShooting = false;

    final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            invalidate();
        }
    };

    public SpaceInvader(Context context) {
        super(context);
        this.context = context;
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        screenWidth = size.x;
        screenHeight = size.y;
        random = new Random();
        enemyShots = new ArrayList<>();
        ourShots = new ArrayList<>();
        explosions = new ArrayList<>();
        ourSpaceship = new OurShip(context);
        enemySpaceship = new EnemyUfo(context);
        handler = new Handler();
        background = BitmapFactory.decodeResource(context.getResources(), R.drawable.background);
        lifeImage = BitmapFactory.decodeResource(context.getResources(), R.drawable.life);
        scorePaint = new Paint();
        scorePaint.setColor(Color.RED);
        scorePaint.setTextSize(TEXT_SIZE);
        scorePaint.setTextAlign(Paint.Align.LEFT);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Canvas'a arka planı, puanları ve canları çiz
        canvas.drawBitmap(background, 0, 0, null);
        canvas.drawText("Score: " + score, 0, TEXT_SIZE, scorePaint);
        for (int i = lives; i >= 1; i--) {
            canvas.drawBitmap(lifeImage, screenWidth - lifeImage.getWidth() * i, 0, null);
        }
        // Canlar 0 olduğunda, oyunu durdur ve GameOver Aktivitesini başlat, puanları ile birlikte
        if (lives == 0) {
            paused = true;
            handler = null;
            Intent intent = new Intent(context, GameOver.class);
            intent.putExtra("points", score);
            context.startActivity(intent);
            ((Activity) context).finish();
        }
        // Düşman uzay gemisini hareket ettir
        enemySpaceship.ex += enemySpaceship.enemyVelocity;
        // Eğer düşman uzay gemisi sağ duvara çarparsa, düşman hızını tersine çevir
        if (enemySpaceship.ex + enemySpaceship.getEnemySpaceshipWidth() >= screenWidth) {
            enemySpaceship.enemyVelocity *= -1;
        }
        // Eğer düşman uzay gemisi sol duvara çarparsa, düşman hızını tersine çevir
        if (enemySpaceship.ex <= 0) {
            enemySpaceship.enemyVelocity *= -1;
        }
        // enemyShooting false olduğu sürece, düşman rastgele bir mesafeden ateş etmelidir
        if (!enemyShooting) {
            if (enemySpaceship.ex >= 200 + random.nextInt(400)) {
                Shooting enemyShot = new Shooting(context, enemySpaceship.ex + enemySpaceship.getEnemySpaceshipWidth() / 2, enemySpaceship.ey);
                enemyShots.add(enemyShot);
                enemyShooting = true;
            }
            if (enemySpaceship.ex >= 400 + random.nextInt(800)) {
                Shooting enemyShot = new Shooting(context, enemySpaceship.ex + enemySpaceship.getEnemySpaceshipWidth() / 2, enemySpaceship.ey);
                enemyShots.add(enemyShot);
                enemyShooting = true;
            } else {
                Shooting enemyShot = new Shooting(context, enemySpaceship.ex + enemySpaceship.getEnemySpaceshipWidth() / 2, enemySpaceship.ey);
                enemyShots.add(enemyShot);
                enemyShooting = true;
            }
        }
        // Düşman uzay gemisini çiz
        canvas.drawBitmap(enemySpaceship.getEnemySpaceship(), enemySpaceship.ex, enemySpaceship.ey, null);
        // Ekranın sol ve sağ kenarı arasında kendi uzay gemimizi çiz
        if (ourSpaceship.ox > screenWidth - ourSpaceship.getOurSpaceshipWidth()) {
            ourSpaceship.ox = screenWidth - ourSpaceship.getOurSpaceshipWidth();
        } else if (ourSpaceship.ox < 0) {
            ourSpaceship.ox = 0;
        }
        // Kendi Uzay Gemisini çiz
        canvas.drawBitmap(ourSpaceship.getOurSpaceship(), ourSpaceship.ox, ourSpaceship.oy, null);
        // Düşmanın ateşi bizim uzay gemimize çarparsa, canları azalt, ateşi düşmanShots ArrayList'ten kaldır ve patlama göster
        // Aksi takdirde ateş alt kenardan geçerse, ateşi düşmanShots ArrayList'ten kaldır
        // Düşman ateşleri ekran üzerinde yoksa, enemyShooting false yap, böylece düşman ateş edebilir
        for (int i = 0; i < enemyShots.size(); i++) {
            enemyShots.get(i).shy += 15;
            canvas.drawBitmap(enemyShots.get(i).getShot(), enemyShots.get(i).shx, enemyShots.get(i).shy, null);
            if ((enemyShots.get(i).shx >= ourSpaceship.ox)
                    && enemyShots.get(i).shx <= ourSpaceship.ox + ourSpaceship.getOurSpaceshipWidth()
                    && enemyShots.get(i).shy >= ourSpaceship.oy
                    && enemyShots.get(i).shy <= screenHeight) {
                lives--;
                enemyShots.remove(i);
                explosion = new Explosion(context, ourSpaceship.ox, ourSpaceship.oy);
                explosions.add(explosion);
            } else if (enemyShots.get(i).shy >= screenHeight) {
                enemyShots.remove(i);
            }
            if (enemyShots.size() < 1) {
                enemyShooting = false;
            }
        }
        // Bizim uzay gemimizin ateşi düşmana doğru. Eğer ateşimizle düşman uzay gemisi arasında bir çarpışma varsa, puanı artır, ateşi ourShots ArrayList'ten kaldır
        // ve yeni bir patlama nesnesi oluştur. Aksi takdirde, ateşimiz üst kenardan geçerse, ateşi ourShots ArrayList'ten kaldır
        for (int i = 0; i < ourShots.size(); i++) {
            ourShots.get(i).shy -= 15;
            canvas.drawBitmap(ourShots.get(i).getShot(), ourShots.get(i).shx, ourShots.get(i).shy, null);
            if ((ourShots.get(i).shx >= enemySpaceship.ex)
                    && ourShots.get(i).shx <= enemySpaceship.ex + enemySpaceship.getEnemySpaceshipWidth()
                    && ourShots.get(i).shy <= enemySpaceship.getEnemySpaceshipWidth()
                    && ourShots.get(i).shy >= enemySpaceship.ey) {
                score++;
                ourShots.remove(i);
                explosion = new Explosion(context, enemySpaceship.ex, enemySpaceship.ey);
                explosions.add(explosion);
            } else if (ourShots.get(i).shy <= 0) {
                ourShots.remove(i);
            }
        }
        // Patlamayı gerçekleştir
        for (int i = 0; i < explosions.size(); i++) {
            canvas.drawBitmap(explosions.get(i).getExplosion(explosions.get(i).explosionFrame), explosions.get(i).eX, explosions.get(i).eY, null);
            explosions.get(i).explosionFrame++;
            if (explosions.get(i).explosionFrame > 8) {
                explosions.remove(i);
            }
        }
        // Eğer duraklatılmamışsa, handler üzerinde postDelayed() metodunu çağıracağız. Bu, UPDATE_INTERVAL içindeki değeri içeren
        // Runnable içindeki run metodunu 30 milisaniye sonra çalıştıracaktır.
        if (!paused)
            handler.postDelayed(runnable, UPDATE_INTERVAL);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int touchX = (int) event.getX();
        // Eğer event.getAction() MotionEvent.ACTION_UP ise, ourShots arraylist size < 1 ise, yeni bir Shot oluştur.
        // Bu şekilde sadece bir ateş yapmamıza kısıtlama getiriyoruz, ekranda.
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (ourShots.size() < 1) {
                Shooting ourShot = new Shooting(context, ourSpaceship.ox + ourSpaceship.getOurSpaceshipWidth() / 2, ourSpaceship.oy);
                ourShots.add(ourShot);
            }
        }
        // Eğer event.getAction() MotionEvent.ACTION_DOWN ise, ourSpaceship'i kontrol et
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            ourSpaceship.ox = touchX;
        }
        // Eğer event.getAction() MotionEvent.ACTION_MOVE ise, ourSpaceship'i kontrol et, dokunma ile birlikte
        if (event.getAction() == MotionEvent.ACTION_MOVE) {
            ourSpaceship.ox = touchX;
        }
        // onTouchEvent() içinde true dönmek, Android sistemine dokunma olayını zaten ele aldığınızı ve başka bir işlem yapılmasına gerek olmadığını söyler.
        return true;
    }
}
