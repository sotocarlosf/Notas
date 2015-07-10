package com.example.carlosarturosotofonseca.notas1;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
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

        File[] list = getFilesDir().listFiles();
        String [] note_names = new String[list.length];
        int p;

        for(int i = 0; i < list.length; i++){
            p = list[i].getPath().lastIndexOf("/");
            note_names[i] = list[i].getPath().substring(p + 1);
            Log.i(TAG, "Titulo de Nota: " + note_names[i]);
        }
        Log.i(TAG, "Arreglo note_names: " + Integer.toString(note_names.length));
        String prueba = Integer.toString(note_names.length);
        Log.i(TAG, "Numero de archivos en" + getFilesDir().getPath() +
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
        // Se pasa a string porque no se puede mandar ints a la actividad siguiente
        String p = Integer.toString(position);
        intent.putExtra("position", p);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this, MainMenu.class);
        startActivity(intent);
    }
}
