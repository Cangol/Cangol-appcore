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
 * RSA加密解密类
 *
 * @author Cangol
 */

import java.math.BigInteger
import java.security.KeyFactory
import java.security.KeyPairGenerator
import java.security.NoSuchAlgorithmException
import java.security.interfaces.RSAPrivateKey
import java.security.interfaces.RSAPublicKey
import java.security.spec.RSAPrivateKeySpec
import java.security.spec.RSAPublicKeySpec
import java.util.*
import javax.crypto.Cipher

object RSAUtils {
    /**
     * 生成公钥和私钥
     *
     * @throws NoSuchAlgorithmException
     */
    @Throws(NoSuchAlgorithmException::class)
    @JvmStatic
    fun getKeys(): Map<String, Any> {
        val map = HashMap<String, Any>()
        val keyPairGen = KeyPairGenerator.getInstance("RSA")
        keyPairGen.initialize(1024)
        val keyPair = keyPairGen.generateKeyPair()
        val publicKey = keyPair.public as RSAPublicKey
        val privateKey = keyPair.private as RSAPrivateKey
        map["public"] = publicKey
        map["private"] = privateKey
        return map
    }

    /**
     * 使用模和指数生成RSA公钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA
     * /None/NoPadding】
     *
     * @param modulus  模
     * @param exponent 指数
     * @return
     */
    @JvmStatic
    fun getPublicKey(modulus: String, exponent: String): RSAPublicKey? {
        return try {
            val b1 = BigInteger(modulus)
            val b2 = BigInteger(exponent)
            val keyFactory = KeyFactory.getInstance("RSA")
            val keySpec = RSAPublicKeySpec(b1, b2)
            keyFactory.generatePublic(keySpec) as RSAPublicKey
        } catch (e: Exception) {
            null
        }

    }

    /**
     * 使用模和指数生成RSA私钥
     * 注意：【此代码用了默认补位方式，为RSA/None/PKCS1Padding，不同JDK默认的补位方式可能不同，如Android默认是RSA
     * /None/NoPadding】
     *
     * @param modulus  模
     * @param exponent 指数
     * @return
     */
    @JvmStatic
    fun getPrivateKey(modulus: String, exponent: String): RSAPrivateKey? {
        return try {
            val b1 = BigInteger(modulus)
            val b2 = BigInteger(exponent)
            val keyFactory = KeyFactory.getInstance("RSA")
            val keySpec = RSAPrivateKeySpec(b1, b2)
            keyFactory.generatePrivate(keySpec) as RSAPrivateKey
        } catch (e: Exception) {
            null
        }

    }

    /**
     * 公钥加密
     *
     * @param data
     * @param publicKey
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    @JvmStatic
    fun encryptByPublicKey(data: String, publicKey: RSAPublicKey): String {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.ENCRYPT_MODE, publicKey)
        // 模长
        val keyLen = publicKey.modulus.bitLength() / 8
        // 加密数据长度 <= 模长-11
        val datas = splitString(data, keyLen - 11)
        val mi = StringBuilder()
        //如果明文长度大于模长-11则要分组加密
        for (s in datas) {
            mi.append(bcd2Str(cipher.doFinal(s?.toByteArray())))
        }
        return mi.toString()
    }

    /**
     * 私钥解密
     *
     * @param data
     * @param privateKey
     * @return
     * @throws Exception
     */
    @Throws(Exception::class)
    @JvmStatic
    fun decryptByPrivateKey(data: String, privateKey: RSAPrivateKey): String {
        val cipher = Cipher.getInstance("RSA")
        cipher.init(Cipher.DECRYPT_MODE, privateKey)
        //模长
        val keyLen = privateKey.modulus.bitLength() / 8
        val bytes = data.toByteArray()
        val bcd = asciiToBcd(bytes, bytes.size)
        //如果密文长度大于模长则要分组解密
        val ming = StringBuilder()
        val arrays = splitArray(bcd, keyLen)
        for (arr in arrays) {
            ming.append(String(cipher.doFinal(arr)))
        }
        return ming.toString()
    }

    /**
     * ASCII码转BCD码
     */
    private fun asciiToBcd(ascii: ByteArray, ascLen: Int): ByteArray {
        val bcd = ByteArray(ascLen / 2)
        var j = 0
        for (i in 0 until (ascLen + 1) / 2) {
            bcd[i] = ascToBcd(ascii[j++])
            bcd[i] = ((if (j >= ascLen) 0x00 else ascToBcd(ascii[j++])) + (bcd[i].toInt() shl 4)).toByte()
        }
        return bcd
    }

    private fun ascToBcd(asc: Byte): Byte {
        return if (asc >= '0'.toByte() && asc <= '9'.toByte()) {
            (asc - '0'.toByte()).toByte()
        } else if (asc >= 'A'.toByte() && asc <= 'F'.toByte()) {
            (asc - 'A'.toByte() + 10).toByte()
        } else if (asc >= 'a'.toByte() && asc <= 'f'.toByte()) {
            (asc - 'a'.toByte() + 10).toByte()
        } else {
            (asc - 48).toByte()
        }
    }

    /**
     * BCD转字符串
     */
    private fun bcd2Str(bytes: ByteArray): String {
        val temp = CharArray(bytes.size * 2)
        var char: Char

        for (i in bytes.indices) {
            char = (bytes[i].toInt() and 0xf0 shr 4 and 0x0f).toChar()
            temp[i * 2] = (if (char.toInt() > 9) char + 'A'.toInt() - 10 else char + '0'.toInt()).toChar()

            char = (bytes[i].toInt() and 0x0f).toChar()
            temp[i * 2 + 1] = (if (char.toInt() > 9) char + 'A'.toInt() - 10 else char + '0'.toInt()).toChar()
        }
        return String(temp)
    }

    /**
     * 拆分字符串
     */
    private fun splitString(string: String, len: Int): Array<String?> {
        val x = string.length / len
        val y = string.length % len
        var z = 0
        if (y != 0) {
            z = 1
        }
        val strings = arrayOfNulls<String>(x + z)
        var str = ""
        for (i in 0 until x + z) {
            if (i == x + z - 1 && y != 0) {
                str = string.substring(i * len, i * len + y)
            } else {
                str = string.substring(i * len, i * len + len)
            }
            strings[i] = str
        }
        return strings
    }

    /**
     * 拆分数组
     */
    private fun splitArray(data: ByteArray, len: Int): Array<ByteArray?> {
        val x = data.size / len
        val y = data.size % len
        var z = 0
        if (y != 0) {
            z = 1
        }
        val arrays = arrayOfNulls<ByteArray>(x + z)
        var arr: ByteArray
        for (i in 0 until x + z) {
            arr = ByteArray(len)
            if (i == x + z - 1 && y != 0) {
                System.arraycopy(data, i * len, arr, 0, y)
            } else {
                System.arraycopy(data, i * len, arr, 0, len)
            }
            arrays[i] = arr
        }
        return arrays
    }
}