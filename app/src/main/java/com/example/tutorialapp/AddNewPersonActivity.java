package com.example.tutorialapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tutorialapp.localdatabase.PersonDatabase;
import com.example.tutorialapp.models.PersonModel;
import com.google.android.material.snackbar.Snackbar;

import java.util.HashMap;
import java.util.Map;

public class AddNewPersonActivity extends AppCompatActivity {
    EditText edName,edSurname,edMail,edPhone;
    Button btnLocalSave,btnRemoteSave;

    PersonDatabase database;

    PersonModel personModel;
    String comingFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_person);

        edName = findViewById(R.id.edName);
        edSurname = findViewById(R.id.edSurname);
        edMail = findViewById(R.id.edMail);
        edPhone = findViewById(R.id.edPhone);
        btnLocalSave = findViewById(R.id.btnSaveLocal);
        btnRemoteSave = findViewById(R.id.btnSaveRemote);
        // An intent is an abstract description of an operation to be performed. It can be used with Context
        Intent intent = getIntent();
         personModel = (PersonModel) intent.getSerializableExtra("PERSON");
         comingFrom = intent.getStringExtra("ComingFrom");
        // To show data appear in the display
        if (personModel!=null){
            edName.setText(personModel.getName());
            edMail.setText(personModel.getEmail());
            edSurname.setText(personModel.getSurname());
            edPhone.setText(personModel.getPhone());
            if (comingFrom.equals("LOCAL")){
                btnRemoteSave.setVisibility(View.INVISIBLE);
                btnLocalSave.setText("Update in Local DB");
            } else if (comingFrom.equals("REMOTE")){
                btnLocalSave.setVisibility(View.INVISIBLE);
                btnRemoteSave.setText("Update in Remote DB");
            }
        }


        database = PersonDatabase.getInstance(this);

        // On this button, click save the data into the local database
        btnLocalSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String name = edName.getText().toString();
                String surname = edSurname.getText().toString();
                String email = edMail.getText().toString();
                String phone = edPhone.getText().toString();

                if (name.isEmpty()){
                    edName.setError("Please enter name");
                } else if (surname.isEmpty()){
                    edSurname.setError("Please enter surName");
                } else if (email.isEmpty()){
                edMail.setError("Please enter email");
                } else if (phone.isEmpty()){
                    edPhone.setError("Please enter phone");
                } else {
                    if (personModel==null){
                    PersonModel model = new PersonModel(name,surname,email,phone);

                    database.personsDAO().insertPerson(model);

                    Snackbar.make(view,"Saved Successfully",Snackbar.LENGTH_LONG).setAction("Close", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {

                        }
                    }).show();
                    } else {
                        // The data will be updated to be automatically displayed in the display
                        personModel.setEmail(email);
                        personModel.setName(name);
                        personModel.setPhone(phone);
                        personModel.setSurname(surname);

                        database.personsDAO().updatePerson(personModel);

                        Snackbar.make(view,"Updated Successfully Successfully",Snackbar.LENGTH_LONG).setAction("Close", new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                            }
                        }).show();
                        startActivity(new Intent(AddNewPersonActivity.this,LocalDbListActivity.class));
                        finish();
                    }
                }
            }
        });

        // On click save data in PHP database
        btnRemoteSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name = edName.getText().toString();
                String surname = edSurname.getText().toString();
                String email = edMail.getText().toString();
                String phone = edPhone.getText().toString();

                if (name.isEmpty()){
                    edName.setError("Please enter name");
                } else if (surname.isEmpty()){
                    edSurname.setError("Please enter surName");
                } else if (email.isEmpty()){
                    edMail.setError("Please enter email");
                } else if (phone.isEmpty()){
                    edPhone.setError("Please enter phone");
                } else {
                    if (personModel==null){
                    PersonModel model = new PersonModel(name,surname,email,phone);
                    // Method to send data in PHP
                    sendDataToPhpDatabase(model,view);
                    } else {
                     updateDataToPhpDatabase(view);

                    }
                }
            }
        });

    }

    private void sendDataToPhpDatabase(final PersonModel model, final View view) {
         // Request  to send data
        StringRequest insertRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.insert_new_person), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // The response will be shown here for promise inserted successfully or not
                Snackbar.make(view,response,Snackbar.LENGTH_LONG).setAction("Close", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // If any Network error occurred
                Toast.makeText(AddNewPersonActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
            Snackbar.make(view,error.getMessage(),Snackbar.LENGTH_LONG).setAction("Close", new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            }).show();
            }
        }){
            @Override
            // To get Parameters
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                // Body parameters for post method that will save in PHP database table
                params.put("name",model.getName());
                params.put("surname",model.getSurname());
                params.put("email",model.getEmail());
                params.put("phone",model.getPhone());
                return params;
            }
        };

        // Que of volley requests that basically, in this case, execute the above Request
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(insertRequest);
    }
    private void updateDataToPhpDatabase( final View view) {
        // Request  to send data
        StringRequest insertRequest = new StringRequest(Request.Method.POST, getResources().getString(R.string.update_person_by_id), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                // The response will be shown here for promise inserted successfully or not
                Snackbar.make(view,response,Snackbar.LENGTH_LONG).setAction("Close", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).show();
                //startActivity(new Intent(AddNewPersonActivity.this,RemoteDbListActivity.class));
                //finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // If any Network error occurred
                Toast.makeText(AddNewPersonActivity.this, error.toString(), Toast.LENGTH_SHORT).show();
                Snackbar.make(view,error.getMessage(),Snackbar.LENGTH_LONG).setAction("Close", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                }).show();
            }
        }){
            @Override
            // To get Parameters
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> params = new HashMap<>();
                // Body parameters for post method that will save in PHP database table
                params.put("id", String.valueOf(personModel.getId()));
                params.put("name",personModel.getName());
                params.put("surname",personModel.getSurname());
                params.put("email",personModel.getEmail());
                params.put("phone",personModel.getPhone());
                return params;
            }
        };

        // Que of volley requests that basically, in this case, execute the above Request
        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(insertRequest);
    }
}
