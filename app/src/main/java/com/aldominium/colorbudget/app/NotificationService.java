package com.aldominium.colorbudget.app;

import android.app.AlertDialog;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.net.URI;
import java.util.Calendar;
import java.util.List;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class NotificationService extends IntentService {
    public static final String TAG = MainActivity.class.getSimpleName();

    public static final String noPaymentsSound = "alarm_rooster";
    public static String paymentsSound = "cash";

    public int mActualDay;
    public int mActualMonth;
    public int mActualYear;

    public int mDaysBefore;

    // Notification Action Elements
    private Intent mNotificationIntent;
    private PendingIntent mContentIntent;

    private long[] mVibratePattern = { 0, 200, 200, 300 };

    // Notification Text Elements
    private final CharSequence tickerText = "Are You Playing Angry Birds Again!";
    private final CharSequence contentTitle = "A Kind Reminder";
    private final CharSequence contentText = "Get back to studying!!";

    private Uri noPaymentsSoundURI = Uri
            .parse("android.resource://com.aldominium.colorbudget.app/"
                    + R.raw.alarm_rooster);

    private Uri paymentsSoundURI;
    final ParseUser currentUser = ParseUser.getCurrentUser();

    //We must call the super constructor
    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        SharedPreferences pref = PreferenceManager
                .getDefaultSharedPreferences(this);


        mDaysBefore = Integer.parseInt(pref.getString("dias","1"));

        paymentsSound = pref.getString("tema","cash");

        if(paymentsSound.equals("cash")){
            paymentsSoundURI = Uri
                    .parse("android.resource://com.aldominium.colorbudget.app/"
                            + R.raw.cash);
        }else if (paymentsSound.equals("dong")){
            paymentsSoundURI = Uri
                    .parse("android.resource://com.aldominium.colorbudget.app/"
                            + R.raw.dong);
        }else {
            paymentsSoundURI = Uri
                    .parse("android.resource://com.aldominium.colorbudget.app/"
                            + R.raw.cash);
        }


        if (intent != null) {
            Calendar calendar = Calendar.getInstance();
            mActualDay = calendar.get(Calendar.DAY_OF_MONTH);
            mActualMonth = calendar.get(Calendar.MONTH)+1;
            mActualYear = calendar.get(Calendar.YEAR);

            ParseQuery<ParseObject> query = new ParseQuery<ParseObject>(ParseConstants.CLASS_PAYMENTS);
            query.whereEqualTo(ParseConstants.KEY_USER_ID, ParseUser.getCurrentUser().getObjectId());
            query.whereEqualTo(ParseConstants.KEY_DAY, mActualDay +mDaysBefore);
            query.whereEqualTo(ParseConstants.KEY_MONTH, mActualMonth);
            query.whereEqualTo(ParseConstants.KEY_YEAR, mActualYear);
            query.setLimit(1000);
            query.findInBackground(new FindCallback<ParseObject>() {

                @Override
                public void done(List<ParseObject> parseObjects, ParseException e)
                {
                    if (e == null){
                        //objects retrieved succesfully
                        if (parseObjects.isEmpty()){
                            //There are no payments due, yay!!
                            Log.e(TAG, "No payments");

                            mNotificationIntent = new Intent(NotificationService.this,MainActivity.class);
                            mNotificationIntent.putExtra("notificacion",true);
                            mContentIntent = PendingIntent.getActivity(NotificationService.this, 0,
                                    mNotificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);

                            Notification.Builder notificationBuilder = new Notification.Builder(
                                    NotificationService.this).setTicker("No payments, yay!!!")
                                    .setSmallIcon(android.R.drawable.stat_sys_warning)
                                    .setAutoCancel(true).setContentTitle("No payments,Relax")
                                    .setSound(noPaymentsSoundURI).setVibrate(mVibratePattern)
                                    .setContentText("No payments for tomorrow, relax!").setContentIntent(mContentIntent);

                            NotificationManager mNotificationManager = (NotificationManager) NotificationService.this
                                    .getSystemService(NotificationService.this.NOTIFICATION_SERVICE);
                            mNotificationManager.notify(1,
                                    notificationBuilder.build());
                        }else {
                            //We have payments, send notification
                            Log.e(TAG, "payments");
                            mNotificationIntent = new Intent(NotificationService.this,MainActivity.class);
                            mNotificationIntent.putExtra("notificacion",true);
                            mContentIntent = PendingIntent.getActivity(NotificationService.this, 0,
                                    mNotificationIntent, Intent.FLAG_ACTIVITY_NEW_TASK);


                            Notification.Builder notificationBuilder = new Notification.Builder(
                                    NotificationService.this).setTicker("Payments tomorrow")
                                    .setSmallIcon(android.R.drawable.stat_sys_warning)
                                    .setAutoCancel(true).setContentTitle("You have payments tomorrow")
                                    .setSound(paymentsSoundURI).setVibrate(mVibratePattern)
                                    .setContentText("Touch to see your tomorrow's payments").setContentIntent(mContentIntent);

                            NotificationManager mNotificationManager = (NotificationManager) NotificationService.this
                                    .getSystemService(NotificationService.this.NOTIFICATION_SERVICE);
                            mNotificationManager.notify(1,
                                    notificationBuilder.build());



                        }
                    }else{
                        Log.e(TAG, e.getMessage());
                        //Error in object retrieval

                    }

                }
            });
        }
    }

}
