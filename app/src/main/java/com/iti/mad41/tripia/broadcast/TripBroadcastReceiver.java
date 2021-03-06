package com.iti.mad41.tripia.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import com.iti.mad41.tripia.helper.Constants;
import com.iti.mad41.tripia.services.AlarmTripService;
import com.iti.mad41.tripia.services.FloatingAppIconService;
import com.iti.mad41.tripia.ui.activity.tripservice.TripAlarmActivity;

public class TripBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("myTrip", Constants.TRIP_TITLE_KEY + " ::::::onReceive ALARM: " + intent.getStringExtra(Constants.TRIP_TITLE_KEY));

        if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            startRescheduleTripAlarmsService(context);
        } else {
            Log.i("myTrip", "createTrip: onRecciver ");
            startFloatAppIconService(context, intent);
            startTripAlarmService(context, intent);

        }
    }

    private void startRescheduleTripAlarmsService(Context context) {

    }

    private void startFloatAppIconService(Context context, Intent intent) {
        Log.i("myTrip", "startFloatAppIconService:  From Broadcast ++++++ ");

        Intent startFloatIconService = new Intent(context, FloatingAppIconService.class);
        startFloatIconService.putExtra(Constants.TRIP_ID_KEY, intent.getStringExtra(Constants.TRIP_ID_KEY));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(startFloatIconService);
        } else {
            Log.i("myTrip", "startFloatAppIconService:  From Broadcast ++++++ startService");

            context.startService(startFloatIconService);
            Log.i("myTrip", "startFloatAppIconService:  From Broadcast ++++++ startService");

        }
    }

    private void startTripAlarmService(Context context, Intent intent) {
        Intent intentService = new Intent(context, AlarmTripService.class);
        setDataOnIntent(intentService, intent);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(intentService);
        } else {
            context.startService(intentService);
        }

        Intent activityIntent = new Intent(context.getApplicationContext(), TripAlarmActivity.class);
        setDataOnIntent(activityIntent, intent);
//        activityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        activityIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(activityIntent);
    }

    public void setDataOnIntent(Intent intentService, Intent intent) {
        Log.i("myTrip", Constants.TRIP_TITLE_KEY + " ::::::setDataOnIntent ALARM: " + intent.getStringExtra(Constants.TRIP_TITLE_KEY));
        intentService.putExtra(Constants.TRIP_ID_KEY, intent.getStringExtra(Constants.TRIP_ID_KEY));
        intentService.putExtra(Constants.TRIP_TITLE_KEY, intent.getStringExtra(Constants.TRIP_TITLE_KEY));
        intentService.putExtra(Constants.TRIP_START_LAT_KEY, intent.getDoubleExtra(Constants.TRIP_START_LAT_KEY, 0.0));
        intentService.putExtra(Constants.TRIP_START_Log_KEY, intent.getDoubleExtra(Constants.TRIP_START_Log_KEY, 0.0));
        intentService.putExtra(Constants.TRIP_DESTINATION_Lat_KEY, intent.getDoubleExtra(Constants.TRIP_DESTINATION_Lat_KEY, 0.0));
        intentService.putExtra(Constants.TRIP_DESTINATION_Log_KEY, intent.getDoubleExtra(Constants.TRIP_DESTINATION_Log_KEY, 0.0));
        intentService.putExtra(Constants.TRIP_DATE_KEY, intent.getLongExtra(Constants.TRIP_DATE_KEY, 0));
        intentService.putExtra(Constants.TRIP_DATE_KEY, intent.getStringExtra(Constants.TRIP_START_ADDRESS_KEY));
        intentService.putExtra(Constants.TRIP_DESTINATION_ADDRESS_KEY, intent.getStringExtra(Constants.TRIP_DESTINATION_ADDRESS_KEY));
    }

}