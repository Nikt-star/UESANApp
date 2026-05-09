package com.example.uesanapp.security.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SecurityDao {
    @Query("SELECT * FROM reported_numbers ORDER BY timestamp DESC")
    fun getAllReportedNumbers(): Flow<List<ReportedNumberEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReportedNumber(entity: ReportedNumberEntity)

    @Delete
    suspend fun deleteReportedNumber(entity: ReportedNumberEntity)

    @Query("SELECT EXISTS(SELECT 1 FROM reported_numbers WHERE number = :number)")
    suspend fun isNumberBlocked(number: String): Boolean
}
