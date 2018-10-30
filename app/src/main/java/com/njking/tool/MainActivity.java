package com.njking.tool;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 应用列表页面
 * @author
 * @date 2018/10/30 14:20
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;

    private Context mContext;

    private String searchKey;

    private int sort = 0;

    /**
     * 默认未系统应用
     */
    private int filter = PackageUtils.TYPE_USER_PACKAGE;

    private AppRecyclerViewAdapter viewAdapter = null;

    @SuppressLint("WrongConstant")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().setTitle("应用列表");

        mContext = this;
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        viewAdapter = new AppRecyclerViewAdapter(this);
        viewAdapter.setOnItemClickListener(new AppRecyclerViewAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                PackageInfo item = viewAdapter.getItem(position);
                startActivity(AppDetailActivity.createIntent(mContext, item.packageName,item.applicationInfo.loadLabel(mContext.getPackageManager()).toString()));
            }
        });
        recyclerView.setAdapter(viewAdapter);
        refresh();
    }

    private MenuItem searchMenu;
    private MenuItem clearSearchMenu;
    private MenuItem sortMenu;
    private MenuItem filterMenu;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        searchMenu = menu.findItem(R.id.search);
        clearSearchMenu = menu.findItem(R.id.clearSearch);
        sortMenu = menu.findItem(R.id.sort);
        filterMenu = menu.findItem(R.id.filter);
        clearSearchMenu.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.refresh:
                refresh();
                break;
            case R.id.search:
                showSearchDialog();
                break;
            case R.id.clearSearch:
                searchKey = null;
                searchMenu.setTitle("搜索");
                clearSearchMenu.setVisible(false);
                refresh();
                break;
            case R.id.sort:
                showSortDialog();
                break;
            case R.id.filter:
                showFilterDialog();
                break;
        }
        return true;
    }

    /**
     * 按筛选条件获得app列表
     */
    private void refresh() {
        List<PackageInfo> filterApps = PackageUtils.getAllPackage(mContext, searchKey, filter);
        switch (sort) {
            case 0:
                Collections.sort(filterApps, new Comparator<PackageInfo>() {
                    @Override
                    public int compare(PackageInfo app1, PackageInfo app2) {
                        return app1.applicationInfo.loadLabel(mContext.getPackageManager()).toString().compareToIgnoreCase(app2.applicationInfo.loadLabel(mContext.getPackageManager()).toString());
                    }
                });
                break;
            case 1:
                Collections.sort(filterApps, new Comparator<PackageInfo>() {
                    @Override
                    public int compare(PackageInfo app1, PackageInfo app2) {
                        return app1.packageName.compareToIgnoreCase(app2.packageName);
                    }
                });
                break;
        }
        viewAdapter.initApps(filterApps);
    }

    /**
     * 显示过滤弹出框
     */
    private void showFilterDialog() {
        final String[] filters = new String[]{"所有应用", "系统应用", "非系统应用"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("选择过滤条件");
        builder.setItems(filters, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                filterMenu.setTitle(String.format("应用过滤（%s）", filters[which]));
                filter = which;
                refresh();
            }
        });
        builder.show();
    }

    /**
     * 显示排序弹出框
     */
    private void showSortDialog() {
        final String[] sorts = new String[]{"应用名称", "应用包名"};
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("选择排序方式");
        builder.setItems(sorts, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sortMenu.setTitle(String.format("选择排序（%s）", sorts[which]));
                sort = which;
                refresh();
            }
        });
        builder.show();
    }

    /**
     * 显示搜索弹出框
     */
    private void showSearchDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle("搜索");
        final EditText textView = new EditText(mContext);
        textView.setHint("请输入关键词");
        textView.setSingleLine(true);
        builder.setView(textView);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                searchKey = textView.getText().toString();
                if (!TextUtils.isEmpty(searchKey)) {
                    clearSearchMenu.setVisible(true);
                    searchMenu.setTitle("搜索：" + searchKey);
                } else {
                    clearSearchMenu.setVisible(false);
                    searchMenu.setTitle("搜索");
                }
                refresh();
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

}
