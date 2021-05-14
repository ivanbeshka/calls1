package com.example.my_application.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

import static androidx.room.OnConflictStrategy.REPLACE;

@Dao
public interface UserDao {

    @Query("SELECT * FROM userentity WHERE phone == :phone")
    List<UserEntity> getUser(String phone);

    @Insert(onConflict = REPLACE)
    void insertUser(UserEntity user);
}
