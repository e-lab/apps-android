#include <string.h>
#include <jni.h>
#include <stdlib.h>
#include <stdio.h>
#include <android/log.h>
#include "thnets.h"
#include <android/asset_manager.h>
#define  D(x...)  __android_log_print(ANDROID_LOG_INFO,"thnets", "%s", x)


#define w 224
#define h 224

#define imw w
#define imh h
#define imsz (imw * imh)

int i,j;

THNETWORK *net;

/**
 * Takes the rgb values of an image, runs it through the network, and returns an array of percentages.
 * The percentages can be correlated to the categories in the categories.txt file in the network folder.
 */
jfloatArray Java_com_purdue_elab_NativeProcessor_processImage(JNIEnv *env, jobject thiz, jintArray argb)
{
    float *result;
    int outwidth, outheight;

    if(!net)
    {
        return (*env)->NewFloatArray(env, 0);
    }
    jint *pixels = (*env)->GetIntArrayElements(env, argb, NULL);

    unsigned char *myData = (unsigned char*) malloc(imsz * 3 * sizeof(unsigned char));
    int index = 0;
    int k = 0;

    //Converge rgb values into the format: RGBRGBRGBRGBRGBRGBRGB...

    for(k = 0; k < imsz; k++) {
        myData[index] = (pixels[k] & 0xff0000) >> 16;
        myData[index+1] = (pixels[k] & 0xff00) >> 8;
        myData[index+2] = pixels[k] & 0xff;
        index += 3;
    }

    int size = THProcessImages(net, &myData, 1, w, h, 3*w, &result, &outwidth, &outheight, 0);
    free(myData);

    jfloatArray percentages = (*env)->NewFloatArray(env, size);
    (*env)->SetFloatArrayRegion(env, percentages, 0, size, result);
    return percentages;
}

/**
 * Set up the system to allow native processing.
 */
int Java_com_purdue_elab_NativeProcessor_init(JNIEnv* env, jobject thiz, jobject assetManager, jboolean dropclassifier) {

    // get native asset manager. This allows access to files stored in the assets folder
    AAssetManager* manager = (AAssetManager *)AAssetManager_fromJava(env, assetManager);
    android_fopen_set_asset_manager(manager);

    THInit();

    //net = THLoadNetwork("Networks/generic");
    net = THLoadNetwork("/sdcard/neural-nets");
    if(net) {
        THUseSpatialConvolutionMM(net, 2);
        if(dropclassifier)
        {
            if(net->net->modules[net->net->nelem-1].type == MT_SoftMax)
                net->net->nelem--;
            if(net->net->modules[net->net->nelem-1].type == MT_Linear)
                net->net->nelem--;
            if(net->net->modules[net->net->nelem-1].type == MT_View)
                net->net->nelem--;
        }
        return 0;

    } else {
        D("Shiiiiit went down.");
        return -1;
    }
}
