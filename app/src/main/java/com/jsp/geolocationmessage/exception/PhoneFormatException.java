package com.jsp.geolocationmessage.exception;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Tejal Shah.
 */
public class PhoneFormatException extends Exception {

    int err;
    Context appContext;

    public PhoneFormatException(){

    }

    public PhoneFormatException(int errCode, Context context, String number){
        this.err=errCode;
        this.appContext=context;
        logError(context, number);
    }


    public void logError(Context c, String number) {
        File folder = c.getCacheDir();
        File f = new File(folder, "ErrorLog.txt");
        try {
            if(!f.exists() || f.isDirectory()){
                f.createNewFile();
            }
            FileWriter fw = new FileWriter(f.getAbsoluteFile(), true);
            BufferedWriter buff = new BufferedWriter(fw);
            buff.write(new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new Date()));
            buff.newLine();
            buff.write("Phone number format exception occured for: " + number);
            buff.newLine();
            buff.close();

            //Check ErrorLog Content
            File fol = c.getCacheDir();
            File fi = new File(fol, "ErrorLog.txt");
            FileInputStream fis = new FileInputStream(fi);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader bufferedReader = new BufferedReader(isr);;
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                Log.d("#PrintErrorLog", line);
            }
            buff.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

