package com.yalantis.ucrop.model;

import android.graphics.Bitmap;
import android.net.Uri;

public class CropParameters {

    private final int mMaxResultImageSizeX;
    private final int mMaxResultImageSizeY;

    private final Bitmap.CompressFormat mCompressFormat;
    private final int mCompressQuality;
    private final String mImageInputPath;
    private final String mImageOutputPath;
    private final ExifInfo mExifInfo;

    private Uri mContentImageInputUri, mContentImageOutputUri;


    public CropParameters(int maxResultImageSizeX, int maxResultImageSizeY,
                          Bitmap.CompressFormat compressFormat, int compressQuality,
                          String imageInputPath, String imageOutputPath, ExifInfo exifInfo) {
        mMaxResultImageSizeX = maxResultImageSizeX;
        mMaxResultImageSizeY = maxResultImageSizeY;
        mCompressFormat = compressFormat;
        mCompressQuality = compressQuality;
        mImageInputPath = imageInputPath;
        mImageOutputPath = imageOutputPath;
        mExifInfo = exifInfo;
    }

    public int getMaxResultImageSizeX() {
        return mMaxResultImageSizeX;
    }

    public int getMaxResultImageSizeY() {
        return mMaxResultImageSizeY;
    }

    public Bitmap.CompressFormat getCompressFormat() {
        return mCompressFormat;
    }

    public int getCompressQuality() {
        return mCompressQuality;
    }

    public String getImageInputPath() {
        return mImageInputPath;
    }

    public String getImageOutputPath() {
        return mImageOutputPath;
    }

    public ExifInfo getExifInfo() {
        return mExifInfo;
    }

    public Uri getContentImageInputUri() {
        return mContentImageInputUri;
    }

    public void setContentImageInputUri(Uri mContentImageInputUri) {
        this.mContentImageInputUri = mContentImageInputUri;
    }

    public Uri getContentImageOutputUri() {
        return mContentImageOutputUri;
    }

    public void setContentImageOutputUri(Uri mContentImageOutputUri) {
        this.mContentImageOutputUri = mContentImageOutputUri;
    }
}
