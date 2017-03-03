package com.android.sensortest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import android.util.Log;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;
import java.io.FileWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;

/**
 * 执行shell脚本工具类
 */
public class CommandExecution {
    public static final String TAG = "CommandExecution";

    public final static String COMMAND_SU       = "su";
    public final static String COMMAND_SH       = "sh";
    public final static String COMMAND_EXIT     = "exit\n";
    public final static String COMMAND_LINE_END = "\n";
    public final static String[] COMMAND_SH_1 = {
        "brightness_path=/sys/class/backlight/pwm-backlight.0/brightness", "max=255", "int=1", "while(( $int<=$max ))","do", "sleep 0.1", "echo $int", "echo $int > $brightness_path", "let \"int++\"", "done"
        };

    public final static String[] COMMAND_SH_M80 = {
        "brightness_path=/sys/class/leds/lcd-backlight/brightness", "max=255", "int=1", "while(( $int<=$max ))","do", "sleep 0.1", "echo $int", "echo $int > $brightness_path", "let \"int++\"", "done"
        };

    public final static String[] COMMAND_SH_M80_DECREASE = {
        "brightness_path=/sys/class/leds/lcd-backlight/brightness", "max=255", "int=1", "while(( $int<=$max ))","do", "sleep 0.1", "echo $max", "echo $max > $brightness_path", "let \"max--\"", "done"
        };

    //public final static String LOG_FILE_DIR = "/Android/data/com.android.sensortest";
    //public final static String WAKEUP_LOG_FILE_NAME = "wakeuplog.log";


    /**
     * Command执行结果
     *
     */
    public static class CommandResult {
        public int result = -1;
        public String errorMsg;
        public String successMsg;
        public List<String> successMsgList;
    }

    /**
     * 执行命令—单条
     * @param command
     * @param isRoot
     * @return
     */
    public CommandResult execCommand(String command, boolean isRoot) {
        String[] commands = {command};
        return execCommand(commands, isRoot);
    }

    /**
     * 执行命令-多条
     * @param commands
     * @param isRoot
     * @return
     */
    public CommandResult execCommand(String[] commands, boolean isRoot) {
        CommandResult commandResult = new CommandResult();
        commandResult.successMsgList = new ArrayList<String>();
        if (commands == null || commands.length == 0) {
            return commandResult;
        }
        for (int i = 0; i < commands.length; i++) {
            Process process = null;
            DataOutputStream os = null;
            BufferedReader successResult = null;
            BufferedReader errorResult = null;
            StringBuilder successMsg = null;
            StringBuilder errorMsg = null;
            try {
                process = Runtime.getRuntime().exec(commands[i]);
                commandResult.result = process.waitFor();
                //获取错误信息
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String successString;
                String errorString;
                while ((successString = successResult.readLine()) != null) {
                    successMsg.append(successString);
                    successMsg.append(",");
                    commandResult.successMsgList.add(successString);
                };
                while ((errorString = errorResult.readLine()) != null) {
                    errorMsg.append(errorString);
                }
                commandResult.successMsg = successMsg.toString();
                commandResult.errorMsg = errorMsg.toString();
                Log.d(TAG, commandResult.result + " | " + commandResult.successMsg
                        + " | " + commandResult.errorMsg);
            } catch (IOException e) {
                String errmsg = e.getMessage();
                if (errmsg != null) {
                    Log.e(TAG, errmsg);
                }
                    e.printStackTrace();

            } catch (Exception e) {
                String errmsg = e.getMessage();
                if (errmsg != null) {
                    Log.e(TAG, errmsg);
                }
                    e.printStackTrace();

            } finally {
                try {
                    if (os != null) os.close();
                    if (successResult != null) successResult.close();
                    if (errorResult != null) errorResult.close();
                } catch (IOException e) {
                    String errmsg = e.getMessage();
                    if (errmsg != null) {
                        Log.e(TAG, errmsg);
                    }
                    e.printStackTrace();

                }
                if (process != null) process.destroy();
            }
        }
        return commandResult;
    }

