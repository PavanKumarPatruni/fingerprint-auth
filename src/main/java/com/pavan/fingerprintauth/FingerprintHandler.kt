package com.pavan.fingerprintauth

import android.Manifest
import android.annotation.TargetApi
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.support.v4.app.ActivityCompat

/**
 * Created by Pavan on 19/02/18.
 */
class FingerprintHandler(mContext: Context) : FingerprintManager.AuthenticationCallback() {

    private var context: Context = mContext
    private var authCallback: AuthCallback = mContext as AuthCallback

    @TargetApi(Build.VERSION_CODES.M)
    fun startAuth(manager: FingerprintManager, cryptoObject: FingerprintManager.CryptoObject) {
        val cancellationSignal = CancellationSignal()
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        manager.authenticate(cryptoObject, cancellationSignal, 0, this, null)
    }


    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence) {
        authCallback.onAuthenticationError(errMsgId, errString)
    }


    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) {
        authCallback.onAuthenticationError(helpMsgId, helpString)
    }


    override fun onAuthenticationFailed() {
        authCallback.onAuthenticationFailed()
    }


    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
        authCallback.onAuthenticationSucceeded(result)
    }

}