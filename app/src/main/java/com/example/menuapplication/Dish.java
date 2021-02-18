package com.example.menuapplication;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
// класс-таблица для сохранения блюд
@Entity
public class Dish {

    @PrimaryKey
    public long id;

    public int type;
    /*1 - завтрак,
    2 - салат,
    3 - обед,
    4 - ужин,
    5 - дессерт
    6 - на заказ*/

    public String name;

    @NonNull
    @Override
    public String toString() {
        return this.name;
    }
}
