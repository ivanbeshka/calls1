package com.example.my_application.view;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.preference.PreferenceFragmentCompat;

import com.example.my_application.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.settings);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home){
            getActivity().onBackPressed();
            return true;
        }
        return false;
    }
}
