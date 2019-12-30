package com.example.tutorialapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.tutorialapp.adapters.PersonAdapter;
import com.example.tutorialapp.localdatabase.PersonDatabase;
import com.example.tutorialapp.models.PersonModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class LocalDbListActivity extends AppCompatActivity {
FloatingActionButton fabAddNew;
RecyclerView recyclerLocal;
PersonAdapter adapter;
List<PersonModel> modelList;

EditText edSearch;
ImageButton btnSearch;

PersonDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_db_list);

        fabAddNew = findViewById(R.id.fabAddNewUser);
        recyclerLocal = findViewById(R.id.recyclerLocalDb);
        edSearch = findViewById(R.id.edSearchLocal);
        btnSearch = findViewById(R.id.btnSearchLocal);

        /*I add recyclerView(an advanced form of listView) and its adapter with the custom layout to fetch data from local
         * (Room database) in the form of modelList, pass it to adapter and attach adapter to recycler*/

        recyclerLocal.setLayoutManager(new LinearLayoutManager(this));

        database = PersonDatabase.getInstance(this);

        // Here is line for getting List of data(rows in the table) from Room database, Also Room return data in POJO (Plain Old java objects)
        // That's way it's easy to implement with Java classes
        modelList = database.personsDAO().getAllPersons();

        adapter = new PersonAdapter(this,modelList,"LOCAL");
        recyclerLocal.setAdapter(adapter);


        // Perform a search in local DB on the base of name
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edSearch.getText().toString();
                if (name.isEmpty()){
                    Toast.makeText(LocalDbListActivity.this, "Search text is empty", Toast.LENGTH_SHORT).show();
                } else {
                    modelList.clear();
                    adapter.notifyDataSetChanged();
                   modelList = database.personsDAO().getSearchedPersonsByName(name);
                    adapter = new PersonAdapter(LocalDbListActivity.this,modelList,"LOCAL");
                    recyclerLocal.setAdapter(adapter);
                }
            }
        });



        // Add new record
        fabAddNew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LocalDbListActivity.this,AddNewPersonActivity.class));
            }
        });
    }

    // Update adapter data in the case of change. e.g delete some row etc.
    public void updateAdapter(int position){
        modelList.remove(position);
        adapter.notifyDataSetChanged();
    }
}
