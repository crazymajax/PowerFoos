package com.motorola.powerfoos;
import android.annotation.TargetApi;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewPropertyAnimator;


@TargetApi(12)
public class CreditsActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credits);

        View container = findViewById(R.id.container);
        ViewPropertyAnimator anim = container.animate();

        anim.translationYBy(100);

    }

}
