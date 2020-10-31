package com.mindfulai.Adapter;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.mindfulai.ministore.R;
import com.mindfulai.Utils.GlobalEnum;
import com.mindfulai.customclass.PhotoFullPopupWindow;
import com.smarteist.autoimageslider.SliderViewAdapter;

import java.util.List;

public class SliderAdapterExample extends
        SliderViewAdapter<SliderAdapterExample.SliderAdapterVH> {

    private Context context;
    private List<String> mSliderItems ;

    public SliderAdapterExample(Context context, List<String> sliderItems) {
        this.context = context;
        this.mSliderItems = sliderItems;
        Log.e("SliderItem", sliderItems.toString());
        notifyDataSetChanged();
    }


    @Override
    public SliderAdapterVH onCreateViewHolder(ViewGroup parent) {
        View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_nd_video_slider_item, null);
        return new SliderAdapterVH(inflate);
    }

    @Override
    public void onBindViewHolder(final SliderAdapterVH viewHolder, final int position) {
        try {
            final String sliderItem = mSliderItems.get(position);
            Log.e("SliderItem", mSliderItems.get(position));
            if (!sliderItem.endsWith("mp4")) {
                Glide.with(context).load(GlobalEnum.AMAZON_URL + sliderItem).into(viewHolder.imageViewBackground);
                viewHolder.videoView.setVisibility(View.GONE);
                viewHolder.imageViewBackground.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        new PhotoFullPopupWindow(context, viewHolder.imageViewBackground, GlobalEnum.AMAZON_URL + sliderItem, null);
                    }
                });
            } else {
                viewHolder.videoView.setVisibility(View.VISIBLE);
                viewHolder.videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mp.setLooping(true);
                    }
                });
                viewHolder.videoView.setVideoPath(GlobalEnum.AMAZON_URL + sliderItem);
                //viewHolder.videoView.start();
                viewHolder.videoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        viewHolder.videoView.start();
                    }
                });
                viewHolder.imageViewBackground.setVisibility(View.GONE);
            }

        } catch (Exception e) {
            Log.e("TAG", "onBindViewHolder: " + e);
        }
    }

    @Override
    public int getCount() {
        //slider view count could be dynamic size
        return mSliderItems.size();
    }

    static class SliderAdapterVH extends SliderViewAdapter.ViewHolder {
        ImageView imageViewBackground;
        VideoView videoView;

        SliderAdapterVH(View itemView) {
            super(itemView);
            imageViewBackground = itemView.findViewById(R.id.salon_image_slider_item);
            videoView = itemView.findViewById(R.id.videoView);
        }
    }
}