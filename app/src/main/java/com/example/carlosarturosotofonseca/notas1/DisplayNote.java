package com.example.carlosarturosotofonseca.notas1;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import java.util.Arrays;

/**
 * Created by carlosarturosotofonseca on 03/07/15.
 */
public class DisplayNote extends ActionBarActivity implements View.OnClickListener {
    private String TAG;
    Intent intent;
    private String title;
    private String note;
    String w;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Se obtiene el valor de position en int
        String value = getIntent().getStringExtra("position");
        Integer position = Integer.parseInt(value);
        Log.i(TAG, "Position: " + Integer.toString(position));

        setContentView(R.layout.activity_display_note);

        Button okNote = (Button) findViewById(R.id.okButton);
        Button deleteNote = (Button) findViewById(R.id.deleteButton);

        okNote.setOnClickListener(this);
        deleteNote.setOnClickListener(this);

        //Se adquiere el texto que se mostrara

        String note = getNote(position);

        //Se asigna el texto al campo en el que se mostrara
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

                // Se crea un objeto DIALOG para que el findVIewById busque en esta vista el editText
                Dialog dialogView = (Dialog) dialog;
                // Se asigna el valor escrito
                String newTitle = editTitleNote.getText().toString();
                Log.i(TAG, "Title edited:" + newTitle);

                // Se asigna el valor default si no se escribe nada
                if (newTitle.equals("")) {
                    Toast.makeText(((Dialog) dialog).getContext(), "Write something!!", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "El title es invalido");
                }
                else{
                    // Se salva el archivo
                    String value = getIntent().getStringExtra("position");
                    note = getNote(Integer.parseInt(value));
                    deleteNote();
                    saveNote(newTitle, note);

                    Log.i(TAG, "CONFIRM pressed");
                    intent = new Intent(getApplicationContext(), ListNotes.class);
                    startActivity(intent);
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

    public String getNote(int position){
        File[] list = getFilesDir().listFiles();
        File file = list[position];
        String note = readNote(file);
        Log.i(TAG, "Note is: " + note);
        setTitle(file.getName());
        return note;
    }

    public String readNote(File file){
        StringBuffer note = new StringBuffer("");
        // FIS y ISR se complementan. InputStream lee los bytes, y tambien puede leer texto, pero
        // fis es menos propenso a convertir mal cuando se trata de texto
        // El bufferedreader se usara para leer la linea de texto completa y no de char por char
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

    public void deleteNote(){
        String value = getIntent().getStringExtra("position");
        Integer position = Integer.parseInt(value);

        File[] list = getFilesDir().listFiles();
        File file = list[position];
        file.delete();
        Log.i(TAG, "DELETED note");
    }

    public void changeTitle(){

    }
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, ListNotes.class);
        startActivity(intent);
    }
}
