package com.example.menuapplication;

import androidx.room.Dao;
import androidx.room.Delete;
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
}
