package edu.csuft.phonesafe.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import edu.csuft.phonesafe.fragment.SettingFragment;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState == null) {
            //将Fragment加载进Activity中
            getFragmentManager().beginTransaction()
                    .add(android.R.id.content, new SettingFragment())
                    .commit();
        }
    }
}
