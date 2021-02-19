package com.example.menuapplication;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

// entity to keep the "saved" current advice
@Entity
public class Advice {

    @PrimaryKey
    public long id;

    public long breakfastId;
    public long saladId;
    public long dinnerId;
    public long supperId;
    public long dessertId;
    public long orderId;
}
