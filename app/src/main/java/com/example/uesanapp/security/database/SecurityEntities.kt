package com.example.uesanapp.security.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.uesanapp.model.AlertCategory

@Entity(tableName = "reported_numbers")
data class ReportedNumberEntity(
    @PrimaryKey val number: String,
    val reason: String,
    val timestamp: Long,
    val category: AlertCategory
)
