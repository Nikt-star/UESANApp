package com.example.uesanapp.model

import androidx.compose.ui.graphics.vector.ImageVector

enum class AlertCategory {
    SCAM,
    PHISHING,
    SUSPICIOUS,
    SPAM,
    SAFE
}

data class CommunityAlert(
    val number: String,
    val reason: String,
    val time: String,
    val category: AlertCategory = AlertCategory.SUSPICIOUS
)

data class SecurityTip(
    val title: String,
    val description: String,
    val icon: ImageVector? = null
)
