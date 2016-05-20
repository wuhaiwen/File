package com.whw.file;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

public class WelcomeActy extends Activity {

    private ImageView welcomeImg = null;
    //避免程序第一次加载时出现长时间空白的尴尬情况，
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = View.inflate(this,R.layout.activity_welcome_acty,null);
        setContentView(view);
        //welcomeImg = (ImageView) this.findViewById(R.id.imageView2);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f, 1.0f);
        alphaAnimation.setDuration(1000);// 设置动画显示时间
        view.startAnimation(alphaAnimation);
        alphaAnimation.setAnimationListener(new AnimationImpl());

    }

    private class AnimationImpl implements Animation.AnimationListener {

        @Override
        public void onAnimationStart(Animation animation) {
          // welcomeImg.setBackgroundResource(R.drawable.timg);
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            skip(); // 动画结束后跳转到别的页面
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

    }

    private void skip() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }
}
