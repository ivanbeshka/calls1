package com.example.my_application.db;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface CommentDao {
    @Query("SELECT * FROM commententity")
    List<CommentEntity> getAll();

    @Query("SELECT * FROM commententity WHERE phone == :phone")
    List<CommentEntity> getComments(String phone);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(CommentEntity... comments);

//    @Query("DELETE FROM commententity WHERE phone == :phone")
//    void delete(String phone);
}
