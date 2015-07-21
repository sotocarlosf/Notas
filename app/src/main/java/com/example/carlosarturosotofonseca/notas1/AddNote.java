package com.example.carlosarturosotofonseca.notas1;

        import android.app.AlertDialog;
        import android.app.Dialog;
        import android.app.DialogFragment;
        import android.content.Context;
        import android.content.DialogInterface;
        import android.content.Intent;
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
    private String TAG;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        Log.i(TAG, "AddNoteMenu");
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
                // Espacio para el alertDialog
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

                // Se crea un objeto DIALOG para que el findVIewById busque en esta vista el editText
                Dialog dialogView = (Dialog) dialog;
                // Se asigna el valor escrito
                title = editTitleNote.getText().toString();
                Log.i(TAG, "Title edited:" + title);

                // Se asigna el valor default si no se escribe nada
                if (title.equals("")) {
                    title = getDefaultTitle();
                    Log.i(TAG, "El title es: " + title);
                }

                // Se salva el archivo
                saveNote(title, note);

                Log.i(TAG, "CONFIRM pressed");
                intent = new Intent(getApplicationContext(), MainMenu.class);
                startActivity(intent);
            }
        });

        builder.setNegativeButton(R.string.cancelAlertDialog, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                Log.i(TAG, "Title canceled:" + title);
            }
        });

        editTitleNote.setHint(getDefaultTitle());
        Dialog dialog = builder.create();
        dialog.getWindow().setSoftInputMode(LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialog.show();
    }

    public void saveNote (String title, String note){
        FileOutputStream fos;
        try {
            fos = openFileOutput(title, MODE_PRIVATE);
            fos.write(note.getBytes());
            fos.close();
            Log.i(TAG, "Note saved");

        } catch (IOException e) {
            Log.e(TAG, "ERROR" + Arrays.toString(e.getStackTrace()));

        }
        finally {
            Log.i(TAG, "TEST done");
        }

        Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
    }

    public String getDefaultTitle(){
        // Se obtiene el titulo por default
        int n = getFilesDir().listFiles().length + 1;
        return "Note " + Integer.toString(n);
    }
}
