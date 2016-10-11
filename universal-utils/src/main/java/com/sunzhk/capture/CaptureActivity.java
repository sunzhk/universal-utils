package com.sunzhk.capture;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.Window;
import android.view.WindowManager;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.DecodeHintType;
import com.google.zxing.Result;
import com.sunzhk.R;
import com.sunzhk.capture.camera.CameraManager;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;

/**
 * 二维码扫描主类</br>
 * 使用CaptureActivity.startForResult(Activity activity)获得result。resultCode为CaptureActivity.CAPTURE_RESULT_CODE</br>
 * 或CaptureActivity.startWithCallBack(Activity activity, AfterCapture AfterCapture)回调</br>
 * 使用需在工程中引用zxing-core-3.2.1.jar
 * @author sunzhk
 *
 */
public class CaptureActivity extends Activity implements SurfaceHolder.Callback {

	public static final int CAPTURE_RESULT_CODE = 0xF0000001;
	
	private static final String TAG = CaptureActivity.class.getSimpleName();
	private CameraManager cameraManager;
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Collection<BarcodeFormat> decodeFormats;
	private Map<DecodeHintType,?> decodeHints;
	private String characterSet;
	
	private static AfterCapture mAfterCapture;

	ViewfinderView getViewfinderView() {
		return viewfinderView;
	}

	  public Handler getHandler() {
	    return handler;
	  }

	  CameraManager getCameraManager() {
	    return cameraManager;
	  }

	  @Override
	  public void onCreate(Bundle icicle) {
	    super.onCreate(icicle);
	    Window window = getWindow();
	    window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	    setContentView(R.layout.activity_capture);

	    hasSurface = false;

	  }

	  @Override
	  protected void onResume() {
	    super.onResume();
	    cameraManager = new CameraManager(getApplication());

	    viewfinderView = (ViewfinderView) findViewById(R.id.activitycapture_vfv_finder);
	    viewfinderView.setCameraManager(cameraManager);

	    handler = null;

//	    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

//	    if (prefs.getBoolean(PreferencesActivity.KEY_DISABLE_AUTO_ORIENTATION, true)) {
//	      setRequestedOrientation(getCurrentOrientation());
//	    } else {
//	      setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
//	    }

	    Intent intent = getIntent();

	    decodeFormats = null;
	    characterSet = null;

	    if (intent != null) {

	      String action = intent.getAction();
	      String dataString = intent.getDataString();

	      if (Intents.Scan.ACTION.equals(action)) {

	        decodeFormats = DecodeFormatManager.parseDecodeFormats(intent);
	        decodeHints = DecodeHintManager.parseDecodeHints(intent);

	        if (intent.hasExtra(Intents.Scan.WIDTH) && intent.hasExtra(Intents.Scan.HEIGHT)) {
	          int width = intent.getIntExtra(Intents.Scan.WIDTH, 0);
	          int height = intent.getIntExtra(Intents.Scan.HEIGHT, 0);
	          if (width > 0 && height > 0) {
	            cameraManager.setManualFramingRect(width, height);
	          }
	        }

	        if (intent.hasExtra(Intents.Scan.CAMERA_ID)) {
	          int cameraId = intent.getIntExtra(Intents.Scan.CAMERA_ID, -1);
	          if (cameraId >= 0) {
	            cameraManager.setManualCameraId(cameraId);
	          }
	        }
	        
	      } else if (dataString != null &&
	                 dataString.contains("http://www.google") &&
	                 dataString.contains("/m/products/scan")) {

	        decodeFormats = DecodeFormatManager.PRODUCT_FORMATS;

	      } 

	      characterSet = intent.getStringExtra(Intents.Scan.CHARACTER_SET);

	    }

	    SurfaceView surfaceView = (SurfaceView) findViewById(R.id.activitycapture_sv_camera);
	    SurfaceHolder surfaceHolder = surfaceView.getHolder();
	    if (hasSurface) {
	      initCamera(surfaceHolder);
	    } else {
	      surfaceHolder.addCallback(this);
	    }
	  }

//	  private int getCurrentOrientation() {
//	    int rotation = getWindowManager().getDefaultDisplay().getRotation();
//	    switch (rotation) {
//	      case Surface.ROTATION_0:
//	      case Surface.ROTATION_90:
//	        return ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
//	      default:
//	        return ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
//	    }
//	  }
	  
