package com.motorola.powerfoos;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;


@TargetApi(12)
public class CreditsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        View container = findViewById(R.id.container);

        Animation mAnimation = new TranslateAnimation(0, 0, 450, -450);
        mAnimation.setDuration(15000);
        mAnimation.setFillAfter(true);
        mAnimation.setAnimationListener(new AnimationListener() {
            public void onAnimationStart(Animation animation) {}
            public void onAnimationRepeat(Animation animation) {}
            public void onAnimationEnd(Animation animation) {
               CreditsActivity.this.finish();
            }

        });
        container.setAnimation(mAnimation);
        container.setVisibility(View.VISIBLE);
    }
}
