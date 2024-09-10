package com.example.clininallied;

import android.graphics.Bitmap;

public interface BitmapCallback {
    void onBitmapReady(Bitmap bitmap);
    void onBitmapLoadFailed(Exception e);
}
