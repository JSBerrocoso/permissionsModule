/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package android.support.v7ox.widget;

import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Build;
import android.support.annotationox.NonNull;
import android.support.v4ox.view.ViewCompat;
//import android.support.v7ox.appcompat.R;
import com.gigigo.permissions.R;

import android.util.AttributeSet;
import android.view.View;

class AppCompatBackgroundHelper {

    private final View mView;
    private final AppCompatDrawableManager mDrawableManager;

    private TintInfo mInternalBackgroundTint;
    private TintInfo mBackgroundTint;
    private TintInfo mTmpInfo;

    AppCompatBackgroundHelper(View view, AppCompatDrawableManager drawableManager) {
        mView = view;
        mDrawableManager = drawableManager;
    }

    void loadFromAttributes(AttributeSet attrs, int defStyleAttr) {
        TypedArray a = mView.getContext().obtainStyledAttributes(attrs,
                R.styleable.ViewBackgroundHelper, defStyleAttr, 0);
        try {
            if (a.hasValue(R.styleable.ViewBackgroundHelper_android_background)) {
                ColorStateList tint = mDrawableManager.getTintList(mView.getContext(),
                        a.getResourceId(R.styleable.ViewBackgroundHelper_android_background, -1));
                if (tint != null) {
                    setInternalBackgroundTint(tint);
                }
            }
            if (a.hasValue(R.styleable.ViewBackgroundHelper_backgroundTint_ox)) {
                ViewCompat.setBackgroundTintList(mView,
                        a.getColorStateList(R.styleable.ViewBackgroundHelper_backgroundTint_ox));
            }
            if (a.hasValue(R.styleable.ViewBackgroundHelper_backgroundTintMode_ox)) {
                ViewCompat.setBackgroundTintMode(mView,
                        DrawableUtils.parseTintMode(
                                a.getInt(R.styleable.ViewBackgroundHelper_backgroundTintMode_ox, -1),
                                null));
            }
        } finally {
            a.recycle();
        }
    }

    void onSetBackgroundResource(int resId) {
        // Update the default background tint
        setInternalBackgroundTint(mDrawableManager != null
                ? mDrawableManager.getTintList(mView.getContext(), resId)
                : null);
    }

    void onSetBackgroundDrawable(Drawable background) {
        // We don't know that this drawable is, so we need to clear the default background tint
        setInternalBackgroundTint(null);
    }

    void setSupportBackgroundTintList(ColorStateList tint) {
        if (mBackgroundTint == null) {
            mBackgroundTint = new TintInfo();
        }
        mBackgroundTint.mTintList = tint;
        mBackgroundTint.mHasTintList = true;

        applySupportBackgroundTint();
    }

    ColorStateList getSupportBackgroundTintList() {
        return mBackgroundTint != null ? mBackgroundTint.mTintList : null;
    }

    void setSupportBackgroundTintMode(PorterDuff.Mode tintMode) {
        if (mBackgroundTint == null) {
            mBackgroundTint = new TintInfo();
        }
        mBackgroundTint.mTintMode = tintMode;
        mBackgroundTint.mHasTintMode = true;

        applySupportBackgroundTint();
    }

    PorterDuff.Mode getSupportBackgroundTintMode() {
        return mBackgroundTint != null ? mBackgroundTint.mTintMode : null;
    }

    void applySupportBackgroundTint() {
        final Drawable background = mView.getBackground();
        if (background != null) {
            if (mBackgroundTint != null) {
                AppCompatDrawableManager
                        .tintDrawable(background, mBackgroundTint, mView.getDrawableState());
            } else if (mInternalBackgroundTint != null) {
                AppCompatDrawableManager.tintDrawable(background, mInternalBackgroundTint,
                        mView.getDrawableState());
            } else if (shouldCompatTintUsingFrameworkTint(background)) {
                compatTintDrawableUsingFrameworkTint(background);
            }
        }
    }

    void setInternalBackgroundTint(ColorStateList tint) {
        if (tint != null) {
            if (mInternalBackgroundTint == null) {
                mInternalBackgroundTint = new TintInfo();
            }
            mInternalBackgroundTint.mTintList = tint;
            mInternalBackgroundTint.mHasTintList = true;
        } else {
            mInternalBackgroundTint = null;
        }
        applySupportBackgroundTint();
    }

    private boolean shouldCompatTintUsingFrameworkTint(@NonNull Drawable background) {
        // GradientDrawable doesn't implement setTintList on API 21
        return (Build.VERSION.SDK_INT == 21 && background instanceof GradientDrawable);
    }

    private void compatTintDrawableUsingFrameworkTint(@NonNull Drawable background) {
        if (mTmpInfo == null) {
            mTmpInfo = new TintInfo();
        }
        final TintInfo info = mTmpInfo;
        info.clear();

        final ColorStateList tintList = ViewCompat.getBackgroundTintList(mView);
        if (tintList != null) {
            info.mHasTintList = true;
            info.mTintList = tintList;
        }
        final PorterDuff.Mode mode = ViewCompat.getBackgroundTintMode(mView);
        if (mode != null) {
            info.mHasTintMode = true;
            info.mTintMode = mode;
        }

        if (info.mHasTintList || info.mHasTintMode) {
            AppCompatDrawableManager.tintDrawable(background, info, mView.getDrawableState());
        }
    }
}
