package com.example.menuapplication;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface AdviceDao {
    @Query("SELECT * FROM Advice WHERE id = 0")
    Advice getAdvice();

    @Insert
    void insertAdvice(Advice advice);

    @Query("DELETE FROM Advice WHERE id = 0")
    void removeAdvice();

    @Query("UPDATE Advice SET breakfastId = :dish_id WHERE id = 0")
    void setBreakfast(long dish_id);

    @Query("UPDATE Advice SET saladId = :dish_id WHERE id = 0")
    void setSalad(long dish_id);

    @Query("UPDATE Advice SET dinnerId = :dish_id WHERE id = 0")
    void setDinner(long dish_id);

    @Query("UPDATE Advice SET supperId = :dish_id WHERE id = 0")
    void setSupper(long dish_id);

    @Query("UPDATE Advice SET dessertId = :dish_id WHERE id = 0")
    void setDessert(long dish_id);

    @Query("UPDATE Advice SET orderId = :dish_id WHERE id = 0")
    void setOrder(long dish_id);
}
