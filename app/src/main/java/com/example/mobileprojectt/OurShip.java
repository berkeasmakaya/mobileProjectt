package com.example.mobileprojectt;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;
public class OurShip {
    Context context;
    Bitmap ourSpaceship;
    int ox, oy;
    Random random;

    public OurShip(Context context) {
        this.context = context;
        ourSpaceship = BitmapFactory.decodeResource(context.getResources(), R.drawable.rocket1);
        random = new Random();
        ox = random.nextInt(SpaceInvader.screenWidth);
        oy = SpaceInvader.screenHeight - ourSpaceship.getHeight();
    }

    public Bitmap getOurSpaceship(){
        return ourSpaceship;
    }

    int getOurSpaceshipWidth(){
        return ourSpaceship.getWidth();
    }
}

