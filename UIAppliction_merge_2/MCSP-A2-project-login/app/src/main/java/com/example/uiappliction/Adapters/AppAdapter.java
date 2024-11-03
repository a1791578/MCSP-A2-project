package com.example.uiappliction.Adapters;

import android.content.Context;
import android.view.View;

import androidx.annotation.IntRange;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;



public abstract class AppAdapter<T> extends BaseAdapter<BaseAdapter<?>.ViewHolder> {

    private List<T> mDataSet;
    private int mPageNumber = 1;
    private boolean mLastPage;
    private Object mTag;

    public AppAdapter(@NonNull Context context) {
        super(context);
    }

    @Override
    public int getItemCount() {
        return getCount();
    }

    public int getCount() {
        if (mDataSet == null) {
            return 0;
        }
        return mDataSet.size();
    }

    public void setData(@Nullable List<T> data) {
        mDataSet = data;
        notifyDataSetChanged();
    }

    @Nullable
    public List<T> getData() {
        return mDataSet;
    }

    public void addData(List<T> data) {
        if (data == null || data.size() == 0) {
            return;
        }

        if (mDataSet == null || mDataSet.size() == 0) {
            setData(data);
            return;
        }

        mDataSet.addAll(data);
        notifyItemRangeInserted(mDataSet.size() - data.size(), data.size());
    }

    public void clearData() {
        if (mDataSet == null || mDataSet.size() == 0) {
            return;
        }

        mDataSet.clear();
        notifyDataSetChanged();
    }

    public boolean containsItem(@IntRange(from = 0) int position) {
        return containsItem(getItem(position));
    }

    public boolean containsItem(T item) {
        if (mDataSet == null || item == null) {
            return false;
        }
        return mDataSet.contains(item);
    }

    public T getItem(@IntRange(from = 0) int position) {
        if (mDataSet == null) {
            return null;
        }
        return mDataSet.get(position);
    }

    public void setItem(@IntRange(from = 0) int position, @NonNull T item) {
        if (mDataSet == null) {
            mDataSet = new ArrayList<>();
        }
        mDataSet.set(position, item);
        notifyItemChanged(position);
    }

    public void addItem(@NonNull T item) {
        if (mDataSet == null) {
            mDataSet = new ArrayList<>();
        }
        addItem(mDataSet.size(), item);
    }

    public void addItem(@IntRange(from = 0) int position, @NonNull T item) {
        if (mDataSet == null) {
            mDataSet = new ArrayList<>();
        }

        if (position < mDataSet.size()) {
            mDataSet.add(position, item);
        } else {
            mDataSet.add(item);
            position = mDataSet.size() - 1;
        }
        notifyItemInserted(position);
    }

    public void removeItem(@NonNull T item) {
        int index = mDataSet.indexOf(item);
        if (index != -1) {
            removeItem(index);
        }
    }

    public void removeItem(@IntRange(from = 0) int position) {
        mDataSet.remove(position);
        notifyItemRemoved(position);
    }

    public int getPageNumber() {
        return mPageNumber;
    }

    public void setPageNumber(@IntRange(from = 0) int number) {
        mPageNumber = number;
    }

    public boolean isLastPage() {
        return mLastPage;
    }

    public void setLastPage(boolean last) {
        mLastPage = last;
    }

    @Nullable
    public Object getTag() {
        return mTag;
    }

    public void setTag(@NonNull Object tag) {
        mTag = tag;
    }

    public final class SimpleHolder extends ViewHolder {

        public SimpleHolder(@LayoutRes int id) {
            super(id);
        }

        public SimpleHolder(View itemView) {
            super(itemView);
        }

        @Override
        public void onBindView(int position) {}
    }
}