package com.iti.mad41.tripia.database.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.iti.mad41.tripia.database.dto.TripHistory;
import com.iti.mad41.tripia.database.dto.Notes;

import java.util.List;

@Dao
public interface TripHistoryDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(TripHistory tr);

    @Query("DELETE FROM trip_notes")
    void deleteAll();

    @Query("DELETE FROM trip_notes WHERE notesId Like :noteId")
    void deleteNote(int noteId);

    @Query("SELECT * FROM trip_notes ORDER BY notesId ASC")
    List<Notes> getAlphabetizedWords();
}