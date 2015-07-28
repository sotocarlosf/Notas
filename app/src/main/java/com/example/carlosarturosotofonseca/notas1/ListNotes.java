package com.example.carlosarturosotofonseca.notas1;

import android.annotation.TargetApi;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;

/**
 * Created by carlosarturosotofonseca on 03/07/15.
 */
public class ListNotes extends ListActivity{
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "ListNotes menu");

        File[] list = getExternalStorageDir(this, getResources().getString(R.string.notesFile_name)).listFiles();
        String [] note_names = new String[list.length];
        int p;

        for(int i = 0; i < list.length; i++){
            p = list[i].getPath().lastIndexOf("/");
            note_names[i] = list[i].getPath().substring(p + 1);
            Log.i(TAG, "Note title: " + note_names[i]);
        }
        Log.i(TAG, "Array note_names: " + Integer.toString(note_names.length));
        String prueba = Integer.toString(note_names.length);
        Log.i(TAG, "Numero de archivos en" + getExternalStorageDir(this, "NOTES").getPath() +
                " :" + prueba);

        ArrayAdapter<String> myArrayAdapter = new ArrayAdapter<String>(
                this,
                android.R.layout.simple_list_item_1,
                note_names);

        setListAdapter(myArrayAdapter);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Intent intent = new Intent(this, DisplayNote.class);
        // The string is sent because ints cannot be sent inside intents to another activity
        String p = Integer.toString(position);
        intent.putExtra("position", p);
        startActivity(intent);
    }

    // Finds in external SD CARD the directory the entered directory
    @TargetApi(Build.VERSION_CODES.KITKAT)
    public File getExternalStorageDir(Context context, String directory_name) {
        if(!isExternalStorageReadable() || !isExternalStorageWritable()){
            Log.e(TAG, "Failed to get the parent directory");
            return null;
        }
        // Get the directory for the app's private new file
        File[] f = context.getExternalFilesDirs(null);
        for(int i = 0; i<f.length; i++){
            Log.d(TAG, "External SD card " + i + ":" + f[i].getAbsolutePath());
        }
        File file = new File (f[1], directory_name);
        if (!file.mkdirs()) {
            Log.e(TAG, "Directory not created or already exists");
        }
        return file;
    }

    /* Checks if external storage is available for read and write */
    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }
}
