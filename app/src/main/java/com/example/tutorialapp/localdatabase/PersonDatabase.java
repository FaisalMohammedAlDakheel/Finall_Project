package com.example.tutorialapp.localdatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.tutorialapp.models.PersonModel;

// Create a database and assign a table to it
@Database(entities = {PersonModel.class}, version = 1)
public abstract class PersonDatabase extends RoomDatabase {

    // Dao is a Data Access Object Interface which actually handles SQL queries on the table(Model) that I have created in the model package

    // Initialize Dao Interface to work with database
    public abstract PersonsDAO personsDAO();

    // Create Instance of DB on the SINGLETON Pattern to access one Instance(Object) of the database from multiple classes(activities etc)
    private static PersonDatabase INSTANCE;
    // To getInstance of DB
    public static PersonDatabase getInstance(Context context){

        if (INSTANCE==null){

            synchronized (PersonDatabase.class){
            // Initialize instance
                if (INSTANCE==null){
                    INSTANCE= Room.databaseBuilder(
                            context.getApplicationContext(),
                            PersonDatabase.class,
                            "PersonsDB")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }

        return INSTANCE;
    }

    public static void destroyInstance(){
        INSTANCE=null;
    }
}
