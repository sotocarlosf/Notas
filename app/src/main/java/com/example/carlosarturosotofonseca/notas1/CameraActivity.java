package com.example.carlosarturosotofonseca.notas1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by carlosarturosotofonseca on 13/07/15.
 */
public class CameraActivity extends Activity implements View.OnClickListener {
    private static final String TAG = "CameraActivityTest";
    Camera mCamera;
    CameraPreview mPreview;
    Activity act;
    Context ctx;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        act = this;
        setContentView(R.layout.activity_add_photonote);

        Button captureButton = (Button) findViewById(R.id.button_capture);
        Button imgClose = (Button) findViewById(R.id.buttonClose);
        captureButton.setOnClickListener(this);
        imgClose.setOnClickListener(this);

        mPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
        preview.addView(mPreview);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.button_capture:
                mCamera.takePicture(shutterCallback, rawCallback, jpegCallback);
                break;
            case R.id.buttonClose:
                onPause();
                Intent intent = new Intent(this, MainMenu.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
            try{
                mCamera = Camera.open();
                mCamera.startPreview();
                mPreview.setCamera(mCamera);
            } catch (RuntimeException ex){
                Toast.makeText(ctx, "Camera not found", Toast.LENGTH_LONG).show();
            }
    }

    @Override
    protected void onPause() {
        if(mCamera != null) {
            mCamera.stopPreview();
            mPreview.setCamera(null);
            mCamera.release();
            mCamera = null;
        }
        super.onPause();
    }

    private void resetCam() {
        mCamera.startPreview();
        mPreview.setCamera(mCamera);
    }

    private void refreshGallery(File file) {
        Intent mediaScanIntent = new Intent( Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(file));
        sendBroadcast(mediaScanIntent);
    }

    Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            new SaveImageTask().execute(data);
            resetCam();
            Log.d(TAG, "onPictureTaken - jpeg");
        }
    };

    Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            //			 Log.d(TAG, "onShutter'd");
        }
    };

    Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            //			 Log.d(TAG, "onPictureTaken - raw");
        }
    };

    private class SaveImageTask extends AsyncTask<byte[], Void, Void> {

        @Override
        protected Void doInBackground(byte[]... data) {
            FileOutputStream outStream = null;

            // Write to SD Card
            try {
                File sdCard = Environment.getExternalStorageDirectory();
                File dir = new File (sdCard.getAbsolutePath() + "/Notas");
                dir.mkdirs();

                String fileName = String.format("%d.jpg", System.currentTimeMillis());
                File outFile = new File(dir, fileName);

                outStream = new FileOutputStream(outFile);
                outStream.write(data[0]);
                outStream.flush();
                outStream.close();

                Log.d(TAG, "onPictureTaken - wrote bytes: " + data.length + " to " + outFile.getAbsolutePath());

                refreshGallery(outFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
            }
            return null;
        }

    }
}