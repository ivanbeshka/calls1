package com.example.my_application.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CallDao {
    @Query("SELECT * FROM callentity")
    List<CallEntity> getAll();

    @Query("SELECT * FROM callentity WHERE phone == :phone")
    CallEntity getCall(String phone);

    @Insert
    void insertAll(CallEntity... calls);

    @Query("DELETE FROM callentity WHERE phone == :phone")
    void delete(String phone);
}
