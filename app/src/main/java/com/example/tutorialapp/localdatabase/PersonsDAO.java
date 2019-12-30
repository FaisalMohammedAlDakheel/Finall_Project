package com.example.tutorialapp.localdatabase;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.tutorialapp.models.PersonModel;

import java.util.List;

// Dao is a Data Access Object Interface which actually handles SQL queries on the table(Model) that I have created in the model package

@Dao
public interface PersonsDAO {

    @Query("SELECT * FROM Tbl_Persons")
    List<PersonModel> getAllPersons();

    @Query("SELECT * FROM Tbl_Persons WHERE name=:name")
    List<PersonModel> getSearchedPersonsByName(String name);


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertPerson(PersonModel model);



    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updatePerson(PersonModel model);

   @Query("DELETE FROM Tbl_Persons WHERE id=:id")
   void deletePersonById(int id);

}
