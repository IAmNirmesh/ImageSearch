package com.android.imagesearch.ui;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.imagesearch.R;
import com.android.imagesearch.network.model.ImageData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ImageListAdapter extends RecyclerView.Adapter<ImageListAdapter.ViewHolder> {

    private Context mContext;
    private List<ImageData> mImageDataList;

    // Allows to remember the last item shown on screen
    private int lastPosition = -1;
    private OnItemClickListener mOnItemClickListener;

    public ImageListAdapter(Context context, List<ImageData> mImageList) {
        mContext = context;
        mImageDataList = new ArrayList<>(mImageList);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(
                R.layout.image_list_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageData data = mImageDataList.get(position);
        Picasso.with(mContext).load(data.getUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder)
                .into(holder.searchedImage);
        holder.imageContainer.setTag(data.getUrl());
        holder.imageTitleTv.setText(data.getTitle());
        setAnimation(holder.imageContainer, position);
    }

    @Override
    public int getItemCount() {
        return mImageDataList.size();
    }

    /**
     * Here is the key method to apply the animation
     */
    private void setAnimation(View viewToAnimate, int position) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            Animation animation = AnimationUtils.loadAnimation(mContext, R.anim.slide_in_bottom);
            viewToAnimate.startAnimation(animation);
            lastPosition = position;
        }
    }

    public void setItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View v, int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        protected ImageView searchedImage;
        protected TextView imageTitleTv;
        protected FrameLayout imageContainer;

        public ViewHolder(View itemView) {
            super(itemView);
            this.searchedImage = (ImageView) itemView.findViewById(R.id.searched_image);
            this.imageTitleTv = (TextView) itemView.findViewById(R.id.image_title_tv);
            this.imageContainer = (FrameLayout) itemView.findViewById(R.id.container);
            this.imageContainer.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if (mOnItemClickListener != null)
                mOnItemClickListener.onItemClick(view, getAdapterPosition());
        }
    }
}