    /**
     * 执行命令-多条
     * @param commands
     * @param isRoot
     * @return
     */
    public CommandResult execCommandNoWait(String[] commands, boolean isRoot) {
        CommandResult commandResult = new CommandResult();
        commandResult.successMsgList = new ArrayList<String>();
        if (commands == null || commands.length == 0) {
            return commandResult;
        }
        for (int i = 0; i < commands.length; i++) {
            Process process = null;
            DataOutputStream os = null;
            BufferedReader successResult = null;
            BufferedReader errorResult = null;
            StringBuilder successMsg = null;
            StringBuilder errorMsg = null;
            try {
                process = Runtime.getRuntime().exec(commands[i]);

                commandResult.result = process.waitFor();
                //获取错误信息
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String successString;
                String errorString;
                while ((successString = successResult.readLine()) != null) {
                    successMsg.append(successString);
                    successMsg.append(",");
                    commandResult.successMsgList.add(successString);
                };
                while ((errorString = errorResult.readLine()) != null) {
                    errorMsg.append(errorString);
                }
                commandResult.successMsg = successMsg.toString();
                commandResult.errorMsg = errorMsg.toString();
                Log.d(TAG, commandResult.result + " | " + commandResult.successMsg
                        + " | " + commandResult.errorMsg);

                Log.d(TAG, "execCommandNoWait sucess");
            } catch (IOException e) {
                String errmsg = e.getMessage();
                if (errmsg != null) {
                    Log.e(TAG, errmsg);
                }
                    e.printStackTrace();

            } catch (Exception e) {
                String errmsg = e.getMessage();
                if (errmsg != null) {
                    Log.e(TAG, errmsg);
                }
                    e.printStackTrace();

            } finally {
                try {
                    if (os != null) os.close();
                    if (successResult != null) successResult.close();
                    if (errorResult != null) errorResult.close();
                } catch (IOException e) {
                    String errmsg = e.getMessage();
                    if (errmsg != null) {
                        Log.e(TAG, errmsg);
                    }
                    e.printStackTrace();

                }
                if (process != null) process.destroy();
            }
        }
        return commandResult;
    }


