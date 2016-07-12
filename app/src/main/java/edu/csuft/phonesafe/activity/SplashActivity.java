package edu.csuft.phonesafe.activity;

import android.content.Intent;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import edu.csuft.phonesafe.R;
import edu.csuft.phonesafe.base.BaseActivity;
import edu.csuft.phonesafe.utils.Config;
import edu.csuft.phonesafe.utils.SharedUtil;
import edu.csuft.phonesafe.utils.UpdateUtil;
import edu.csuft.phonesafe.utils.ViewUtil;

public class SplashActivity extends BaseActivity {

    @Bind(R.id.layout_splash_bg)
    RelativeLayout layout_splash_bg;
    @Bind(R.id.tv_splash_version)
    TextView tv_splash_version;

    @Override
    public void initData() {
        tv_splash_version.setText("版本号:"+ ViewUtil.getAppVersion(this));
        setAnimation();

        //联网更新是否打开
        boolean isConnToUpdate = (boolean) SharedUtil.getSavedDataFromSetting(this, Config.KEY_AUTO_UPDATE,true);
        if(isConnToUpdate){     //需要联网更新
            UpdateUtil.getInstance(this).getUpdateInfo();
        }else{  //不需要联网更新
            delayToEnterMainActivity();
        }
    }

    @Override
    public void initListener() {

    }

    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_splash;
    }

    /** 延迟进入主界面 */
    private void delayToEnterMainActivity() {
        //延迟1.8秒进入主界面
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent();
                intent.setClass(SplashActivity.this,MainActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        },1800);
    }

    /** 设置动画效果 */
    private void setAnimation() {
        //Splash界面产生动画效果
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 1.5f, 1, 1.5f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //动画效果设置成2000秒
        scaleAnimation.setDuration(20000);
        layout_splash_bg.startAnimation(scaleAnimation);
    }
}