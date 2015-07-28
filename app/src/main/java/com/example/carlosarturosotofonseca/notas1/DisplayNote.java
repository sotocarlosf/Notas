package com.example.carlosarturosotofonseca.notas1;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.channels.FileChannel;
import java.util.Arrays;

/**
 * Created by carlosarturosotofonseca on 03/07/15.
 */
public class DisplayNote extends ActionBarActivity implements View.OnClickListener {
    private String TAG;
    Intent intent;
    private String title;
    private String note;
    private File parent_directory;
    private File current_file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Position value is gotten in int
        String value = getIntent().getStringExtra("position");
        Integer position = Integer.parseInt(value);
        Log.i(TAG, "Position: " + Integer.toString(position));

        setContentView(R.layout.activity_display_note);

        Button okNote = (Button) findViewById(R.id.okButton);
        Button deleteNote = (Button) findViewById(R.id.deleteButton);

        okNote.setOnClickListener(this);
        deleteNote.setOnClickListener(this);

        parent_directory = getExternalStorageDir(this, getResources().getString(R.string.notesFile_name));
        File[] list = parent_directory.listFiles();
        current_file = list[position];

        //Get the text that will be showed
        String note = getNote(current_file);

        //Text is allocated inside its respective field
        TextView textViewNote = (TextView) findViewById(R.id.textViewNote);
        textViewNote.setText(note);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.display_note_bar, menu);
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.okButton:
                Log.i(TAG, "OK note");
                intent = new Intent(this, ListNotes.class);
                startActivity(intent);
                break;
            case R.id.deleteButton:
                Log.i(TAG, "Delete note");
                deleteNote();
                intent = new Intent(this, ListNotes.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuMainMenu:
                intent = new Intent(this, MainMenu.class);
                startActivity(intent);
                Log.i(TAG, "Go back to MAIN_MENU");
                return true;
            case R.id.menuChangeTitle:
                newName_AlertDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);

    }

    /* Shows the dialog to write the desired title for the note, if that title already
    exists it calls the showToast()
     */
    public void newName_AlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog_save_note, null);
        builder.setTitle(R.string.noteNewTitleAlertDialog);
        builder.setView(view);
        final EditText editTitleNote = (EditText) view.findViewById(R.id.editTitleNote);

        builder.setPositiveButton(R.string.confirmAlertDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // The written value is assigned
                String newTitle = editTitleNote.getText().toString();
                Log.i(TAG, "Title edited:" + newTitle);

                // If the user doesnt write anything it will enter the default title
                if (newTitle.equals("")) {
                    Toast.makeText(((Dialog) dialog).getContext(), "New title is invalid", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "New title is invalid");
                }
                else{
                    // Here is where this dialog differs with the ADDNOTE one
                    if(checkTitleAvailability(parent_directory, newTitle)) {
                        // The note is saved
                        File f = new File(parent_directory, newTitle);
                        saveNote(newTitle, getNote(current_file));
                        current_file.delete();
                        intent = new Intent(getApplicationContext(), ListNotes.class);
                        startActivity(intent);
                    }
                    else{
                        showToast();
                    }
                }
            }

        });

        builder.setNegativeButton(R.string.cancelAlertDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Log.i(TAG, "Title canceled:" + title);
            }
        });

        editTitleNote.setHint("");
        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    /* Shows toast message to the overwriting title */
    public void showToast(){
        Toast.makeText(this, "This title already exists, try again", Toast.LENGTH_SHORT).show();
    }

    public static void copyFile(File src, File dst) throws IOException
    {
        FileChannel inChannel = new FileInputStream(src).getChannel();
        FileChannel outChannel = new FileOutputStream(dst).getChannel();
        try
        {
            inChannel.transferTo(0, inChannel.size(), outChannel);
        }
        finally
        {
            if (inChannel != null)
                inChannel.close();
            if (outChannel != null)
                outChannel.close();
        }
    }

    /*  Saves the note with the parameters entered, this having access to the global dir */
    public void saveNote (String title, String note){
        FileOutputStream fos;
        try {
            fos = openFileOutput(title, MODE_PRIVATE);
            fos.write(note.getBytes());
            fos.close();
            Log.i(TAG, "Note saved");

        } catch (IOException e) {
            Log.e(TAG, "ERROR" + Arrays.toString(e.getStackTrace()));

        } finally {
            Log.i(TAG, "TEST done");
        }

        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
    }

    /*  Gets the text from the note with the current file */
    public String getNote(File current_file){
        String note = readNote(current_file);
        Log.i(TAG, "Note is: " + note);
        setTitle(current_file.getName());
        return note;
    }

    /*  Reads the note from the file entered */
    public String readNote(File file){
        StringBuffer note = new StringBuffer("");
        /* FIS y ISR complement. InputStream read the bytes,and also can read text, but fis is
        less prone to convert badly when its about convert text. Bufferedreader is going to be used
        to read the entire line and not char by char*/
        try{
            FileInputStream fin = openFileInput(file.getName());
            InputStreamReader isr = new InputStreamReader(fin);
            BufferedReader bf = new BufferedReader(isr);

            String readString = bf.readLine();
            while(readString != null){
                note.append(readString);
                readString = bf.readLine();
            }

            isr.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return note.toString();
    }

    /* Deletes the note */
    public void deleteNote(){
        String value = getIntent().getStringExtra("position");
        Integer position = Integer.parseInt(value);

        File[] list = getExternalStorageDir(this,getResources().getString(R.string.notesFile_name)).listFiles();
        File file = list[position];
        if(file.delete()){
            Log.i(TAG, "DELETED note");
        }
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

    //Returns true if this file name is available
    public boolean checkTitleAvailability(File parent_file, String file_name){
        if(parent_file.isDirectory()){
            File [] files = parent_file.listFiles();
            for(int i = 0; i < files.length; i++){
                if(file_name.equals(files[i].getName())){
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ListNotes.class);
        startActivity(intent);
    }
}
