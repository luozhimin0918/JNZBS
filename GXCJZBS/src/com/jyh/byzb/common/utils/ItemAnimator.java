package com.jyh.byzb.common.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;

/**
 * 项目名称：com.demo.demo.customtool
 * 类描述：
 * 创建人：Administrator
 * 创建时间：2016/8/1014:48
 * 修改人：Administrator
 * 修改时间：2016/8/1014:48
 * 修改备注：
 */
public class ItemAnimator extends RecyclerView.ItemAnimator {
    private Animation.AnimationListener animtionListener;

    @Override
    public void runPendingAnimations() {

    }

    @Override
    public boolean animateRemove(RecyclerView.ViewHolder viewHolder) {
        stopAnimation(viewHolder.itemView);
        Animation translateIn = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -1, Animation
                .RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        translateIn.setDuration(1000);
        translateIn.setFillAfter(true);
        translateIn.setAnimationListener(animtionListener);
        viewHolder.itemView.startAnimation(translateIn);
        return false;
    }

    @Override
    public boolean animateAdd(RecyclerView.ViewHolder viewHolder) {
        stopAnimation(viewHolder.itemView);
        Animation translateIn = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1, Animation.RELATIVE_TO_SELF, 0, Animation
                .RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0);
        translateIn.setDuration(1000);
        translateIn.setFillAfter(true);
//        translateIn.setAnimationListener(animtionListener);
        viewHolder.itemView.startAnimation(translateIn);
        return false;
    }

    @Override
    public boolean animateMove(RecyclerView.ViewHolder viewHolder, int i, int i1, int i2, int i3) {
        stopAnimation(viewHolder.itemView);
        TranslateAnimation anim = new TranslateAnimation(50, 50, viewHolder.itemView.getHeight(), 0);
        anim.setDuration(1000);
        anim.setFillAfter(true);
        viewHolder.itemView.startAnimation(anim);
//        anim.setAnimationListener(animtionListener);
        return false;
    }

    public void setAnimtionListener(Animation.AnimationListener animtionListener){
        this.animtionListener=animtionListener;
    }

    @Override
    public boolean animateChange(RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1, int i, int i1, int i2, int i3) {
        return false;
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder viewHolder) {

    }

    @Override
    public void endAnimations() {

    }

    @Override
    public boolean isRunning() {
        return false;
    }

    private void stopAnimation(View view) {
        Animation animation = view.getAnimation();
        if (animation != null) {
            animation.reset();
            animation.cancel();
        }
    }
}
