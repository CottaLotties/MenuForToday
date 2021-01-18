package com.example.menuapplication;

import android.app.Application;

import androidx.room.Room;

public class App extends Application {
    public static App instance;
    private AppDatabase db;

    // App class; here we create app instance and db
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "dishes")
                .allowMainThreadQueries().build();
    }

    public static App getInstance(){
        return instance;
    }

    public AppDatabase getDatabase(){
        return db;
    }
}
