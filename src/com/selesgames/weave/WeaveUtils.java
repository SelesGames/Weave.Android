package com.selesgames.weave;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;

public class WeaveUtils {

    public static String readRawFile(Context context, int resId) {
        InputStream is = context.getResources().openRawResource(resId);
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();
        try {
            String line = null;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            // Log error
        } finally {
            try {
                is.close();
                br.close();
            } catch (IOException e) {
                // no-op
            }
        }

        return sb.toString();
    }

}
