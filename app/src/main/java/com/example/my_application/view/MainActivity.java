package com.example.my_application.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;
import androidx.room.Room;

import android.Manifest;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.example.my_application.CallReceiver;
import com.example.my_application.NotificationShower;
import com.example.my_application.R;
import com.example.my_application.db.AppDB;

public class MainActivity extends AppCompatActivity {

    public AppDB db;

    private static final int REQUEST_PERMISSIONS_READ_CALL_LOG = 1;
    private static final int MY_PERMISSIONS_REQUEST_ANSWER_CALLS = 4;

    private final MyPhoneStateListener phoneStateListener = new MyPhoneStateListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            navigateUser();
        }

        db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "contact.db").allowMainThreadQueries().build();

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        phoneStateListener.setContext(getApplicationContext());

        CallReceiver receiver = new CallReceiver();
        IntentFilter filter = new IntentFilter("android.intent.action.PHONE_STATE");
        getApplicationContext().registerReceiver(receiver, filter);
    }

    private void navigateUser() {
        if (isUserRegistered()) {
            navigateTo(R.id.mainFragment, R.id.registerFragment);
        } else {
            navigateTo(R.id.registerFragment, R.id.mainFragment);
        }
    }

    private boolean isUserRegistered() {
        SharedPreferences sharedPref = getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getBoolean(getString(R.string.preference_is_registered), false);
    }

    private void navigateTo(int resId, int popUpTo) {
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        NavController navController = navHostFragment.getNavController();
        navController.navigate(resId, null, new NavOptions.Builder().setPopUpTo(popUpTo, true).build());
    }



    /**
     * this method request to permission asked.
     */
    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CALL_LOG);

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CALL_LOG},
                    REQUEST_PERMISSIONS_READ_CALL_LOG);
        }

        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ANSWER_PHONE_CALLS) != PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ANSWER_PHONE_CALLS},
                        MY_PERMISSIONS_REQUEST_ANSWER_CALLS);
            }
        }
    }

    /**
     * this method check permission and return current state of permission need.
     */
    private boolean checkPermissions() {
        int permissionStateReadCalls = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALL_LOG);
        int permissionStateAnswerCalls = PackageManager.PERMISSION_DENIED;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
            permissionStateAnswerCalls = ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ANSWER_PHONE_CALLS);
        }

        return permissionStateReadCalls == PackageManager.PERMISSION_GRANTED &&
                permissionStateAnswerCalls == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_READ_CALL_LOG) {
            if (grantResults.length <= 0) {
                Log.i("Error", "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (checkPermissions()) {
                    navigateUser();
                }
            } else {
            }
        }

        if (requestCode == MY_PERMISSIONS_REQUEST_ANSWER_CALLS) {
            if (grantResults.length <= 0) {
                Log.i("Error", "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (checkPermissions()) {
                    navigateUser();
                }
            } else {
            }
        }

    }

    private static class MyPhoneStateListener extends PhoneStateListener {

        private Context context;

        public void setContext(Context context) {
            this.context = context;
        }

        @Override
        public void onCallStateChanged(int state, String phoneNumber) {

            if (state == TelephonyManager.CALL_STATE_RINGING) {
                Log.d("CallRecorder", "CALL_STATE_RINGING");
                NotificationShower.show(context, phoneNumber);
            }
        }
    }
}
