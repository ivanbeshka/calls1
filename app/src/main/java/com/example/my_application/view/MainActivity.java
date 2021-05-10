package com.example.my_application.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.room.Room;

import android.Manifest;
import android.app.SearchManager;
import android.content.Context;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.SearchView;

import com.example.my_application.CallReceiver;
import com.example.my_application.NotificationShower;
import com.example.my_application.R;
import com.example.my_application.db.AppDB;

public class MainActivity extends AppCompatActivity {

    public AppDB db;

    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 1;
    private static final int MY_PERMISSIONS_REQUEST_READ_PHONE_STATE = 2;
    private static final int MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS = 3;

    private final MyPhoneStateListener phoneStateListener = new MyPhoneStateListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!checkPermissions()) {
            requestPermissions();
        } else {
            navigateToMainFragment();
        }

        db = Room.databaseBuilder(getApplicationContext(), AppDB.class, "contact.db").allowMainThreadQueries().build();

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        phoneStateListener.setContext(getApplicationContext());

        CallReceiver receiver = new CallReceiver();
        IntentFilter filter = new IntentFilter("android.intent.action.PHONE_STATE");
        getApplicationContext().registerReceiver(receiver, filter);
    }

    private void navigateToMainFragment() {
        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        NavController navController = navHostFragment.getNavController();
        navController.navigate(R.id.mainFragment);
    }

    /**
     * this method request to permission asked.
     */
    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_CALL_LOG);

        if (!shouldProvideRationale) {
            // previously and checked "Never ask again".
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_CALL_LOG},
                    REQUEST_PERMISSIONS_REQUEST_CODE);
        }

        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE)) {
            // Permission has not been granted, therefore prompt the user to grant permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_PHONE_STATE},
                    MY_PERMISSIONS_REQUEST_READ_PHONE_STATE);
        }
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.PROCESS_OUTGOING_CALLS)) {
            // Permission has not been granted, therefore prompt the user to grant permission
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.PROCESS_OUTGOING_CALLS},
                    MY_PERMISSIONS_REQUEST_PROCESS_OUTGOING_CALLS);
        }
    }

    /**
     * this method check permission and return current state of permission need.
     */
    private boolean checkPermissions() {
        int permissionStateReadCalls = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_CALL_LOG);
        int permissionStateReadPhoneState = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.READ_PHONE_STATE);
        int permissionStateOutgoingCalls = ActivityCompat.checkSelfPermission(this,
                Manifest.permission.PROCESS_OUTGOING_CALLS);

        return permissionStateReadCalls == PackageManager.PERMISSION_GRANTED &&
                permissionStateReadPhoneState == PackageManager.PERMISSION_GRANTED &&
                permissionStateOutgoingCalls == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_PERMISSIONS_REQUEST_CODE) {
            if (grantResults.length <= 0) {
                // If user interaction was interrupted, the permission request is cancelled and you
                // receive empty arrays.
                Log.i("Error", "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission was granted. Kick off the process of building and connecting
                // GoogleApiClient.
                if (checkPermissions()) {
                    navigateToMainFragment();
                }
            } else {
            }
        }

        if (requestCode == MY_PERMISSIONS_REQUEST_READ_PHONE_STATE) {
            if (grantResults.length <= 0) {
                Log.i("Error", "User interaction was cancelled.");
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (checkPermissions()) {
                    navigateToMainFragment();
                }
            } else {
            }
        }

    }

    private static class MyPhoneStateListener extends PhoneStateListener {

        private Context context;

        public void setContext(Context context){
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
