package com.example.lu.diprint;


import android.content.pm.PackageManager;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {
    private static final String RTAG ="test" ;

    //static native void test();
    static {
        System.loadLibrary("mylibrary");
    }


    StringBuffer libpath = new StringBuffer();
    String apkpath = "null";
    String stacktrace = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnTest = (Button) findViewById(R.id.button);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String res;
                String res1 = DiPrint.detect();
                String res2 = checkUndeclaedPermission();
                String res3 = checkAPKCodeLoadingPath();
                String res4 = getStatckTrace();

                if (res1.equals("virtualization") || res2.equals("virtualization") || res3.equals("virtualization")|| res4.equals("virtualization")){
                    res = "virtualization";
                }
                else{
                    res = "real";
                }
                addTextView("Detection Result ", res);
            }
        });
    }

    public String hasReadContactsPermission() {
        String res = "";
        int perm = checkCallingOrSelfPermission("android.permission.READ_CONTACTS");

        if (perm == PackageManager.PERMISSION_GRANTED) {
            Log.i(RTAG, "hasReadContactsPermission:TRUE. in virtualization.");
            res = "virtualization";
        } else {
            Log.i(RTAG, "hasReadContactsPermission:FALSE. in real system.");
            res = "real";
        }

        return res;
    }


    public String readAllContacts() {
        String res = "";
        try {
            Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI,
                    null, null, null, null);
        } catch (Exception e) {
            Log.i(TAG, "call directly permissions：" + "- ");
            Log.i(RTAG, "readAllContacts:FALSE. in real system .");
            //e.printStackTrace();
            res = "real";
            return res;
        }
        Log.i(TAG, "call directly permissions：" + "call successfully");
        Log.i(RTAG, "readAllContacts:TRUE. in virtualization . ");
        res = "virtualization";
        return res;
    }

    public String checkUndeclaedPermission() {
        String res = "";
        String query = hasReadContactsPermission(); //query
        String execute = readAllContacts(); //execute
        if (query.equals("virtualization") || execute.equals("virtualization")){
            res = "virtualization";
        }else{
            res = "real";
        }
        return res;
    }

    public String checkAPKCodeLoadingPath() {
        String res = "";
        apkpath = getPackageCodePath();
        //filehelper.write(mPath, apkPath);
        Log.i(TAG, apkpath);

        if ((apkpath.equals("/data/app/com.example.lu.diprint-1/base.apk"))||(apkpath.equals("/data/app/com.example.lu.diprint-2/base.apk"))) {
            res = "real";
            Log.i(RTAG, "checkAPKCodeLoadingPath:" + apkpath + "  TRUE. in real system. ");
        } else {
            res = "virtualization";
            Log.i(RTAG, "checkAPKCodeLoadingPath:" + apkpath + "  FALSE. in virtualization. ");
        }
        return res;
    }

    public String getStatckTrace() {
        String res = "...";

        int count =0;
        try {
            throw new Exception("blah");
        }catch (Exception e) {

            for(StackTraceElement stackTraceElement : e.getStackTrace()) {
                String stacktracetmp = "";
                Log.i(TAG, "call stack： " + stackTraceElement.getClassName() + "->" + stackTraceElement.getMethodName());
                stacktracetmp = stackTraceElement.getClassName() + "->" + stackTraceElement.getMethodName();
                System.out.println("stack--"+stacktracetmp);
                stacktrace = stacktrace + "\n" + stacktracetmp;
                if(stacktracetmp.contains("callActivityOnCreate")){
                    count = count +1;
                }
            }
            if(count >1){
                res = "virtualization";
            }
            else{
                res = "real";
            }
            Log.i(RTAG, "GetStackTrace: " +  stacktrace);
        }
        return res;
    }

    private void addTextView(String text1, String text2 ){
        LinearLayout rl = (LinearLayout) findViewById(R.id.rl);
        LinearLayout ll = new LinearLayout(this);

        ll.setOrientation(LinearLayout.HORIZONTAL);
        TextView tv1 = new TextView(this);
        tv1.setText(text1 + " :   ");
        ll.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText(text2);
        ll.addView(tv2);
        rl.addView(ll);
    }

    private void addTextView(String text1, int text2 ){
        LinearLayout rl = (LinearLayout) findViewById(R.id.rl);

        LinearLayout ll = new LinearLayout(this);

        ll.setOrientation(LinearLayout.HORIZONTAL);
        TextView tv1 = new TextView(this);
        tv1.setText(text1 + " :   ");
        ll.addView(tv1);
        TextView tv2 = new TextView(this);
        tv2.setText(text2);
        ll.addView(tv2);
        rl.addView(ll);
    }

}