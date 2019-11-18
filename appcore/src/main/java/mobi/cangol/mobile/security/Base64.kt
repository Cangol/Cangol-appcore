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

import android.util.Log

import java.io.UnsupportedEncodingException

/**
 * base64加密解密类
 *
 * @author Cangol
 */

object Base64 {
    private const val ENCODING_CHAR = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/"

    @JvmStatic fun encode(source: String): String {
        val sourceBytes = getPaddedBytes(source)
        val numGroups = (sourceBytes.size + 2) / 3
        val targetBytes = CharArray(4)
        val target = CharArray(4 * numGroups)
        for (group in 0 until numGroups) {
            convert3To4(sourceBytes, group * 3, targetBytes)
            for (i in targetBytes.indices) {
                target[i + 4 * group] = ENCODING_CHAR[targetBytes[i].toInt()]
            }
        }
        val numPadBytes = sourceBytes.size - source.length
        for (i in target.size - numPadBytes until target.size) {
            target[i] = '='
        }
        return String(target)
    }

    private fun getPaddedBytes(source: String): CharArray {
        val converted = source.toCharArray()
        val requiredLength = 3 * ((converted.size + 2) / 3)
        val result = CharArray(requiredLength)
        System.arraycopy(converted, 0, result, 0, converted.size)
        return result
    }

    private fun convert3To4(source: CharArray, sourceIndex: Int, target: CharArray) {
        target[0] = source[sourceIndex].toInt().ushr(2).toChar()
        target[1] = (source[sourceIndex].toInt() and 0x03 shl 4 or source[sourceIndex + 1].toInt().ushr(4)).toChar()
        target[2] = (source[sourceIndex + 1].toInt() and 0x0f shl 2 or source[sourceIndex + 2].toInt().ushr(6)).toChar()
        target[3] = (source[sourceIndex + 2].toInt() and 0x3f).toChar()
    }

    @JvmStatic fun decode(source: String): String? {
        if (source.length % 4 != 0) {
            throw RuntimeException("valid Base64 codes have a multiple of 4 characters")
        }
        val numGroups = source.length / 4
        val numExtraBytes = if (source.endsWith("==")) 2 else if (source.endsWith("=")) 1 else 0
        val targetBytes = ByteArray(3 * numGroups)
        val sourceBytes = ByteArray(4)
        for (group in 0 until numGroups) {
            for (i in sourceBytes.indices) {
                sourceBytes[i] = Math.max(0, ENCODING_CHAR.indexOf(source[4 * group + i])).toByte()
            }
            convert4To3(sourceBytes, targetBytes, group * 3)
        }

        try {
            return String(targetBytes, 0, targetBytes.size - numExtraBytes)
        } catch (e: UnsupportedEncodingException) {
            Log.d("UnsupportedEncoding", e.message)
        }

        return null
    }

    private fun convert4To3(source: ByteArray, target: ByteArray, targetIndex: Int) {
        target[targetIndex] = (source[0].toInt() shl 2 or source[1].toInt().ushr(4)).toByte()
        target[targetIndex + 1] = (source[1].toInt() and 0x0f shl 4 or source[2].toInt().ushr(2)).toByte()
        target[targetIndex + 2] = (source[2].toInt() and 0x03 shl 6 or source[3].toInt()).toByte()
    }
}
