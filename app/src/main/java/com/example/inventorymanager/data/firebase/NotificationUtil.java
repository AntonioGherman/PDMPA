package com.example.inventorymanager.data.firebase;

import android.content.Context;
import android.content.pm.PackageManager;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.inventorymanager.R;

public class NotificationUtil {

    public static final String CHANNEL_ALERTS = "stock_alerts";

    public static void show(
            Context context,
            int id,
            String title,
            String text
    ) {

        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ALERTS)
                        .setSmallIcon(R.drawable.ic_alert)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true);

//        if (ActivityCompat.checkSelfPermission(
//                context,
//                Manifest.permission.
//        ) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }

        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        NotificationManagerCompat.from(context)
                .notify(id, builder.build());
    }
}
