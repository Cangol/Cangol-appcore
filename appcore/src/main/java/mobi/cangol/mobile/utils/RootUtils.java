package mobi.cangol.mobile.utils;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * @author Kevin Kowalewski
 */
public class RootUtils {

    public static boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    public static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    public static boolean checkRootMethod2() {
        try {
            File file = new File("/system/app/Superuser.apk");
            return file.exists();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkRootMethod3() {
        return new ExecShell().executeCommand(SHELL_CMD.check_su_binary) != null;
    }

    public static enum SHELL_CMD {
        check_su_binary(new String[]{"/system/xbin/which", "su"});

        String[] command;

        SHELL_CMD(String[] command) {
            this.command = command;
        }
    }

    /**
     * @author Kevin Kowalewski
     */
    private static class ExecShell {

        public ArrayList<String> executeCommand(SHELL_CMD shellCmd) {
            String line = null;
            ArrayList<String> fullResponse = new ArrayList<String>();
            Process localProcess = null;
            try {
                localProcess = Runtime.getRuntime().exec(shellCmd.command);
            } catch (Exception e) {
                return null;
            }
            BufferedReader in;
            try {
                in = new BufferedReader(new InputStreamReader(
                        localProcess.getInputStream(), "UTF-8"));
                while ((line = in.readLine()) != null) {
                    Log.d(getClass().getName(), "--> Line received: " + line);
                    fullResponse.add(line);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            Log.d(getClass().getName(), "--> Full response was: " + fullResponse);
            return fullResponse;
        }
    }
}