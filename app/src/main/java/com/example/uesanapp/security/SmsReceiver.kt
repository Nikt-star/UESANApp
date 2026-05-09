package com.example.uesanapp.security

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.widget.Toast
import com.example.uesanapp.model.AlertCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SmsReceiver : BroadcastReceiver() {
    private val receiverScope = CoroutineScope(Dispatchers.IO)
    // Categorías de riesgo para reducir falsos positivos
    private val criticalKeywords = listOf("clavis", "token", "contraseña", "password", "cvv")
    private val urgentKeywords = listOf("urgente", "bloqueada", "suspendida", "vence hoy")
    private val prizeKeywords = listOf("premio", "ganaste", "sorteo", "felicidades")

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION) {
            val messages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            for (sms in messages) {
                val body = sms.messageBody.lowercase()
                val sender = sms.originatingAddress ?: "Desconocido"
                
                analyzeMessage(context, sender, body)
            }
        }
    }

    private fun analyzeMessage(context: Context, sender: String, body: String) {
        var riskScore = 0
        
        // 1. Análisis de enlaces (Alto riesgo)
        if (body.contains("http") || body.contains("bit.ly") || body.contains(".tk")) {
            riskScore += 40
        }

        // 2. Análisis de palabras críticas (Seguridad bancaria)
        if (criticalKeywords.any { body.contains(it) }) {
            riskScore += 30
        }

        // 3. Análisis de urgencia/miedo
        if (urgentKeywords.any { body.contains(it) }) {
            riskScore += 20
        }

        // 4. Análisis de premios
        if (prizeKeywords.any { body.contains(it) }) {
            riskScore += 20
        }

        // Determinar acción basada en el puntaje de riesgo
        when {
            riskScore >= 60 -> {
                showSecurityAlert(context, "⚠️ ALERTA CRÍTICA", "Posible estafa de $sender detectada.")
                reportAutomatically(context, sender, "SMS Fraudulento Detectado")
            }
            riskScore >= 30 -> {
                showSecurityAlert(context, "🔍 Mensaje Sospechoso", "Ten cuidado con el mensaje de $sender.")
            }
        }
    }

    private fun reportAutomatically(context: Context, sender: String, reason: String) {
        val repository = SecurityRepository.getInstance(context.applicationContext)
        receiverScope.launch {
            repository.reportNumber(sender, reason, AlertCategory.SCAM)
        }
    }

    private fun showSecurityAlert(context: Context, title: String, message: String) {
        Toast.makeText(context, "$title: $message", Toast.LENGTH_LONG).show()
        // Aquí se enviaría una notificación al sistema de notificaciones de Android
    }
}
