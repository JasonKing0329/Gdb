package com.jing.app.jjgallery.gdb.view.star.phone;

import android.content.Intent;
import android.view.MotionEvent;

import com.jing.app.jjgallery.gdb.GBaseActivity;
import com.jing.app.jjgallery.gdb.R;

/**
 * 通过star page的 record list打开record设置为打开后保留StarActivity，为了防止在star page>record page(finish)>star page>record page(finish)...过程中
 * ，star page(StarActivity)被无限打开，将StarActivity的启动模式设置为singleTask
 * singleTask模式下，重新启动一个StarActivity的时候不会执行onCreate，这时候就需要在StarActivity的onNewIntent设置完成后在onResume里重新获取starId进行重新加载了
 * 把onResume的操作直接写在StarFragment里
 */
public class StarActivity extends GBaseActivity {

    public static final String KEY_STAR_ID = "key_star_id";
    private StarFragment starFragment;

    @Override
    public int getContentView() {
        return R.layout.activity_pull_zoom_header;
    }

    @Override
    public void initController() {

    }

    @Override
    public void initView() {
        getSupportActionBar().hide();
        starFragment = new StarFragment();
        starFragment.setStarId(getIntent().getLongExtra(KEY_STAR_ID, -1));
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, starFragment)
                .commit();
    }

    @Override
    public void initBackgroundWork() {

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        starFragment.setStarId(intent.getIntExtra(KEY_STAR_ID, -1));
        starFragment.onNewIntent();
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (starFragment != null) {
            starFragment.dispatchTouchEvent(ev);
        }
        return super.dispatchTouchEvent(ev);
    }
}
