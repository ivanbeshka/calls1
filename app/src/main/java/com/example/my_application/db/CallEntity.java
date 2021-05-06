package com.example.my_application.db;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import org.jetbrains.annotations.NotNull;

@Entity
public class CallEntity {
    @PrimaryKey
    @NotNull
    public String phone;

    @ColumnInfo(name = "tags")
    public String tags;

    @ColumnInfo(name = "isSpam")
    public boolean isSpam;
}
