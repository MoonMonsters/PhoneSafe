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

/**
 * 欢迎界面，加载图片及相关信息
 */
public class SplashActivity extends BaseActivity {

    @Bind(R.id.layout_splash_bg)
    RelativeLayout layout_splash_bg;
    @Bind(R.id.tv_splash_version)
    TextView tv_splash_version;

    @Override
    public void initData() {
        tv_splash_version.setText("版本号:" + ViewUtil.getAppVersion(this));
        setAnimation();

        //联网更新是否打开
        boolean isConnToUpdate = (boolean) SharedUtil.getSavedDataFromSetting(this, Config.KEY_AUTO_UPDATE, true);
        if (isConnToUpdate) {     //需要联网更新
            UpdateUtil.getInstance(this).getUpdateInfo();
        } else {  //不需要联网更新
            delayToEnterMainActivity();
        }
//android.permission.GET_PACKAGE_SIZE
//        try {
//            String[] permissions = getPackageManager().getPackageInfo(getPackageName(), PackageManager.PERMISSION_DENIED).requestedPermissions;
//            if(permissions != null && permissions.length != 0){
//                for(String p : permissions){
//                    Log.i("TAG","----->"+p);
//                }
//            }
//        }catch (Exception e){
//            e.printStackTrace();
//        }
    }

    /**
     * 在该方法中，给控件设置监听器
     */
    @Override
    public void initListener() {

    }

    /**
     * 返回布局id
     */
    @Override
    public int getLayoutResourceId() {
        return R.layout.activity_splash;
    }

    /**
     * 延迟进入主界面
     */
    private void delayToEnterMainActivity() {
        //延迟1.8秒进入主界面
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /*
                 进入主界面
                */
                Intent intent = new Intent();
                intent.setClass(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                SplashActivity.this.finish();
            }
        }, 1800);
    }

    /**
     * 设置动画效果
     */
    private void setAnimation() {
        //Splash界面产生动画效果，界面扩大到原界面的1.5倍大
        ScaleAnimation scaleAnimation = new ScaleAnimation(1, 1.5f, 1, 1.5f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        //动画效果设置成6000秒
        scaleAnimation.setDuration(6000);
        //开启动画
        layout_splash_bg.startAnimation(scaleAnimation);
    }
}