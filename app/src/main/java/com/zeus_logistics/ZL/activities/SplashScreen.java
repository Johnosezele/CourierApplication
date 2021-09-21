package com.zeus_logistics.ZL.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.zeus_logistics.ZL.R;

public class SplashScreen extends AppCompatActivity {

    private static int SPLASH_SCREEN = 3000;

    //Variables
    Animation left_animation, right_animation, fade_out;
    ImageView logo;
    TextView logoText1, logoText2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_splash_screen);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);


        //Animations
        left_animation = AnimationUtils.loadAnimation(this, R.anim.left_animation);
        right_animation = AnimationUtils.loadAnimation(this, R.anim.right_animation);
        fade_out = AnimationUtils.loadAnimation(this, R.anim.fade_out);

        //Hooks
        logo = findViewById(R.id.logo);
        logoText1 = findViewById(R.id.textLogo1);
        logoText2 = findViewById(R.id.textLogo2);

        logo.setAnimation(left_animation);
        logoText1.setAnimation(right_animation);
        logoText2.setAnimation(fade_out);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this,MainActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);
    }
}