package com.example.my_application.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {CallEntity.class, CommentEntity.class, UserEntity.class}, version = 1)
public abstract class AppDB extends RoomDatabase {
    public abstract CallDao callDao();
    public abstract CommentDao commentDao();
    public abstract UserDao userDao();
}
