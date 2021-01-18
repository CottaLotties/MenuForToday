package com.example.menuapplication;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DishDao {

    @Query("SELECT name FROM Dish WHERE type = :type")
    List<String> getByType(int type);

    @Query("SELECT name FROM Dish WHERE id = :id")
    String getNameById(long id);

    @Query("SELECT * FROM Dish WHERE type = :type")
    List<Dish> getAllByType(int type);

    @Query("UPDATE Dish SET name = :name WHERE id = :id")
    void setDish(String name, long id);

    @Insert
    void insert(Dish dish);

    @Query("DELETE FROM Dish WHERE id = :id")
    void deleteById(long id);

    @Query("SELECT MAX(id) FROM Dish")
    int getMaxId();
}
