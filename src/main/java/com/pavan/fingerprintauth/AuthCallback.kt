package com.pavan.fingerprintauth

import android.hardware.fingerprint.FingerprintManager

/**
 * Created by Pavan on 16/02/18.
 */
interface AuthCallback {
    fun onAuthenticationError(errMsgId: Int, errString: CharSequence)
    fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence)
    fun onAuthenticationFailed()
    fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult)
}