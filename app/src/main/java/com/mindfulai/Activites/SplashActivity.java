package com.mindfulai.Activites;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.mindfulai.Utils.SPData;
import com.mindfulai.ministore.R;
import com.mindfulai.Utils.CommonUtils;
import com.valdesekamdem.library.mdtoast.MDToast;

public class SplashActivity extends Activity implements Animation.AnimationListener {
    private static final String TAG = "SplashActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        try {
            getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
            Animation animFadeIn = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.animation_fade_in);
            animFadeIn.setAnimationListener(this);
            RelativeLayout linearLayout = findViewById(R.id.layout_linear);
            linearLayout.setVisibility(View.VISIBLE);
            linearLayout.startAnimation(animFadeIn);
        } catch (Exception e) {
            Log.e(TAG, "onCreate: " + e);
            new CommonUtils(SplashActivity.this).showErrorMessage(""+e);
        }
    }

    @Override
    public void onAnimationStart(Animation animation) {
        //under Implementation
    }

    public void onAnimationEnd(Animation animation) {
        Intent i = new Intent(SplashActivity.this, MainActivity.class);
        i.putExtra("show",true);
        startActivity(i);
        this.finish();
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
        //under Implementation
    }

}