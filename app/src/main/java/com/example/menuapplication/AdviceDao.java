package com.example.menuapplication;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface AdviceDao {
    @Query("SELECT * FROM Advice WHERE id = :id")
    Advice getAdvice(long id);

    @Insert
    void insertAdvice(Advice advice);

    @Query("DELETE FROM Advice WHERE id = :id")
    void removeAdvice(long id);

    @Query("UPDATE Advice SET breakfastId = :dish_id WHERE id = :id")
    void setBreakfast(long dish_id, long id);

    @Query("UPDATE Advice SET saladId = :dish_id WHERE id = :id")
    void setSalad(long dish_id, long id);

    @Query("UPDATE Advice SET dinnerId = :dish_id WHERE id = :id")
    void setDinner(long dish_id, long id);

    @Query("UPDATE Advice SET supperId = :dish_id WHERE id = :id")
    void setSupper(long dish_id, long id);

    @Query("UPDATE Advice SET dessertId = :dish_id WHERE id = :id")
    void setDessert(long dish_id, long id);

    @Query("UPDATE Advice SET orderId = :dish_id WHERE id = :id")
    void setOrder(long dish_id, long id);
}
