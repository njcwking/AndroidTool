package com.njking.tool;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;

/**
 * @ClassName : BaseActivity
 * @Author : 陈伟
 * @Date : 2018/11/5
 * @Description : say something
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        if (getResourceLayout() != -1) {
            setContentView(getResourceLayout());
            ButterKnife.bind(this);
            initView();
        }

    }

    protected  abstract int getResourceLayout();

    protected abstract void initView();
}
