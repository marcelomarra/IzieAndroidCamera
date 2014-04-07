package br.com.izie.android.camera.library;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import br.com.izie.android.camera.library.view.CameraPreview;

public class CameraActivity extends Activity {

    private static final String TAG = "CameraActivity";

    public static Camera getCameraInstance() {
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_layout);

        // Create an instance of Camera
        mCamera = getCameraInstance();

        // Create our Preview view and set it as the content of our activity.
        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // get an image from the camera
                        mCamera.takePicture(null, null, mPicture);
                    }
                }
        );
        // get Camera parameters
        Camera.Parameters params = mCamera.getParameters();
// set the focus mode
        params.setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO);
// set Camera parameters
        params.setPictureSize(getIntent().getIntExtra("outputWidth", 1024), getIntent().getIntExtra("outputWidth", 768));
        mCamera.setParameters(params);
    }

    public String convertMediaUriToPath(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        cursor.close();
        return path;
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {
            String pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, pictureFile);
            Uri uri = getContentResolver().insert(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            if (pictureFile == null) {
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return;
            }
            try {
                FileOutputStream fos = new FileOutputStream(convertMediaUriToPath(uri));
                fos.write(data);
                fos.close();
                getIntent().setData(uri);
                setResult(RESULT_OK, getIntent());
                finish();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    /**
     * Create a File for saving an image or video
     */
    private String getOutputMediaFile(int type) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        if (type == MEDIA_TYPE_IMAGE) {
            return "IMG_" + timeStamp + ".jpg";
        } else if (type == MEDIA_TYPE_VIDEO) {
            return "VID_" + timeStamp + ".mp4";
        }
        return null;
    }

    private Camera mCamera;
    private SurfaceView mPreview;
    private MediaRecorder mMediaRecorder;


    @Override
    protected void onPause() {
        super.onPause();
        releaseMediaRecorder();       // if you are using MediaRecorder, release it first
        releaseCamera();              // release the camera immediately on pause event
    }

    private void releaseMediaRecorder() {
        if (mMediaRecorder != null) {
            mMediaRecorder.reset();   // clear recorder configuration
            mMediaRecorder.release(); // release the recorder object
            mMediaRecorder = null;
            mCamera.lock();           // lock camera for later use
        }
    }

    private void releaseCamera() {
        if (mCamera != null) {
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }


    public static void showTakePicture(Intent intent, Activity activity, int requestCode) {
        intent.setClass(activity, CameraActivity.class);
        activity.startActivityForResult(intent, requestCode);
    }

}