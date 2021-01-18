package com.example.menuapplication;

import androidx.room.Database;
import androidx.room.RoomDatabase;

// class to provide access to DAO
@Database(entities={Dish.class}, version=1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DishDao dishDao();

}