    public boolean execCommandResultToFile(String[] commands, boolean isRoot, String fileName, int countDown) {
        boolean result = false;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(fileName));
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        if (commands == null || commands.length == 0 || fileName == null || bw == null) {
            return (result = false);
        }
        for (int i = 0; i < commands.length; i++) {
            Process process = null;
            BufferedReader successResult = null;
            BufferedReader errorResult = null;
            StringBuilder successMsg = null;
            StringBuilder errorMsg = null;
            try {
                process = Runtime.getRuntime().exec(commands[i]);

                //获取错误信息
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String successString = null;
                String errorString;
                while ((successString = successResult.readLine()) != null && countDown > 0) {
                    bw.write(successString);
                    bw.write("\r\n");
                    if (successString.indexOf("Waking up from sleep") != -1){
                        countDown--;
                        Log.d(TAG, "catch Waking up from Log : countDown:"+countDown);
                    }
                }
                while ((errorString = errorResult.readLine()) != null && countDown > 0) {
                    errorMsg.append(errorString);
                }
                Log.d(TAG, "errorMsg:"+errorMsg);
                bw.flush();
                result = true;
            } catch (IOException e) {
                String errmsg = e.getMessage();
                if (errmsg != null) {
                    Log.e(TAG, errmsg);
                }
                e.printStackTrace();
                result = false;
            } catch (Exception e) {
                String errmsg = e.getMessage();
                if (errmsg != null) {
                    Log.e(TAG, errmsg);
                }
                e.printStackTrace();
                result = false;
            } finally {
                try {
                    if (successResult != null) successResult.close();
                    if (errorResult != null) errorResult.close();
                    if (bw != null) bw.close();
                    Log.d(TAG, "execCommandResultToFile success");
                } catch (IOException e) {
                    String errmsg = e.getMessage();
                    if (errmsg != null) {
                        Log.e(TAG, errmsg);
                    }
                    e.printStackTrace();
                }
                if (process != null) process.destroy();
            }
        }
        return result;
    }

    public boolean execCommandResultToFile(String[] commands, boolean isRoot, File file, int countDown, String matchString) {
        boolean result = false;
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file));
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

        if (commands == null || commands.length == 0 || file == null || bw == null || matchString == null) {
            return (result = false);
        }
        for (int i = 0; i < commands.length; i++) {
            Process process = null;
            BufferedReader successResult = null;
            BufferedReader errorResult = null;
            StringBuilder successMsg = null;
            StringBuilder errorMsg = null;
            try {
                process = Runtime.getRuntime().exec(commands[i]);

                //获取错误信息
                successMsg = new StringBuilder();
                errorMsg = new StringBuilder();
                successResult = new BufferedReader(new InputStreamReader(process.getInputStream()));
                errorResult = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                String successString = null;
                String errorString;
                while (countDown > 0 && file.exists() && ((successString = successResult.readLine()) != null)) {
                    bw.write(successString);
                    bw.write("\r\n");
                    /*
                    if (successString.indexOf("PowerManagerService:")!= -1){
                        Log.d(TAG, "WXY##:"+ Arrays.toString(successString.split("PowerManagerService:")));
                        Log.d(TAG, "WXY##:"+ Arrays.toString((successString.split("PowerManagerService:"))[1].split(" ")));
                    }
                    if (successString.indexOf("LocalDisplayAdapter:")!= -1){
                        Log.d(TAG, "WXY##:"+ Arrays.toString(successString.split("LocalDisplayAdapter:")));
                    }
                    if (successString.indexOf("DisplayPowerController:")!= -1){
                        Log.d(TAG, "WXY##:"+ Arrays.toString(successString.split("DisplayPowerController:")));
                    }
                    if (successString.indexOf("SurfaceControl:")!= -1){
                        Log.d(TAG, "WXY##:"+ Arrays.toString(successString.split("SurfaceControl:")));
                    }
                    if (successString.indexOf("hwcomposer:")!= -1){
                        Log.d(TAG, "WXY##:"+ Arrays.toString(successString.split("hwcomposer:")));
                        Log.d(TAG, "WXY##:"+ Arrays.toString((successString.split("hwcomposer:"))[1].split(" ")));
                    }
                    */
                    if (successString.indexOf(matchString)!= -1){
                        countDown--;
                        Log.d(TAG, "catch Waking up from Log : countDown:"+countDown);
                    }
                }

                while (countDown > 0 && file.exists() && ((errorString = errorResult.readLine()) != null)) {
                    errorMsg.append(errorString);
                }
                Log.d(TAG, "errorMsg:"+errorMsg);
                bw.flush();
                result = true;
            } catch (IOException e) {
                String errmsg = e.getMessage();
                if (errmsg != null) {
                    Log.e(TAG, errmsg);
                }
                e.printStackTrace();
                result = false;
            } catch (Exception e) {
                String errmsg = e.getMessage();
                if (errmsg != null) {
                    Log.e(TAG, errmsg);
                }
                e.printStackTrace();
                result = false;
            } finally {
                try {
                    if (successResult != null) successResult.close();
                    if (errorResult != null) errorResult.close();
                    if (bw != null) bw.close();
                    Log.d(TAG, "execCommandResultToFile success");
                } catch (IOException e) {
                    String errmsg = e.getMessage();
                    if (errmsg != null) {
                        Log.e(TAG, errmsg);
                    }
                    e.printStackTrace();
                }
                if (process != null) process.destroy();
            }
        }
        return result;
    }
}
