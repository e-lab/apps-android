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
jfloatArray Java_com_purdue_elab_NativeProcessor_processImage(JNIEnv *env, jobject thiz,
jintArray r, jintArray g, jintArray b)
{
    float *result;
    int outwidth, outheight;


    jint *redPixels = (*env)->GetIntArrayElements(env, r, NULL);
    jint *greenPixels = (*env)->GetIntArrayElements(env, g, NULL);
    jint *bluePixels = (*env)->GetIntArrayElements(env, b, NULL);

    unsigned char *myData = (unsigned char*) malloc(imsz * 3 * sizeof(unsigned char));
    int index = 0;
    int k = 0;

    //Converge rgb values into the format: RGBRGBRGBRGBRGBRGBRGB...

    for(k = 0; k < imsz; k++) {
        myData[index] = redPixels[k];
        myData[index+1] = greenPixels[k];
        myData[index+2] = bluePixels[k];

        index += 3;
    }

    int size = THProcessImages(net, &myData, 1, w, h, 3*w, &result, &outwidth, &outheight, 0);

    jfloatArray percentages = (*env)->NewFloatArray(env, size);
    (*env)->SetFloatArrayRegion(env, percentages, 0, size, result);
    return percentages;
}

/**
 * Set up the system to allow native processing.
 */
void Java_com_purdue_elab_NativeProcessor_init(JNIEnv* env, jobject thiz, jobject assetManager) {

    // get native asset manager. This allows access to files stored in the assets folder
    AAssetManager* manager = AAssetManager_fromJava(env, assetManager);
    android_fopen_set_asset_manager(manager);

    THInit();

    net = THLoadNetwork("Networks/generic");
    if(net) {

    } else {
        D("Shiiiiit went down.");
    }
    THUseSpatialConvolutionMM(net, 2);
}
