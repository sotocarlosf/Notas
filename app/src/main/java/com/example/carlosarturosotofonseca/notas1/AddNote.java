package com.example.carlosarturosotofonseca.notas1;

        import android.annotation.TargetApi;
        import android.app.AlertDialog;
        import android.app.Dialog;
        import android.app.DialogFragment;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.os.Build;
        import android.os.Bundle;
        import android.os.Environment;
        import android.support.annotation.StringRes;
        import android.support.v7.app.ActionBarActivity;
        import android.util.Log;
        import android.view.KeyEvent;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.inputmethod.EditorInfo;
        import android.view.inputmethod.InputMethodManager;
        import android.widget.Button;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;

        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.OutputStream;
        import java.util.Arrays;
        import java.util.logging.XMLFormatter;
        import android.view.WindowManager.LayoutParams;

        import static java.security.AccessController.getContext;

/**
 * Created by carlosarturosotofonseca on 26/06/15.
 */
public class AddNote extends ActionBarActivity implements View.OnClickListener {

    private Intent intent;
    private String note;
    private String title = "";
    private EditText editTextNote;
    private File dir;
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        Log.i(TAG, "AddNoteMenu");

        // The directory for NOTES is created
        dir = getExternalStorageDir(this, getResources().getString(R.string.notesFile_name));
        Log.i(TAG, "Directory NOTES path: " + dir.getAbsolutePath());

        Button cancelNote = (Button) findViewById(R.id.buttonCancel);
        Button saveNote = (Button) findViewById(R.id.buttonSave);

        cancelNote.setOnClickListener(this);
        saveNote.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch(v.getId()){
            case R.id.buttonSave:
                editTextNote = (EditText)findViewById(R.id.editTextNote);
                note = editTextNote.getText().toString();
                Log.i(TAG, "Note(EditText):" + note);
                name_AlertDialog();
                break;

            case R.id.buttonCancel:
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
                Log.i(TAG, "CANCELED note");
                intent = new Intent(this, MainMenu.class);
                startActivity(intent);
                break;
        }
    }

    /* Shows the dialog to write the desired title for the note, if that title already
    exists it calls the showToast()
     */
    public void name_AlertDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        LayoutInflater inflater = this.getLayoutInflater();
        View view = inflater.inflate(R.layout.alert_dialog_save_note, null);
        builder.setTitle(R.string.noteTitleAlertDialog);
        builder.setView(view);
        final EditText editTitleNote = (EditText) view.findViewById(R.id.editTitleNote);

        builder.setPositiveButton(R.string.confirmAlertDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // The written value is assigned
                title = editTitleNote.getText().toString();
                Log.i(TAG, "Title edited:" + title);

                // If the user doesnt write anything it will enter the default title
                if (title.equals("")) {
                    title = getDefaultTitle(dir);
                    Log.i(TAG, "The new title is: " + title);
                }

                if (checkTitleAvailability(dir, title)) {
                    // The note is saved
                    File parent_directory = dir;
                    Log.i(TAG, "PD: " + parent_directory.getAbsolutePath());
                    saveNote(parent_directory, title, note);
                    Log.i(TAG, "CONFIRM pressed");
                    intent = new Intent(getApplicationContext(), MainMenu.class);
                    startActivity(intent);
                } else {
                    showToast();
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

        editTitleNote.setHint(getDefaultTitle(dir));
        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    /* Shows toast message to the overwriting title */
    public void showToast(){
        Toast.makeText(this, "This title already exists, try again", Toast.LENGTH_SHORT).show();
    }

    /*  Saves the note with the parameters entered, this having access to the global dir */
    public void saveNote (File parent_directory, String title, String note){
        try {
            File file = new File(parent_directory, title);
            FileOutputStream fos = new FileOutputStream(file);
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

    // Gets the default note NUMBER title in the directory entered
    public String getDefaultTitle(File parent_directory){
        int n;
        n = parent_directory.listFiles().length + 1;
        Log.i(TAG, "Number of note: " + Integer.toString(n));
        return "Note " + Integer.toString(n);
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
}