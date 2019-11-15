/**
 * Copyright (c) 2013 Cangol
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package mobi.cangol.mobile.security

/**
 * @author Cangol
 */

import java.io.UnsupportedEncodingException
import java.security.InvalidKeyException
import java.security.NoSuchAlgorithmException
import java.security.SecureRandom
import javax.crypto.*
import javax.crypto.spec.SecretKeySpec

object AESUtils {

    @Throws(UnsupportedEncodingException::class, IllegalBlockSizeException::class, InvalidKeyException::class, BadPaddingException::class, NoSuchAlgorithmException::class, NoSuchPaddingException::class)
    @JvmStatic
    fun encrypt(seed: String, content: String): String {
        val rawKey = getRawKey(seed.toByteArray())
        val result = encrypt(rawKey, content.toByteArray())
        return toHex(result)
    }

    @Throws(UnsupportedEncodingException::class, NoSuchAlgorithmException::class, IllegalBlockSizeException::class, InvalidKeyException::class, BadPaddingException::class, NoSuchPaddingException::class)
    @JvmStatic
    fun decrypt(seed: String, encrypted: String): String {
        val rawKey = getRawKey(seed.toByteArray())
        val enc = toByte(encrypted)
        val result = decrypt(rawKey, enc)
        return String(result)
    }

    @Throws(NoSuchAlgorithmException::class)
    @JvmStatic
    fun getRawKey(seed: ByteArray): ByteArray {
        val kgen = KeyGenerator.getInstance("AES")
        val sr = SecureRandom.getInstance("SHA1PRNG")
        sr.setSeed(seed)
        kgen.init(128, sr) // 192 and 256 bits may not be available
        return kgen.generateKey().encoded
    }


    @Throws(NoSuchPaddingException::class, NoSuchAlgorithmException::class, InvalidKeyException::class, BadPaddingException::class, IllegalBlockSizeException::class)
    @JvmStatic
    fun encrypt(raw: ByteArray, clear: ByteArray): ByteArray {
        val skeySpec = SecretKeySpec(raw, "AES")
        val cipher = Cipher.getInstance("AES")   //AES/CBC/PKCS5Padding
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec)
        return cipher.doFinal(clear)
    }

    @Throws(NoSuchPaddingException::class, NoSuchAlgorithmException::class, InvalidKeyException::class, BadPaddingException::class, IllegalBlockSizeException::class)
    @JvmStatic
    fun decrypt(raw: ByteArray, encrypted: ByteArray): ByteArray {
        val skeySpec = SecretKeySpec(raw, "AES")
        val cipher = Cipher.getInstance("AES")   //AES/CBC/PKCS5Padding
        cipher.init(Cipher.DECRYPT_MODE, skeySpec)
        return cipher.doFinal(encrypted)
    }

    @JvmStatic
    fun toByte(hexString: String): ByteArray {
        val len = hexString.length / 2
        val result = ByteArray(len)
        for (i in 0 until len) {
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2), 16).toByte()
        }
        return result
    }

    @JvmStatic
    fun toHex(buf: ByteArray?): String {
        val hex = "0123456789ABCDEF"
        if (buf == null) {
            return ""
        }
        val result = StringBuilder(2 * buf.size)
        for (i in buf.indices) {
            result.append(hex[buf[i].toInt() shr 4 and 0x0f])
                    .append(hex[buf[i].toInt() and 0x0f])
        }
        return result.toString()
    }
}