package com.example.uesanapp.security

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.util.Log

class SecurityAccessibilityService : AccessibilityService() {

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED ||
            event.eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            
            val rootNode = rootInActiveWindow ?: return
            scanNode(rootNode)
        }
    }

    private fun scanNode(node: AccessibilityNodeInfo) {
        if (node.text != null) {
            val content = node.text.toString()
            if (isScam(content)) {
                Log.d("SecurityService", "Posible estafa detectada: $content")
                // Aquí se podría mostrar un overlay o notificación al usuario
            }
        }
        for (i in 0 until node.childCount) {
            val child = node.getChild(i)
            if (child != null) {
                scanNode(child)
            }
        }
    }

    private fun isScam(text: String): Boolean {
        val scamKeywords = listOf("premio", "ganaste", "urgente", "banco", "actualizar datos", "verificar cuenta")
        return scamKeywords.any { text.lowercase().contains(it) } && text.contains("http")
    }

    override fun onInterrupt() {
        Log.e("SecurityService", "Servicio interrumpido")
    }
}
