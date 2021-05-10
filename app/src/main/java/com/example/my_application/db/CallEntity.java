package com.example.my_application.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity
public class CallEntity {

    @NonNull
    @PrimaryKey
    public String phone;

    @ColumnInfo(name = "tags")
    public String tags;

    @ColumnInfo(name = "isSpam")
    public boolean isSpam;

    @ColumnInfo(name = "location")
    public String location;
}
