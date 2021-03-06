package com.hhly.mlottery.adapter.core;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class BaseRecyclerViewAdapter<V extends RecyclerView.ViewHolder> extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private View rootView;
    private ArrayList mList;
    private BaseRecyclerViewHolder.OnItemClickListener onItemClickListener;
    private BaseRecyclerViewHolder.OnItemLongClickListener onItemLongClickListener;

    public BaseRecyclerViewAdapter() {
        mList = new ArrayList();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType < 0) return null;
        if (getItemLayouts() == null) return null;
        int[] layoutIds = getItemLayouts();
        if (layoutIds.length < 1) return null;

        int itemLayoutId;
        if (layoutIds.length == 1) itemLayoutId = layoutIds[0];
        else itemLayoutId = layoutIds[viewType - 1];
        rootView = LayoutInflater.from(parent.getContext()).inflate(itemLayoutId, null);
        rootView.setLayoutParams(
                new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return new BaseRecyclerViewHolder(rootView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        try {
            BaseRecyclerViewHolder baseRecyclerViewHolder = (BaseRecyclerViewHolder) holder;
            onBindRecycleViewHolder(baseRecyclerViewHolder, position);
            baseRecyclerViewHolder.setOnItemClickListener(onItemClickListener, position);
            baseRecyclerViewHolder.setOnItemLongClickListener(onItemLongClickListener, position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public <T> T getItemByPosition(int position) {
        if (null != mList && mList.size() > 0) return (T) mList.get(position);
        return null;
    }

    public int getListSize() {
        return mList.size();
    }

    public void addAll(Collection list) {
        mList.addAll(list);
    }

    public void setList(List list) {
        mList.clear();
        if (list == null) return;
        mList.addAll(list);
    }

    public void clear() {
        mList.clear();
    }

    public void remove(Object o) {
        mList.remove(o);
    }

    public List getList() {
        return mList;
    }

    public View getRootView() {
        return rootView;
    }

    /**
     * Please return RecyclerView loading layout Id array
     * 请返回RecyclerView加载的布局Id数组
     *
     * @return 布局Id数组
     */
    public abstract int[] getItemLayouts();

    /**
     * butt joint the onBindViewHolder and
     * If you want to write logic in onBindViewHolder, you can write here
     * 对接了onBindViewHolder
     * onBindViewHolder里的逻辑写在这
     *
     * @param viewHolder viewHolder
     * @param position   position
     */
    public abstract void onBindRecycleViewHolder(BaseRecyclerViewHolder viewHolder, int position);

    /**
     * Please write judgment logic when more layout
     * and not write when single layout
     * 如果是多布局的话，请写判断逻辑
     * 单布局可以不写
     *
     * @param position Item position
     * @return 布局Id数组中的index
     */
    public abstract int getRecycleViewItemType(int position);

    /**
     * get the itemType by position
     * 根据position获取ItemType
     *
     * @param position Item position
     * @return 默认ItemType等于0
     */
    @Override
    public int getItemViewType(int position) {
        return this.getRecycleViewItemType(position);
    }

    /**
     * set the on item_event_half_finish click listener
     * 设置点击事件
     *
     * @param onItemClickListener onItemClickListener
     */
    public void setOnItemClickListener(BaseRecyclerViewHolder.OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * set the on item_event_half_finish long click listener
     * 设置长点击事件
     *
     * @param onItemLongClickListener onItemLongClickListener
     */
    public void setOnItemLongClickListener(BaseRecyclerViewHolder.OnItemLongClickListener onItemLongClickListener) {
        this.onItemLongClickListener = onItemLongClickListener;
    }
}