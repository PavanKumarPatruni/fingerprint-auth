package com.pavan.fingerprintauth

import android.Manifest
import android.annotation.TargetApi
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.Bundle
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.support.v4.app.ActivityCompat
import android.support.v7.app.AppCompatActivity
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

class FingerprintAuthenticationActivity : AppCompatActivity(), AuthCallback {

    private var resultantString: String? = null
    private var resultantBoolean: Boolean = false

    private val REQUEST_CODE_FINGERPRINT: Int = 1
    private val KEY_NAME: String = "FingerPrint-Auth"
    private var cipher: Cipher? = null
    private var keyStore: KeyStore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fingerprint_authentication)

        initSensor()
    }

    private fun initSensor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val keyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
            val fingerprintManager = getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.USE_FINGERPRINT), REQUEST_CODE_FINGERPRINT)
            } else {
                if (fingerprintManager.isHardwareDetected) {
                    if (fingerprintManager.hasEnrolledFingerprints()) {
                        if (keyguardManager.isKeyguardSecure) {
                            generateKey()
                            if (cipherInit()) {
                                val cryptoObject = FingerprintManager.CryptoObject(cipher)
                                val helper = FingerprintHandler(this)
                                helper.startAuth(fingerprintManager, cryptoObject)
                            }
                        } else {
                            resultantString = "Lock screen security not enabled in Settings"
                            resultantBoolean = false

                            onFinish()
                        }
                    } else {
                        resultantString = "Your Device does not have a Fingerprint Sensor"
                        resultantBoolean = false

                        onFinish()
                    }
                } else {
                    resultantString = "Register at least one fingerprint in Settings"
                    resultantBoolean = false

                    onFinish()
                }
            }
        } else {
            resultantString = "Unsupported Version"
            resultantBoolean = false

            onFinish()
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun generateKey() {
        try {
            keyStore = KeyStore.getInstance("AndroidKeyStore")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        var keyGenerator: KeyGenerator? = null
        try {
            keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is NoSuchProviderException -> {
                    throw RuntimeException("Failed to get KeyGenerator instance", e)
                }
            }
        }

        try {
            keyStore?.load(null)
            keyGenerator?.init(KeyGenParameterSpec.Builder(KEY_NAME, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                    .setUserAuthenticationRequired(true)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
                    .build())
            keyGenerator.generateKey()
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is InvalidAlgorithmParameterException,
                is CertificateException,
                is IOException -> {
                    throw RuntimeException(e)
                }
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private fun cipherInit(): Boolean {
        try {
            cipher = Cipher.getInstance(KeyProperties.KEY_ALGORITHM_AES + "/" + KeyProperties.BLOCK_MODE_CBC + "/" + KeyProperties.ENCRYPTION_PADDING_PKCS7)
        } catch (e: Exception) {
            when (e) {
                is NoSuchAlgorithmException,
                is NoSuchPaddingException -> {
                    throw RuntimeException("Failed to get Cipher", e)
                }
            }
        }

        try {
            keyStore?.load(null)

            val key = keyStore?.getKey(KEY_NAME, null) as SecretKey
            cipher?.init(Cipher.ENCRYPT_MODE, key)
            return true
        } catch (e: KeyPermanentlyInvalidatedException) {
            return false
        } catch (e: Exception) {
            when (e) {
                is KeyStoreException,
                is CertificateException,
                is UnrecoverableKeyException,
                is IOException,
                is NoSuchAlgorithmException,
                is InvalidKeyException -> {
                    throw RuntimeException("Failed to init Cipher", e)
                }
            }
            return false
        }
    }

    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence) {
        resultantString = "Fingerprint Authentication Error\\n$errString"
        resultantBoolean = false

        onFinish();
    }

    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) {
        resultantString = "Fingerprint Authentication Error\\n$helpString"
        resultantBoolean = false

        onFinish()
    }

    override fun onAuthenticationFailed() {
        resultantString = "Fingerprint Authentication Error"
        resultantBoolean = false

        onFinish()
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult) {
        resultantString = "Fingerprint Authentication Success"
        resultantBoolean = true

        onFinish()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_CODE_FINGERPRINT -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    initSensor()
                } else {
                    resultantString = "No permission access fingerprint sensor"
                    resultantBoolean = false

                    onFinish()
                }
                return
            }
            else -> {
                // Ignore all other requests.
            }
        }
    }

    fun onFinish() {
        val resultIntent = Intent()
        resultIntent.putExtra(Constants.AUTH_MESSAGE, resultantString)
        resultIntent.putExtra(Constants.AUTH_STATUS, resultantBoolean)

        setResult(Activity.RESULT_OK, resultIntent)

        finish()
    }

    override fun onBackPressed() {
        onFinish()

        super.onBackPressed()
    }
}
