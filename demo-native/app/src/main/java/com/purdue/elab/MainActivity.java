package com.purdue.elab;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.ImageFormat;
import android.graphics.Point;
import android.graphics.Typeface;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.os.Build;
import android.os.Bundle;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity {

    NativeProcessor nativeAPI;

    int screenwidth, screenheight;
	int fps = 0;
    int nativeerror;
	Camera myCamera;

    MyCameraSurfaceView myCameraSurfaceView;
	Settings settings;

    static int currentCameraId;
    FrameLayout myCameraPreview;
    
    Button myButton;
    Button switchCamera;
    Button myset;
    TextView resultField1;
    TextView resultField2;
    TextView resultField3;
    TextView fpsField;
    TextView live;
    View recordingState;

	RelativeLayout mainlayout;

    boolean recording = false;
    boolean camswitch = false;

    Typeface bebasNeue;
	Typeface openSans;
	Typeface telegrafico;

    static
    {
        System.loadLibrary("NativeProcessor");
    }


    /**
     *
     */
	public void initialize(){
        nativeAPI = new NativeProcessor();
        nativeerror = nativeAPI.init(getAssets());

        telegrafico = Typeface.createFromAsset(getAssets(), "telegrafico.ttf");
        mainlayout = (RelativeLayout) findViewById(R.id.background);

        live = (TextView) findViewById(R.id.liveView);
        live.setTextColor(Color.parseColor("#FCC200"));
        live.setTypeface(telegrafico);

        myButton = (Button) findViewById(R.id.buttonstart);
        myButton.setOnClickListener(myButtonOnClickListener);
        recordingState = (View) findViewById(R.id.recordingState);
        switchCamera = (Button)findViewById(R.id.camera);
        switchCamera.setOnClickListener(switchCameraOnClickListener);

        resultField1 = (TextView) findViewById(R.id.category1);
        resultField2 = (TextView) findViewById(R.id.category2);
        resultField3 = (TextView) findViewById(R.id.category3);
        fpsField = (TextView) findViewById(R.id.fps);
        myset = (Button)findViewById(R.id.mysettings);
        myset.setOnClickListener(mySettingsOnClickListener);

        bebasNeue = Typeface.createFromAsset(getAssets(), "BebasNeue.otf");
        openSans = Typeface.createFromAsset(getAssets(), "OpenSans.ttf");

        resultField1.setTypeface(bebasNeue);
        resultField2.setTypeface(bebasNeue);
        resultField3.setTypeface(bebasNeue);
        fpsField.setTypeface(openSans);
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		settings = new Settings(MainActivity.this);

       	currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
        
        myCamera = Camera.open(currentCameraId); // attempt to get a Camera instance
        myCamera.setDisplayOrientation(90);
        
        myCameraSurfaceView = new MyCameraSurfaceView(this, myCamera);  
        myCameraPreview = (FrameLayout) findViewById(R.id.surface_camera);
        myCameraPreview.addView(myCameraSurfaceView);
        
    	initialize();

        /*******GET SIZE OF PHONE SCREEN******/
        Display screenDisplay = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        screenDisplay.getSize(size);
        screenwidth = size.x;
        screenheight = size.y;
        /*************************************/

        Categories categories = Categories.get(this);

		Thread t = new Thread() {
			  @Override
			  public void run() {
			    try {
			      while (!isInterrupted()) {
			        Thread.sleep(1000);
			        runOnUiThread(new Runnable() {
			          @Override
			          public void run() {
			        		  fpsField.setText(fps + " fps");
			        		  fps = 0;
			          }
			        });
			      }
			    } catch (InterruptedException e) {
			    }
			  }
		};
		t.start();
    }

	/**
	 *
	 */
    Button.OnClickListener myButtonOnClickListener = new Button.OnClickListener() {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR1)
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub

            if(nativeerror != 0)
                Toast.makeText(MainActivity.this, "No network found in /sdcard/demo-native",Toast.LENGTH_LONG).show();
            else if (recording) {
            	recording = false;
                //Turn back to red button
                recordingState.animate().setDuration(250).alpha(0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        recordingState.setBackgroundResource(R.drawable.redcircle);
                        recordingState.animate().setDuration(250).alpha(1).setListener(new AnimatorListenerAdapter(){
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                recording = false;
                            }
                        });
                    }
                });
        	} 
        	else {
                //Turn to green button
                recordingState.animate().setDuration(250).alpha(0).setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        recordingState.setBackgroundResource(R.drawable.greencircle);
                        recordingState.animate().setDuration(250).alpha(1).setListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                recording = true;
                            }
                        });
                    }
                });
        	}
        }
    };

	/**
	 *
	 */
    Button.OnClickListener switchCameraOnClickListener = new Button.OnClickListener() {
    	@Override
        public void onClick(View v) {
            camswitch = true;
    		if (recording) {
    			    	Toast.makeText(MainActivity.this, "Make sure recording is off!",Toast.LENGTH_SHORT).show();      			      
    		}
    		else {
    			myCamera.setPreviewCallback(null);
    			myCamera.release();
    			//swap the id of the camera to be used
    			if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK){
    				currentCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
    			}
    			else {
    				currentCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
    			}
    			myCamera = Camera.open(currentCameraId);
    			
    			if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT){
    				
    			}
    			else {
    				//myCamera.setDisplayOrientation(90);
    			}
    			
    			if (myCamera == null) {
    	            Toast.makeText(MainActivity.this,"Fail to get Camera",Toast.LENGTH_LONG).show();
    	        }
    			//System.err.println("Again");
    			myCameraPreview.removeView(myCameraSurfaceView);
    			myCameraSurfaceView = new MyCameraSurfaceView(MainActivity.this, myCamera);
    			//myCameraPreview = (FrameLayout) findViewById(R.id.surface_camera);
    			myCameraPreview.addView(myCameraSurfaceView);
    			//myCamera.startPreview();
    		}
        	camswitch = false;
    	}
    };


	/**
	 * 	Class that controls Camera View
	 */
    public class MyCameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback,Camera.PreviewCallback {
    	
        SurfaceHolder mHolder;
        Camera mCamera;

        public MyCameraSurfaceView(Context context, Camera camera) {
            super(context);
            mCamera = camera;

            // Install a SurfaceHolder.Callback so we get notified when the
            // underlying surface is created and destroyed.
            mHolder = getHolder();
            mHolder.addCallback(this);

            // deprecated setting, but required on Android versions prior to 3.0
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }
        
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int weight, int height) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.  	
        	
            if (mHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }
            
            Parameters parameters;
        	parameters = mCamera.getParameters();
      	
        	parameters.setPreviewFormat(ImageFormat.NV21);
        	//parameters.setPreviewSize(640, 480);
        	mCamera.setParameters(parameters);
        	
            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception e) {
                // ignore: tried to stop a non-existent preview
            }
            
            // make any resize, rotate or reformatting changes here

            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(mHolder);
            	mCamera.setPreviewCallback(this);
                mCamera.startPreview();

            } catch (Exception e) {
            }
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // TODO Auto-generated method stub
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
                
                mCamera.setDisplayOrientation(90);
             
                /*if(currentCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
                	mCamera.setDisplayOrientation(90);
                else
                	mCamera.setDisplayOrientation(270);*/
            } catch (Exception e) {
            	mCamera.release();
        		mCamera = null;
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // TODO Auto-generated method stub
        }

        LiveMode liveMode = new LiveMode(MainActivity.this);
        
		@Override
		public void onPreviewFrame(byte[] data, Camera arg1) {
			// TODO Auto-generated method stub
			//System.err.println("I came here");
			if(recording) {
				fps++;				
				liveMode.startProcess(data, mCamera, nativeAPI);
			}
			else {
				resultField1.setText("");
                resultField2.setText("");
                resultField3.setText("");
			}
		}
    }
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
        	case R.id.scenechange: {
        		if (recording)
                    Toast.makeText(MainActivity.this, "Make sure recording is off!",Toast.LENGTH_LONG).show();     
        		else 
        			settings.changeScene();
        		return true;
        	}
        	case R.id.model: {
        		if (recording)
                    Toast.makeText(MainActivity.this, "Make sure recording is off!",Toast.LENGTH_LONG).show();     
        		else
        			//settings.changeModel();
        		return true;
        	}
        	case R.id.categories: {
        		if (recording)
                    Toast.makeText(MainActivity.this, "Make sure recording is off!",Toast.LENGTH_LONG).show();     
        		else
        			settings.viewCategories();
        		return true;
        	}
            default:
                return super.onOptionsItemSelected(item);
        }
    }

	/**
	 *
	 */
    Button.OnClickListener mySettingsOnClickListener = new Button.OnClickListener() {
        @Override
        public void onClick(View v) {
            openOptionsMenu();
        }
    }; 
}