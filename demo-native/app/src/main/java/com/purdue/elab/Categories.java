package com.purdue.elab;

import android.content.Context;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

public class Categories {

    private static Categories INSTANCE;
    private Map<Integer, String> map = new HashMap<>();
    private int size = 0;

    /**
     *
     * @param context
     * @return
     */
    public static Categories get(Context context) {
        if(INSTANCE == null) {
            INSTANCE = new Categories(context.getApplicationContext());
        }
        return INSTANCE;
    }

    private Categories(Context context){
        int count = 0;
        BufferedReader reader = null;
        try {
            //reader = new BufferedReader(new InputStreamReader(context.getAssets().open("Networks/generic/categories.txt")));
            reader = new BufferedReader(new InputStreamReader(new FileInputStream("/sdcard/demo-native/categories.txt")));
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //System.err.println(mLine);
                String[] parts = mLine.split(",");
                String part1 = parts[0];
                map.put(count++,part1);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        size = count;
    }

    public String getCategory(int index){
        return map.get(index);
    }

    public int getSize(){
        return size;
    }

}