	  @Override
	  protected void onPause() {
	    if (handler != null) {
	      handler.quitSynchronously();
	      handler = null;
	    }
	    cameraManager.closeDriver();
	    if (!hasSurface) {
	      SurfaceView surfaceView = (SurfaceView) findViewById(R.id.activitycapture_sv_camera);
	      SurfaceHolder surfaceHolder = surfaceView.getHolder();
	      surfaceHolder.removeCallback(this);
	    }
	    super.onPause();
	  }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				Intent intent = new Intent();
				intent.putExtra("code", "");
				setResult(CAPTURE_RESULT_CODE, intent);
				finish();
			case KeyEvent.KEYCODE_CAMERA:
				return true;
			case KeyEvent.KEYCODE_VOLUME_DOWN:
				cameraManager.setTorch(false);
				return true;
			case KeyEvent.KEYCODE_VOLUME_UP:
				cameraManager.setTorch(true);
				return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	  @Override
	  public void surfaceCreated(SurfaceHolder holder) {
	    if (holder == null) {
	      Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
	    }
	    if (!hasSurface) {
	      hasSurface = true;
	      initCamera(holder);
	    }
	  }

	  @Override
	  public void surfaceDestroyed(SurfaceHolder holder) {
	    hasSurface = false;
	  }

	  @Override
	  public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	  }
	  
	  /**
	   * 返回结果
	   * @param rawResult
	   */
	public void handleDecode(Result rawResult) {
		Log.d("capResult", rawResult.getText());
		if(mAfterCapture != null){
			mAfterCapture.doAfterCapture(rawResult.getText());
			mAfterCapture = null;
		}else{
			Intent intent = new Intent();
			intent.putExtra("code", rawResult.getText());
			setResult(CAPTURE_RESULT_CODE, intent);
		}
		finish();
	  }

	  private void initCamera(SurfaceHolder surfaceHolder) {
	    if (surfaceHolder == null) {
	      throw new IllegalStateException("No SurfaceHolder provided");
	    }
	    if (cameraManager.isOpen()) {
	      return;
	    }
	    try {
	      cameraManager.openDriver(surfaceHolder);
	      if (handler == null) {
	        handler = new CaptureActivityHandler(this, decodeFormats, decodeHints, characterSet, cameraManager);
	      }
	    } catch (IOException ioe) {
	      Log.w(TAG, ioe);
	      displayFrameworkBugMessageAndExit();
	    } catch (RuntimeException e) {
	      Log.w(TAG, "Unexpected error initializing camera", e);
	      displayFrameworkBugMessageAndExit();
	    }
	  }




	private void displayFrameworkBugMessageAndExit() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle(getString(R.string.app_name));
		builder.setMessage(getString(R.string.msg_camera_framework_bug));
		builder.setPositiveButton(R.string.button_ok, new FinishListener(this));
		builder.setOnCancelListener(new FinishListener(this));
		builder.show();
	}

	public void drawViewfinder() {
		viewfinderView.drawViewfinder();
	}

	public static interface AfterCapture{
		void doAfterCapture(String result);
	}
	
	public static void startForResult(Activity activity){
		activity.startActivityForResult(new Intent(activity, CaptureActivity.class), CAPTURE_RESULT_CODE);
	}
	
	public static void startWithCallBack(Activity activity, AfterCapture AfterCapture){
		mAfterCapture = AfterCapture;
		activity.startActivity(new Intent(activity, CaptureActivity.class));
	}

}
