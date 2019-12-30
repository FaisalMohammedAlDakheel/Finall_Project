package com.example.tutorialapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tutorialapp.adapters.PersonAdapter;
import com.example.tutorialapp.localdatabase.PersonDatabase;
import com.example.tutorialapp.models.PersonModel;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
Button btnLocal,btnRemote,btnSyncDs;
List<PersonModel> localRecordsList;
List<PersonModel> remoteRecordsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnLocal = findViewById(R.id.btnGoLocalList);
        btnRemote = findViewById(R.id.btnGoRemoteList);
        btnSyncDs = findViewById(R.id.btnSyncDbs);

        localRecordsList = PersonDatabase.getInstance(this).personsDAO().getAllPersons();
        remoteRecordsList = new ArrayList<>(); // Each ArrayList instance has a capacity. The capacity is the size of the array used to store the elements in the list.

        // On click go to local list activity
        btnLocal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,LocalDbListActivity.class));
            }
        });

        // On click go to Remote list activity
        btnRemote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,RemoteDbListActivity.class));
            }
        });


        /*
        // on click perform sync between local and remote DB. For this, we create a method
        // in this method, we check records(rows) on the basis of the phone number(unique for everyone)
         first, there are loops to check it from local and send to remote if not exist and vise versa*/
       btnSyncDs.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               localRecordsList = PersonDatabase.getInstance(MainActivity.this).personsDAO().getAllPersons();
               remoteRecordsList = new ArrayList<>(); // Each ArrayList instance has a capacity. The capacity is the size of the array used to store the elements in the list.
              getAllPersonsFromPHPDatabase(); // Get all data from PHP database


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

                        remoteRecordsList.add(model);
                    }
                    // It will display on the screen after we click on sync button
                    Toast.makeText(MainActivity.this, "Local size: "+localRecordsList.size(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(MainActivity.this, "Remote size: "+remoteRecordsList.size(), Toast.LENGTH_SHORT).show();

                    doSynckForDbs();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
                // Toast.makeText(RemoteDbListActivity.this, response, Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        // Get all data(persons)request
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(getAllPersonsRequest);
    }

    private void doSynckForDbs() {
        // Check in local data each element contain remote if not send it to a remote database
try {


        if (remoteRecordsList.size()>0){
        for (PersonModel modelLocal: localRecordsList){
            for (PersonModel modelRemote: remoteRecordsList){
                if (!modelLocal.getPhone().equals(modelRemote.getPhone())){
                   // insert it in remote db
                    Toast.makeText(this, modelLocal.getName() +" not in remote", Toast.LENGTH_SHORT).show();
                    sendDataToPhpDatabase(modelLocal);


                }
            }
        }
        } else {
            if (localRecordsList.size()>0)
            sendDataToPhpDatabase(localRecordsList.get(0));
        }
} catch (Exception e){

}
        // check in remote db contain  local if not then insert in local
      try {


        if (localRecordsList.size()>0){
        for (PersonModel modelRemote: remoteRecordsList){
            for (PersonModel modelLocal: localRecordsList){
                if (!modelRemote.getPhone().equals(modelLocal.getPhone())){
                    // insert it in local db
                    Toast.makeText(this, modelRemote.getName()+" not in local", Toast.LENGTH_SHORT).show();
                    PersonDatabase.getInstance(this).personsDAO().insertPerson(modelRemote);
                    localRecordsList.add(modelRemote);
                }
            }
        }
        } else {
            if (remoteRecordsList.size()>0){
                PersonDatabase.getInstance(this).personsDAO().insertPerson(remoteRecordsList.get(0));
            localRecordsList.add(remoteRecordsList.get(0));}
        }
      } catch (Exception e){

      }
    }

    private void sendDataToPhpDatabase(final PersonModel model) {

        StringRequest insertRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.insert_new_person), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                remoteRecordsList.add(model);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();

            }
        }){
            @Override
            // To get Parameters
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                params.put("name",model.getName());
                params.put("surname",model.getSurname());
                params.put("email",model.getEmail());
                params.put("phone",model.getPhone());
                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(insertRequest);
    }
}
