package com.yalantis.ucrop.model;

import androidx.annotation.Nullable;

public class AspectRatio {

    @Nullable
    private final String mAspectRatioTitle;
    private final float mAspectRatioX;
    private final float mAspectRatioY;

    public AspectRatio(@Nullable String aspectRatioTitle, float aspectRatioX, float aspectRatioY) {
        mAspectRatioTitle = aspectRatioTitle;
        mAspectRatioX = aspectRatioX;
        mAspectRatioY = aspectRatioY;
    }

    @Nullable
    public String getAspectRatioTitle() {
        return mAspectRatioTitle;
    }

    public float getAspectRatioX() {
        return mAspectRatioX;
    }

    public float getAspectRatioY() {
        return mAspectRatioY;
    }

}
