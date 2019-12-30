package com.example.tutorialapp.models;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;


// Person Model with parameters and also a table for room database with its columns

// Annotation to make a model table in room database
@Entity(tableName = "Tbl_Persons")
public class PersonModel implements Serializable {

    // Make id primary key in database case
  @PrimaryKey(autoGenerate = true)
  private int id;

   private String name,surname,email,phone;

   // The room requires only one good constructor so for money constructors in model ignore them from the database instead of one
   @Ignore
    public PersonModel(int id, String name, String surname, String email, String phone) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
    }

    public PersonModel(String name, String surname, String email, String phone) {
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
