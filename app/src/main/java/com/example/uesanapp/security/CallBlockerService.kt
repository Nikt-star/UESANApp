package com.example.uesanapp.security

import android.telecom.Call
import android.telecom.CallScreeningService
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CallBlockerService : CallScreeningService() {
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    override fun onScreenCall(callDetails: Call.Details) {
        val phoneNumber = callDetails.handle.schemeSpecificPart
        Log.d("CallBlocker", "Llamada entrante de: $phoneNumber")

        val repository = SecurityRepository.getInstance(applicationContext)

        serviceScope.launch {
            if (repository.isNumberBlocked(phoneNumber)) {
                Log.d("CallBlocker", "Bloqueando llamada de spam detectado: $phoneNumber")
                respondToCall(callDetails, CallResponse.Builder()
                    .setDisallowCall(true)
                    .setRejectCall(true)
                    .setSkipCallLog(false)
                    .setSkipNotification(false)
                    .build())
            } else {
                respondToCall(callDetails, CallResponse.Builder().build())
            }
        }
    }
}
