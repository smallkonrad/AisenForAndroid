package com.example.aisensample.ui.activity;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.aisensample.R;
import com.example.aisensample.support.bean.MenuBean;
import com.example.aisensample.ui.fragment.BaseFragmentSample;
import com.example.aisensample.ui.fragment.MenuFragment;
import org.aisen.android.common.utils.SystemUtils;
import org.aisen.android.support.inject.ViewInject;
import org.aisen.android.ui.activity.basic.BaseActivity;

/**
 * Created by wangdan on 15/4/23.
 */
public class MainActivity extends BaseActivity {

    @ViewInject(id = R.id.drawer)
    private DrawerLayout mDrawerLayout;

    private ActionBarDrawerToggle mDrawerToggle;

    private MenuFragment menuFragment;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ui_main);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(false);

        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
                getToolbar(), R.string.draw_open, R.string.draw_close) {

            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }

        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        menuFragment = MenuFragment.newInstance();
        getFragmentManager().beginTransaction().add(R.id.menu_frame, menuFragment, "MenuFragment").commit();
    }

    public void onMenuSelected(MenuBean bean) {
        Fragment fragment = null;

        switch (Integer.parseInt(bean.getType())) {
        case 0:
            fragment = BaseFragmentSample.newInstance();
            break;
        }

        closeDrawer();

        getSupportActionBar().setTitle(bean.getTitleRes());

        getFragmentManager().beginTransaction().replace(R.id.content_frame, fragment, "Main").commit();

        View toolbarSpinner = getToolbar().findViewById(R.id.toolbarSpinner);
        if (fragment instanceof MainSpinnerNavigation) {
            final MainSpinnerNavigation navigation = (MainSpinnerNavigation) fragment;

            toolbarSpinner.setVisibility(View.VISIBLE);
            TextView txtTitle = (TextView) toolbarSpinner.findViewById(R.id.txtTitle);
            txtTitle.setText(navigation.generateItems()[0]);
            toolbarSpinner.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    showSpinnerDialog(MainActivity.this, navigation, v);
                }

            });

            getSupportActionBar().setTitle("");
        }
        else {
            toolbarSpinner.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        if (mDrawerToggle != null)
            mDrawerToggle.syncState();
    }

    public void closeDrawer() {
        mDrawerLayout.closeDrawers();
    }

    public static void showSpinnerDialog(final BaseActivity activity, final MainSpinnerNavigation navigation, View targetView) {
        String[] items = navigation.generateItems();

        Rect rect = new Rect();
        targetView.getGlobalVisibleRect(rect);

        AlertDialog menuDialog = new AlertDialog.Builder(activity, R.style.main_overflow_menus).setItems(items, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                navigation.onItemSelected(null, null, which, which);
            }

        }).create();
        menuDialog.show();

        if (Build.VERSION.SDK_INT < 21) {
            menuDialog.getListView().setSelector(R.drawable.abc_item_background_holo_light);
            menuDialog.getListView().setDivider(null);
        }
        menuDialog.getListView().setSelection(navigation.initPosition());

        WindowManager.LayoutParams params = menuDialog.getWindow().getAttributes();
        params.x = rect.left - Math.round(activity.getResources().getDimensionPixelSize(R.dimen.abc_action_bar_icon_vertical_padding_material) * 1.2f);
        params.y = rect.bottom - Math.round(activity.getResources().getDimensionPixelSize(R.dimen.abc_action_bar_icon_vertical_padding_material) * 0.8f);
        params.width = Math.round(targetView.getWidth() * 1.3f);
        menuDialog.setCanceledOnTouchOutside(true);
        menuDialog.getWindow().setGravity(Gravity.LEFT | Gravity.TOP);
        if (items.length > 5) {
            menuDialog.getWindow().setLayout(params.width, SystemUtils.getScreenHeight() * 3 / 5);
        }
        else {
            menuDialog.getWindow().setLayout(params.width, WindowManager.LayoutParams.WRAP_CONTENT);
        }
        menuDialog.getWindow().setAttributes(params);
    }

    public interface MainSpinnerNavigation extends AdapterView.OnItemSelectedListener {

        public String[] generateItems();

        public int initPosition();

    }
}
