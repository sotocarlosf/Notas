package com.example.carlosarturosotofonseca.notas1;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

/**
 * Created by carlosarturosotofonseca on 24/06/15.
 */
public class MainMenu extends Activity implements View.OnClickListener{
    private String TAG;
    Button buttonAddNote, buttonAddAudio, buttonAddPhoto, buttonMyNotes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonAddNote = (Button) findViewById(R.id.buttonAddNote);
        buttonAddAudio = (Button) findViewById(R.id.buttonAddAudio);
        buttonAddPhoto = (Button) findViewById(R.id.buttonAddPhoto);
        buttonMyNotes = (Button) findViewById(R.id.buttonMyNotes);

        buttonAddNote.setOnClickListener(this);
        buttonAddAudio.setOnClickListener(this);
        buttonAddPhoto.setOnClickListener(this);
        buttonMyNotes.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu_bar, menu);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        String message = "in onStart";
        Log.i(TAG, message);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String message = "in onRestart";
        Log.i(TAG, message);
    }

    @Override
    protected void onStop() {
        super.onStop();
        String message = "in onStop";
        Log.i(TAG, message);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String message = "in onDestroy";
        Log.i(TAG, message);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonAddNote:
                Toast.makeText(this, "Add note", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Add note");
                Intent intentAddNote= new Intent(this, AddNote.class);
                startActivity(intentAddNote);
                break;
            case R.id.buttonAddPhoto:
                if(checkCameraHardware(this)){
                    Toast.makeText(this, "Add photo", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "numero de cam" + Integer.toString(Camera.getNumberOfCameras()));
                    Intent intentAddCameraNote = new Intent(this, AddPhotoNote.class);
                    startActivity(intentAddCameraNote);
                }
                else
                    Toast.makeText(this, "Your phone does not have a camera", Toast.LENGTH_SHORT).show();
                break;
            case R.id.buttonAddAudio:
                Intent intentAddAudioNote = new Intent(this, AddAudioNote.class);
                startActivity(intentAddAudioNote);
                Toast.makeText(this, "Add audio", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "Add AUDIO/NOTE");
                break;
            case R.id.buttonMyNotes:
                Toast.makeText(this, "My notes", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "VIEW my notes");
                Intent intentListNotes = new Intent(this, ListNotes.class);
                startActivity(intentListNotes);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menuSettings:
                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuAbout:
                Toast.makeText(this, "About it", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }
}
