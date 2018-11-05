package com.njking.tool;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * @ClassName : AppRecyclerViewAdapter
 * @Author : 陈伟
 * @Date : 2018/10/29
 * @Description : say something
 */
public class AppRecyclerViewAdapter extends RecyclerView.Adapter<AppRecyclerViewAdapter.AppViewHolder> implements View.OnClickListener {
    private final LayoutInflater mLayoutInflater;
    private final Context mContext;
    private List<PackageInfo> mAppInfos = new ArrayList<>();
    private PackageManager pm;

    private OnItemClickListener mOnItemClickListener;

    public AppRecyclerViewAdapter(Context context) {
        mContext = context;
        mLayoutInflater = LayoutInflater.from(context);
        pm = mContext.getPackageManager();
    }

    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_app_list, parent, false);
        view.setOnClickListener(this);
        return new AppViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        holder.itemView.setTag(position);
        PackageInfo app = mAppInfos.get(position);
        holder.tvAppName.setText("应用名：" + app.applicationInfo.loadLabel(pm));
        holder.tvPackageName.setText("包名：" + app.packageName);
        holder.tvVersionName.setText("版本号：V" + app.versionName);
        holder.ivIcon.setImageDrawable(app.applicationInfo.loadIcon(pm));
    }

    @Override
    public int getItemCount() {
        return mAppInfos == null ? 0 : mAppInfos.size();
    }

    public PackageInfo getItem(int position) {
        return mAppInfos.get(position);
    }

    public void initApps(List<PackageInfo> apps) {
        mAppInfos.clear();
        if (apps != null && apps.size() > 0) {
            mAppInfos.addAll(apps);
        }
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    @Override
    public void onClick(View view) {
        if (mOnItemClickListener != null) {
            mOnItemClickListener.onItemClick(view, (int) view.getTag());
        }
    }

    class AppViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.ivIcon)
        ImageView ivIcon;
        @BindView(R.id.tvAppName)
        TextView tvAppName;
        @BindView(R.id.tvPackageName)
        TextView tvPackageName;
        @BindView(R.id.tvVersionName)
        TextView tvVersionName;

        public AppViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }
}
