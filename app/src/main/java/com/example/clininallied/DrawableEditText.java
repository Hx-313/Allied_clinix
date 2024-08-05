package com.example.clininallied;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatEditText;
public class DrawableEditText extends AppCompatEditText{
    private OnDrawableClickListener onDrawableClickListener;
    public DrawableEditText(@NonNull Context context) {
        super(context);
    }
    public DrawableEditText(Context context , AttributeSet attrs){
        super(context,attrs);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            // Check if drawableEnd was clicked
            if (getCompoundDrawables()[2] != null) {
                int drawableEndStart = getWidth() - getPaddingEnd() - getCompoundDrawables()[2].getBounds().width();
                if (event.getX() >= drawableEndStart) {
                    if (onDrawableClickListener != null) {
                        onDrawableClickListener.onDrawableClick();
                    }
                    return true;
                }
            }
        }
        return super.onTouchEvent(event);
    }
    public void setOnDrawableClickListener(OnDrawableClickListener listener) {
        this.onDrawableClickListener = listener;
    }

    public interface OnDrawableClickListener {
        void onDrawableClick();
    }

}
