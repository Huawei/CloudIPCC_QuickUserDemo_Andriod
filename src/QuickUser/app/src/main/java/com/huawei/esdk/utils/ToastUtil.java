package com.huawei.esdk.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created on 2017/11/28.
 */
public class ToastUtil {
    private static Toast toast;

    public static void showToast(Context context, String str, int length)
    {
        if (null == context)
        {
            return;
        }

        if (null == toast)
        {
            toast = Toast.makeText(context, str, length);
        }
        else
        {
            toast.setText(str);
        }
        toast.show();
    }
}
