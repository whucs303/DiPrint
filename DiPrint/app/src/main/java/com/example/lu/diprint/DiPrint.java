package com.example.lu.diprint;


import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created by lu on 2019/3/30.
 */

public class DiPrint {
    private static final String RTAG ="test" ;

    static StringBuffer libpath = new StringBuffer();
    static String hostapkpath = "null";
    static String suspiciousproc = "";

    public static String detect(){
        String res0;
        String res1 = checkSuspiciousLib("/proc/self/maps");
        String res2 = checkHostAPK("/proc/self/maps");
        String res3 = runShell("ps");

        if (res1.equals("virtualization")||res2.equals("virtualization")||res3.equals("virtualization")){
            res0 = "virtualization";
        }else{
            res0 = "real";
        }

        return res0;
    }


    public static String checkSuspiciousLib(String filePath) {
        String res = "";
        try {
            String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt;
                int flag = 0;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if (lineTxt.contains(".so") && !lineTxt.contains("/system/lib/") && !lineTxt.contains("libmylibrary") && !lineTxt.contains("/system/vendor/lib/")) {
                        libpath.append(lineTxt);
                        System.out.println("hostAPK--"+lineTxt);
                        flag = 1;
                    }
                }
                if (flag == 0) {
                    res = "real";
                    Log.i(RTAG, "suspicious lib:TRUE in real");
                } else {
                    res = "virtualization";
                    Log.i(RTAG, "suspicious lib:FALSE in virtualization");
                }
                read.close();
            } else {
                System.out.println("no such file.");
            }
        } catch (Exception e) {
            System.out.println("read file error.");
            e.printStackTrace();
        }
        return res;
    }


    public static String checkHostAPK(String filePath) {
        String res = "";
        try {
            String encoding = "GBK";
            File file = new File(filePath);
            if (file.isFile() && file.exists()) {
                InputStreamReader read = new InputStreamReader(
                        new FileInputStream(file), encoding);
                BufferedReader bufferedReader = new BufferedReader(read);
                String lineTxt;
                String no = "no";
                int flag = 0;
                while ((lineTxt = bufferedReader.readLine()) != null) {
                    if ((lineTxt.contains("base.apk") && !lineTxt.contains("com.example.lu.diprint") )) {
                        hostapkpath = lineTxt;
                        flag = 1;
                        System.out.println("hostAPK--"+hostapkpath);
                    }
                }
                if (flag == 0) {
                    res = "real";
                    Log.i(RTAG, "checkHostAPK :TRUE in real");
                } else {
                    res = "virtualization";
                    Log.i(RTAG, "checkHostAPK :FALSE in virtualization");
                }
                read.close();
            } else {
                System.out.println("no such file.");
            }
        } catch (Exception e) {
            System.out.println("read file error.");
            e.printStackTrace();
        }
        return res;
    }


    public static String runShell(String cmd) {
        String res = "real";
        int count = 0;
        Runtime mRuntime = Runtime.getRuntime();
        try {
            Process mProcess = mRuntime.exec(cmd);
            mProcess.getOutputStream().close();
            InputStream stdin = mProcess.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(stdin));
            String currentLine = "";
            String findline = "";
            String doubleproc = "";
            String uid = "null";
            while ((currentLine = br.readLine()) != null) {
                System.out.println("process--"+currentLine);
                if (currentLine.contains("com.example.lu.diprint")) {
                    uid = currentLine.split("   ")[0];
                    break;
                }
            }

            Process mProcess1 = mRuntime.exec(cmd);
            mProcess.getOutputStream().close();
            InputStream stdin1 = mProcess1.getInputStream();
            BufferedReader br1 = new BufferedReader(new InputStreamReader(stdin1));

            while ((findline = br1.readLine()) != null) {
                System.out.println("process2--"+findline);
                System.out.println("uid--"+uid);
                if (findline.contains(uid) && !findline.contains("com.example.lu.diprint")&& !findline.contains("R ps")) {
                    res = "virtualization";
                    suspiciousproc = suspiciousproc + "\n" + findline;
                }
            }

//            Process mProcess2 = mRuntime.exec(cmd);
//            mProcess.getOutputStream().close();
//            InputStream stdin2 = mProcess2.getInputStream();
//            BufferedReader br2 = new BufferedReader(new InputStreamReader(stdin2));
//
//            while ((doubleproc = br2.readLine()) != null) {
//                if (doubleproc.contains("com.example.lu.diprint")) {
//                    count = count + 1;
//                    System.out.println("process3--"+doubleproc);
//                    suspiciousproc = suspiciousproc + "\n" + doubleproc;
//                }
//            }
//
//            if (count >1){
//                res = "virtualization";
//            }

            br.close();

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return res;
    }
}
