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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tutorialapp.adapters.PersonAdapter;
import com.example.tutorialapp.models.PersonModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RemoteDbListActivity extends AppCompatActivity {

    FloatingActionButton fabAddNewUser;
    RecyclerView recyclerPersons;
    PersonAdapter adapter;
    List<PersonModel> modelList;

    EditText edSearch;
    ImageButton btnSearch;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_remote_db_list);

        fabAddNewUser = findViewById(R.id.fabAddNewUserR);
        recyclerPersons = findViewById(R.id.recyclerRemoteDb);
        edSearch = findViewById(R.id.edSearchRemote);
        btnSearch = findViewById(R.id.btnSearchRemote);

        /*Again use recycler, adapter, and modellist to show data*/
        recyclerPersons.setLayoutManager(new LinearLayoutManager(this));

        modelList = new ArrayList<>(); // Each ArrayList instance has a capacity. The capacity is the size of the array used to store the elements in the list.

        // Call this method to get data from PHP database via Volley dependency which works on UI thread(no need of Asynck Task).
        getAllPersonsFromPHPDatabase();


        // Perform a search in remote DB on the base of name
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edSearch.getText().toString();
                if (name.isEmpty()){
                    Toast.makeText(RemoteDbListActivity.this, "Search text is empty", Toast.LENGTH_SHORT).show();
                } else {
                    getSearchedPersonsFromPHPDatabase(name);
                }
            }
        });


        fabAddNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RemoteDbListActivity.this,AddNewPersonActivity.class));

            }
        });

    }
    // Get all data from PHP database
    private void getAllPersonsFromPHPDatabase(){
        // All data is requested from the database and written link for the database
        StringRequest getAllPersonsRequest = new StringRequest(Request.Method.GET, getResources().getString(R.string.get_all_person), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Get a response in the form of string which actually holds JSON from data we get JSON data from String
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                        int id = Integer.parseInt(jsonObject.getString("id"));
                        String name = jsonObject.getString("name");
                        String surname = jsonObject.getString("surname");
                        String email = jsonObject.getString("email");
                        String phone = jsonObject.getString("phone");

                        PersonModel model = new PersonModel(id,name,surname,email,phone);

                        modelList.add(model);
                    }

                    adapter = new PersonAdapter(RemoteDbListActivity.this,modelList,"REMOTE");
                    recyclerPersons.setAdapter(adapter);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
               // Toast.makeText(RemoteDbListActivity.this, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RemoteDbListActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(getAllPersonsRequest);
    }
    private void getSearchedPersonsFromPHPDatabase(final String name){
                modelList.clear();
                adapter.notifyDataSetChanged();
             // Create a request to fetch data with Post method and IP of the server with port and linked PHP file
        StringRequest getAllPersonsRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.search_person_by_name), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // Get a response in the form of string which actually hold JSON from data we get JSON data from String
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    for (int i=0; i<jsonArray.length();i++){
                        JSONObject jsonObject = (JSONObject) jsonArray.get(i);

                        int id = Integer.parseInt(jsonObject.getString("id"));
                        String name = jsonObject.getString("name");
                        String surname = jsonObject.getString("surname");
                        String email = jsonObject.getString("email");
                        String phone = jsonObject.getString("phone");

                        // Load String data into the model and add model to list
                        PersonModel model = new PersonModel(id,name,surname,email,phone);

                        modelList.add(model);
                    }

                    // Told the adapter that its dataset(List) has changed so it will update itself(List UI update)
                   adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Toast.makeText(RemoteDbListActivity.this, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(RemoteDbListActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                params.put("name",name);
               return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(getAllPersonsRequest);
    }

    // Update on data change inside adapter
    public void updateAdapter(int position){
        modelList.remove(position);
        adapter.notifyDataSetChanged();
    }
}
