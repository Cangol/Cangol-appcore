package mobi.cangol.mobile.utils;


import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import mobi.cangol.mobile.logging.Log;

/**
 * @author Kevin Kowalewski
 */
public class RootUtils {
    private RootUtils() {
    }

    public static boolean isDeviceRooted() {
        return checkRootMethod1() || checkRootMethod2() || checkRootMethod3();
    }

    public static boolean checkRootMethod1() {
        String buildTags = android.os.Build.TAGS;
        return buildTags != null && buildTags.contains("test-keys");
    }

    public static boolean checkRootMethod2() {
        try {
            return new File("/system/app/Superuser.apk").exists();
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean checkRootMethod3() {
        return new ExecShell().executeCommand(SHELL_CMD.CHECK_SU_BINARY) != null;
    }

    public enum SHELL_CMD {
        CHECK_SU_BINARY(new String[]{"/system/xbin/which", "su"});

        String[] command;

        SHELL_CMD(String[] command) {
            this.command = command;
        }
    }

    /**
     * @author Kevin Kowalewski
     */
    private static class ExecShell {

        public List<String> executeCommand(SHELL_CMD shellCmd) {
            String line = null;
            List<String> fullResponse = new ArrayList<>();
            Process localProcess = null;
            try {
                localProcess = Runtime.getRuntime().exec(shellCmd.command);
            } catch (Exception e) {
                return new ArrayList<>();
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
                Log.d(getClass().getName(), e.getMessage());
            }
            Log.d(getClass().getName(), "--> Full response was: " + fullResponse);
            return fullResponse;
        }
    }
}