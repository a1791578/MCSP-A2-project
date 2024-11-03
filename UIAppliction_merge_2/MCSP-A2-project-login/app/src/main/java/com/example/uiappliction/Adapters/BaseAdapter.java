package com.example.uiappliction.Adapters;

import android.content.Context;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.uiappliction.action.ResourcesAction;

public abstract class BaseAdapter<VH extends BaseAdapter<?>.ViewHolder>
        extends RecyclerView.Adapter<VH> implements ResourcesAction {

    private final Context mContext;

    private RecyclerView mRecyclerView;

    @Nullable
    private OnItemClickListener mItemClickListener;
    @Nullable
    private OnItemLongClickListener mItemLongClickListener;

    @Nullable
    private SparseArray<OnChildClickListener> mChildClickListeners;
    @Nullable
    private SparseArray<OnChildLongClickListener> mChildLongClickListeners;

    private int mPositionOffset = 0;

    public BaseAdapter(Context context) {
        mContext = context;
        if (mContext == null) {
            throw new IllegalArgumentException("are you ok?");
        }
    }

    @Override
    public final void onBindViewHolder(@NonNull VH holder, int position) {
        mPositionOffset = position - holder.getAdapterPosition();
        holder.onBindView(position);
    }

    @Nullable
    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    public abstract class ViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, View.OnLongClickListener {

        public ViewHolder(@LayoutRes int id) {
            this(LayoutInflater.from(getContext()).inflate(id, mRecyclerView, false));
        }

        public ViewHolder(View itemView) {
            super(itemView);

            if (mItemClickListener != null) {
                itemView.setOnClickListener(this);
            }
            if (mItemLongClickListener != null) {
                itemView.setOnLongClickListener(this);
            }

            if (mChildClickListeners != null) {
                for (int i = 0; i < mChildClickListeners.size(); i++) {
                    View childView = findViewById(mChildClickListeners.keyAt(i));
                    if (childView != null) {
                        childView.setOnClickListener(this);
                    }
                }
            }

            if (mChildLongClickListeners != null) {
                for (int i = 0; i < mChildLongClickListeners.size(); i++) {
                    View childView = findViewById(mChildLongClickListeners.keyAt(i));
                    if (childView != null) {
                        childView.setOnLongClickListener(this);
                    }
                }
            }
        }

        public abstract void onBindView(int position);

        protected final int getViewHolderPosition() {
            // 这里解释一下为什么用 getLayoutPosition 而不用 getAdapterPosition
            // 如果是使用 getAdapterPosition 会导致一个问题，那就是快速点击删除条目的时候会出现 -1 的情况，因为这个 ViewHolder 已经解绑了
            // 而使用 getLayoutPosition 则不会出现位置为 -1 的情况，因为解绑之后在布局中不会立马消失，所以不用担心在动画执行中获取位置有异常的情况
            return getLayoutPosition() + mPositionOffset;
        }

        /**
         * {@link View.OnClickListener}
         */

        @Override
        public void onClick(View view) {
            int position = getViewHolderPosition();
            if (position < 0 || position >= getItemCount()) {
                return;
            }

            if (view == getItemView()) {
                if(mItemClickListener != null) {
                    mItemClickListener.onItemClick(mRecyclerView, view, position);
                }
                return;
            }

            if (mChildClickListeners != null) {
                OnChildClickListener listener = mChildClickListeners.get(view.getId());
                if (listener != null) {
                    listener.onChildClick(mRecyclerView, view, position);
                }
            }
        }

        /**
         * {@link View.OnLongClickListener}
         */

        @Override
        public boolean onLongClick(View view) {
            int position = getViewHolderPosition();
            if (position < 0 || position >= getItemCount()) {
                return false;
            }

            if (view == getItemView()) {
                if (mItemLongClickListener != null) {
                    return mItemLongClickListener.onItemLongClick(mRecyclerView, view, position);
                }
                return false;
            }

            if (mChildLongClickListeners != null) {
                OnChildLongClickListener listener = mChildLongClickListeners.get(view.getId());
                if (listener != null) {
                    return listener.onChildLongClick(mRecyclerView, view, position);
                }
            }
            return false;
        }

        public final View getItemView() {
            return itemView;
        }

        public final <V extends View> V findViewById(@IdRes int id) {
            return getItemView().findViewById(id);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = recyclerView;
        if (mRecyclerView.getLayoutManager() == null) {
            RecyclerView.LayoutManager layoutManager = generateDefaultLayoutManager(mContext);
            if (layoutManager != null) {
                mRecyclerView.setLayoutManager(layoutManager);
            }
        }
    }

    @Override
    public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
        mRecyclerView = null;
    }

    protected RecyclerView.LayoutManager generateDefaultLayoutManager(Context context) {
        return new LinearLayoutManager(context);
    }

    public void setOnItemClickListener(@Nullable OnItemClickListener listener) {
        checkRecyclerViewState();
        mItemClickListener = listener;
    }

    public void setOnChildClickListener(@IdRes int id, @Nullable OnChildClickListener listener) {
        checkRecyclerViewState();
        if (mChildClickListeners == null) {
            mChildClickListeners = new SparseArray<>();
        }
        mChildClickListeners.put(id, listener);
    }

    public void setOnItemLongClickListener(@Nullable OnItemLongClickListener listener) {
        checkRecyclerViewState();
        mItemLongClickListener = listener;
    }

    public void setOnChildLongClickListener(@IdRes int id, @Nullable OnChildLongClickListener listener) {
        checkRecyclerViewState();
        if (mChildLongClickListeners == null) {
            mChildLongClickListeners = new SparseArray<>();
        }
        mChildLongClickListeners.put(id, listener);
    }

    private void checkRecyclerViewState() {
        if (mRecyclerView != null) {
            // 必须在 RecyclerView.setAdapter() 之前设置监听
            throw new IllegalStateException("are you ok?");
        }
    }

    public interface OnItemClickListener{

        void onItemClick(RecyclerView recyclerView, View itemView, int position);
    }

    public interface OnItemLongClickListener {

        boolean onItemLongClick(RecyclerView recyclerView, View itemView, int position);
    }

    public interface OnChildClickListener {

        void onChildClick(RecyclerView recyclerView, View childView, int position);
    }

    public interface OnChildLongClickListener {

        boolean onChildLongClick(RecyclerView recyclerView, View childView, int position);
    }
}