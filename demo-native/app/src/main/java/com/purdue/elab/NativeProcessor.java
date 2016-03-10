package com.purdue.elab;

import android.content.res.AssetManager;

public class NativeProcessor {
    public native void init(AssetManager assetManager);
    public native float[] processImage(int[] r, int[] g, int[] b);
}