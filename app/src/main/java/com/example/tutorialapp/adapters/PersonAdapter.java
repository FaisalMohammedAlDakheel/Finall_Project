package com.example.tutorialapp.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.tutorialapp.AddNewPersonActivity;
import com.example.tutorialapp.LocalDbListActivity;
import com.example.tutorialapp.R;
import com.example.tutorialapp.RemoteDbListActivity;
import com.example.tutorialapp.localdatabase.PersonDatabase;
import com.example.tutorialapp.models.PersonModel;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.PersonViewHolder> {

    private Context context;
    private List<PersonModel> modelList;
    private String comingFrom;

    public PersonAdapter(Context context, List<PersonModel> modelList, String comingFrom) {
        this.context = context;
        this.modelList = modelList;
        this.comingFrom = comingFrom;
    }

    @NonNull
    @Override
    public PersonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Create a view for each item of the list
       View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item_recycler,parent,false);
        PersonViewHolder viewHolder = new PersonViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull PersonViewHolder holder, int position) {
        // Bind each view to each model(Row)
              holder.bind(modelList.get(position));
    }

    @Override
    public int getItemCount() {
        return modelList.size();
    }

    // View holder which actually handles what's inside the single view and how to arrange data of each view
    public class PersonViewHolder extends RecyclerView.ViewHolder{
        TextView tvName,tvSurname,tvEmail,tvPhone;
        ImageButton btnDelete,btnUpdate; // Images button for delete and update
        public PersonViewHolder(@NonNull View itemView) {
            super(itemView);

            tvName = itemView.findViewById(R.id.tvNameItem);
            tvSurname = itemView.findViewById(R.id.tvNickItem);
            tvEmail = itemView.findViewById(R.id.tvMailItem);
            tvPhone = itemView.findViewById(R.id.tvPhoneItem);
            btnDelete = itemView.findViewById(R.id.imgBtnDelete);
            btnUpdate = itemView.findViewById(R.id.imgBtnEdit);
        }
        // To show data appear in the display
        @SuppressLint("SetTextI18n")
        void bind(final PersonModel model){
            tvName.setText(model.getName());
            tvSurname.setText(model.getSurname());
            tvEmail.setText("Contact: "+model.getEmail());
            tvPhone.setText(model.getPhone());
            // Update button
            btnUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // An intent is an abstract description of an operation to be performed. It can be used with Context
                    Intent intent = new Intent(context, AddNewPersonActivity.class);
                    intent.putExtra("PERSON",model);
                    intent.putExtra("ComingFrom",comingFrom);
                        context.startActivity(intent);


                }
            });
            // Delete button
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showAlert(model.getId(),getAdapterPosition());
                }
            });
        }
        // Show alert message to confirm delete
        private void showAlert(final int id, final int position){
            new AlertDialog.Builder(context)
                    .setTitle("Delete")
                    .setMessage("Are you sure you want to delete?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (comingFrom.equals("LOCAL")){
                            PersonDatabase.getInstance(context).personsDAO().deletePersonById(id);
                            ((LocalDbListActivity)context).updateAdapter(position);}
                            else if (comingFrom.equals("REMOTE")){
                                deletePersonFromPhPDatabase(id,position);
                            }
                        }
                    }).setNegativeButton("No", null).show();
        }

        // Method to delete data from php Database
      private void deletePersonFromPhPDatabase(final int id, final int position){

          StringRequest deleteRequest = new StringRequest(Request.Method.POST, context.getResources().getString(R.string.delete_person_by_id), new Response.Listener<String>() {
              @Override
              public void onResponse(String response) {
                  Toast.makeText(context, response, Toast.LENGTH_SHORT).show();
                  ((RemoteDbListActivity)context).updateAdapter(position);
              }
          }, new Response.ErrorListener() {
              @Override
              public void onErrorResponse(VolleyError error) {
                  Toast.makeText(context, error.getMessage(), Toast.LENGTH_SHORT).show();
              }
          }){
              @Override
              // To get Parameters
              protected Map<String, String> getParams() throws AuthFailureError {
                  HashMap<String,String> params = new HashMap<>();

                    params.put("id",String.valueOf(id));
                  return params;
              }
          };

          RequestQueue queue = Volley.newRequestQueue(context);

          queue.add(deleteRequest);
      }


    }
}
