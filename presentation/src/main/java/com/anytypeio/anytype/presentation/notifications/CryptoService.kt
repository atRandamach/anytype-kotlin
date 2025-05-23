package com.anytypeio.anytype.presentation.notifications

import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec

interface CryptoService {
    /**
     * Decrypts AES-GCM encrypted data.
     * @param data Combined format: nonce (12 bytes) + ciphertext + tag
     * @param keyData AES key bytes
     * @return decrypted plaintext bytes
     * @throws CryptoError.DecryptionFailed if decryption fails
     */
    @Throws(CryptoError.DecryptionFailed::class)
    fun decryptAESGCM(data: ByteArray, keyData: ByteArray): ByteArray
}

class CryptoServiceImpl : CryptoService {
    companion object {
        private const val NONCE_SIZE = 12
    }

    @Throws(CryptoError.DecryptionFailed::class)
    override fun decryptAESGCM(data: ByteArray, keyData: ByteArray): ByteArray {
        return try {
            // Verify input data length
            if (data.size < NONCE_SIZE) {
                throw CryptoError.DecryptionFailed(IllegalArgumentException("Input data must be at least $NONCE_SIZE bytes"))
            }

            // Create SecretKeySpec for AES
            val keySpec = SecretKeySpec(keyData, "AES")

            // Combined data format: nonce + ciphertext + tag
            val nonce = data.copyOfRange(0, NONCE_SIZE)
            val cipherText = data.copyOfRange(NONCE_SIZE, data.size)

            // Initialize Cipher for AES/GCM/NoPadding
            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val gcmSpec = GCMParameterSpec(128, nonce)
            cipher.init(Cipher.DECRYPT_MODE, keySpec, gcmSpec)

            // Perform decryption and return plaintext
            cipher.doFinal(cipherText)
        } catch (e: Exception) {
            throw CryptoError.DecryptionFailed(e)
        }
    }
}

/**
 * Errors for CryptoService
 */
sealed class CryptoError(message: String, cause: Throwable? = null) : Exception(message, cause) {
    class DecryptionFailed(cause: Throwable? = null) : CryptoError("Decryption failed", cause)
}