package com.example.inventorymanager.util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventorymanager.R;

public class ToastUtils {

    public static void showSuccess(Context context, String message) {
        showToast(
                context,
                message,
                R.drawable.ic_success,
                R.drawable.bg_success
        );
    }

    public static void showError(Context context, String message) {
        showToast(
                context,
                message,
                R.drawable.ic_close,
                R.drawable.bg_error
        );
    }

    private static void showToast(
            Context context,
            String message,
            int iconRes,
            int backgroundRes
    ) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.toast_message, null);

        ImageView icon = layout.findViewById(R.id.ivIcon);
        TextView text = layout.findViewById(R.id.tvText);

        icon.setImageResource(iconRes);
        text.setText(message);

        layout.setBackgroundResource(backgroundRes);

        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }
}
