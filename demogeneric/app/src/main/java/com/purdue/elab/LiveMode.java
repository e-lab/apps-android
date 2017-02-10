package com.purdue.elab;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;

import java.io.ByteArrayOutputStream;

public class LiveMode{
	
	protected static MainActivity context;
	Bitmap bitmap;
    YuvImage yuv;
	String detection1,detection2,detection3;

	float max[] = new float[3];
	int maxPos[] = new int[3];
	int r[];
	int g[];
	int b[];

	/**
	 *
	 * @param context
	 */
	public LiveMode(Context context) {
		this.context = (MainActivity) context;
	}

	/**
	 *
	 * @param image
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
	public Bitmap getResizedBitmap(Bitmap image, int newWidth, int newHeight) {
		int width = image.getWidth();
		int height = image.getHeight();
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// create a matrix for the manipulation
		Matrix matrix = new Matrix();
		// resize the bit map
		matrix.postScale(scaleWidth, scaleHeight);
		matrix.postRotate(90);
		// recreate the new Bitmap
		Bitmap resizedBitmap = Bitmap.createBitmap(image, 0, 0, width, height, matrix, false);
		return resizedBitmap;
	}

	/**
	 *
	 * @param data
	 * @param mCamera
	 */
	public void startProcess(byte[] data, Camera mCamera, NativeProcessor nativeAPI) {

		Camera.Parameters parameters = mCamera.getParameters();

	    int width = parameters.getPreviewSize().width;
	    int height = parameters.getPreviewSize().height;

	    yuv = new YuvImage(data, parameters.getPreviewFormat(), width, height, null);

	    ByteArrayOutputStream out = new ByteArrayOutputStream();
	    yuv.compressToJpeg(new Rect(0, 0, width, height), 50, out);

	    byte[] bytes = out.toByteArray();
	   	bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

		bitmap = getResizedBitmap(bitmap,224,224);

	    // We dump the rotated Bitmap to the stream 
	    //bitmap.compress(CompressFormat.JPEG, 20, rotatedStream);

		int pixels[] = new int[224 * 224];
		r = new int[224*224];
		g = new int[224*224];
		b = new int[224*224];

		bitmap.getPixels(pixels, 0, 224, 0, 0, 224, 224);

		for (int i = 0; i < pixels.length; i++) {
			r[i] = (pixels[i]) >> 16 & 0xff;
			g[i] = (pixels[i]) >> 8 & 0xff;
			b[i] = (pixels[i]) & 0xff;
		}

		float percentages[] = nativeAPI.processImage(r, g, b);
		max[0] = 0;
		max[1] = 0;
		max[2] = 0;
		maxPos[0] = 0;
		maxPos[1] = 0;
		maxPos[2] = 0;
		for(int j=0; j<percentages.length; j++) {
			if(percentages[j] > max[0]) {
				maxPos[0] = j+1;
				max[0] = percentages[j];
			} else if(percentages[j] > max[1]) {
				maxPos[1] = j+1;
				max[1] = percentages[j];
			} else if(percentages[j] > max[2]) {
				maxPos[2] = j+1;
				max[2] = percentages[j];
			}
		}

		detection1 = Categories.get(context).getCategory(maxPos[0]);
		detection2 = Categories.get(context).getCategory(maxPos[1]);
		detection3 = Categories.get(context).getCategory(maxPos[2]);
		context.resultField1.setText(detection1);
		context.resultField2.setText(detection2);
		context.resultField3.setText(detection3);

	}
}