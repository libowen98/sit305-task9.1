package com.example.positioningdemo;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


/**
 * toast ut
 */
public class ToUtils {

    /**
     * 弹框
     */
    public static void toast(Context context,String msg) {
        View view = LayoutInflater.from(context).inflate(R.layout.to_utils, null);
        TextView tvContent = view.findViewById(R.id.tvContent);
        tvContent.setText(msg);
        Toast toast = new Toast(context);
        toast.setView(view);
        toast.setGravity(Gravity.CENTER, 0 , 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.show();
    }

}
