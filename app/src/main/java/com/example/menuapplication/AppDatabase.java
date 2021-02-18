package com.example.menuapplication;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

// class to provide access to DAO
@Database(entities={Dish.class, Advice.class}, version=1)
public abstract class AppDatabase extends RoomDatabase {

    public abstract DishDao dishDao();
    public abstract AdviceDao adviceDao();

}
