package com.example.my_application.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserEntity {

    @NonNull
    @PrimaryKey
    public String phone;

    @ColumnInfo(name = "password")
    public String password;
}
