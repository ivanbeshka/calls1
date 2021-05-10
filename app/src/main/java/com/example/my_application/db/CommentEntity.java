package com.example.my_application.db;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.SET_DEFAULT;

@Entity
public class CommentEntity {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "phone")
    public String phone;

    @ColumnInfo(name = "comment")
    public String comment;

    @ColumnInfo(name = "commentPhone")
    public String commentPhone;

    @ColumnInfo(name = "date")
    public String date;
}
