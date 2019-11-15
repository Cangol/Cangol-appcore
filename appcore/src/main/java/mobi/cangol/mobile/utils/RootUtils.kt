package mobi.cangol.mobile.utils


import android.os.Build
import android.support.annotation.RequiresApi

import java.io.BufferedReader
import java.io.File
import java.io.InputStreamReader
import java.nio.charset.StandardCharsets
import java.util.ArrayList

import mobi.cangol.mobile.logging.Log

/**
 * @author Kevin Kowalewski
 */
object RootUtils {

   private const val SYSTEM_APP_SUPERUSER_APK = "/system/app/Superuser.apk"

    @JvmStatic fun isDeviceRooted(): Boolean {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3()
    }
    @JvmStatic fun checkRootMethod1(): Boolean {
        val buildTags = Build.TAGS
        return buildTags != null && buildTags.contains("test-keys")
    }

    @JvmStatic fun checkRootMethod2(): Boolean {
        return try {
            File(SYSTEM_APP_SUPERUSER_APK).exists()
        } catch (e: Exception) {
            false
        }

    }

    @JvmStatic fun checkRootMethod3(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            ExecShell().executeCommand(SHELL_CMD.CHECK_SU_BINARY) != null
        } else {
            return false
        }
    }

    enum class SHELL_CMD constructor(internal var command: Array<String>) {
        CHECK_SU_BINARY(arrayOf<String>("/system/xbin/which", "su"))
    }

    /**
     * @author Kevin Kowalewski
     */
    internal class ExecShell {

        @RequiresApi(api = Build.VERSION_CODES.KITKAT)
        fun executeCommand(shellCmd: SHELL_CMD): List<String> {
            val fullResponse = ArrayList<String>()
            var localProcess: Process? = null
            try {
                localProcess = Runtime.getRuntime().exec(shellCmd.command)
            } catch (e: Exception) {
                return ArrayList()
            }

            val reader: BufferedReader
            try {
                reader = BufferedReader(InputStreamReader(localProcess!!.inputStream, StandardCharsets.UTF_8))
                var has: Boolean? = true
                while (has!!) {
                    val line = reader.readLine()
                    has = if(line!=null){
                        fullResponse.add(line)
                        true
                    }else{
                        false
                    }
                }
            } catch (e: Exception) {
                Log.d(javaClass.name, e.message)
            }

            Log.d(javaClass.name, "--> Full response was: $fullResponse")
            return fullResponse
        }
    }
}