package com.example.myapptreasure.treasure.home;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;


import com.example.myapptreasure.R;
import com.example.myapptreasure.commons.ActivityUtils;
import com.example.myapptreasure.treasure.TreasureRepo;
import com.example.myapptreasure.treasure.home.map.MapFragment;
import com.example.myapptreasure.user.UserPrefs;
import com.example.myapptreasure.user.account.AccountActivity;
import com.nostra13.universalimageloader.core.ImageLoader;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements NavigationView
        .OnNavigationItemSelectedListener {
    @BindView(R.id.toolbar)
    Toolbar        toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout   drawerLayout;
    @BindView(R.id.nav_view)
    NavigationView navigationView;
    private static final String TAG = "HomeActivity";
    private ActivityUtils activityUtils;

    private ImageView imageView;

    private FragmentManager fragmentManager;
    private MapFragment     mapFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityUtils = new ActivityUtils(this);
        setContentView(R.layout.activity_home);
        fragmentManager = getSupportFragmentManager();
        mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.mapFragment);
        TreasureRepo.getInstance().clear();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 每次重新回到Home，更新用户头像
        String photoUrl = UserPrefs.getInstance().getPhoto();
        if (photoUrl != null) {
            char c = photoUrl.charAt(photoUrl.length() - 1);
            Log.d(TAG, "onStart: >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>." + c);
            if (c != '/') {
                ImageLoader.getInstance().displayImage(photoUrl, imageView);
            }
        }
    }

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
        //
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);// 关闭title
        navigationView.setNavigationItemSelectedListener(this); // 监听
        //
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        //
        imageView = (ImageView) navigationView.getHeaderView(0).findViewById(R.id.iv_userIcon);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activityUtils.startActivity(AccountActivity.class);
            }
        });
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_hide: // 埋藏宝藏
                drawerLayout.closeDrawer(GravityCompat.START);
                mapFragment.switchToHideTreasure();
                break;
        }
        // 返回true,当前选项变为checked状态
        return false;
    }

    @Override
    public void onBackPressed() {
        // DrawerLayout是开的
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        }
        // DrawerLayout是关的
        else {
            if (mapFragment.clickBackPressed()) {
                super.onBackPressed();
            }
        }
    }
}