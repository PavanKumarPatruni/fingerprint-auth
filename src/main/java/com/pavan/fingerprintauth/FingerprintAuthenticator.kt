package com.pavan.fingerprintauth

import android.content.Context
import android.content.Intent
import android.support.v4.content.ContextCompat

/**
 * Created by Pavan on 19/02/18.
 */
class FingerprintAuthenticator {

    companion object Factory {
        fun init(context: Context) {
            val intent = Intent(context, FingerprintAuthenticationActivity::class.java)
            ContextCompat.startActivity(context, intent, null)
        }
    }

}