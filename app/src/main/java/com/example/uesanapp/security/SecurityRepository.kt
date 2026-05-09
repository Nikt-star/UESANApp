package com.example.uesanapp.security

import android.content.Context
import com.example.uesanapp.model.AlertCategory
import com.example.uesanapp.model.CommunityAlert
import com.example.uesanapp.security.database.ReportedNumberEntity
import com.example.uesanapp.security.database.SecurityDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*

class SecurityRepository(context: Context) {
    private val dao = SecurityDatabase.getDatabase(context).securityDao()

    val reportedNumbers: Flow<List<CommunityAlert>> = dao.getAllReportedNumbers().map { entities ->
        entities.map { entity ->
            CommunityAlert(
                number = entity.number,
                reason = entity.reason,
                time = formatTimestamp(entity.timestamp),
                category = entity.category
            )
        }
    }

    suspend fun reportNumber(number: String, reason: String, category: AlertCategory) {
        if (number.isNotBlank()) {
            val entity = ReportedNumberEntity(
                number = number,
                reason = reason,
                timestamp = System.currentTimeMillis(),
                category = category
            )
            dao.insertReportedNumber(entity)
        }
    }

    suspend fun isNumberBlocked(number: String): Boolean {
        val normalized = normalizeNumber(number)
        // Por ahora una búsqueda exacta, se podría mejorar con LIKE si es necesario
        return dao.isNumberBlocked(normalized)
    }

    private fun normalizeNumber(number: String): String {
        return number.replace(" ", "").replace("-", "").replace("+", "")
    }

    private fun formatTimestamp(timestamp: Long): String {
        val diff = System.currentTimeMillis() - timestamp
        return when {
            diff < 60_000 -> "Recién reportado"
            diff < 3600_000 -> "Hace ${diff / 60_000} min"
            else -> SimpleDateFormat("dd/MM HH:mm", Locale.getDefault()).format(Date(timestamp))
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: SecurityRepository? = null

        fun getInstance(context: Context): SecurityRepository {
            return INSTANCE ?: synchronized(this) {
                val instance = SecurityRepository(context)
                INSTANCE = instance
                instance
            }
        }
    }
}
