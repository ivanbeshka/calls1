package com.example.my_application.view;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.room.Room;

import com.example.my_application.Constants;
import com.example.my_application.R;
import com.example.my_application.db.AppDB;
import com.example.my_application.db.CallEntity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import static com.example.my_application.R.id.spam;

public class ProfileFragment extends Fragment {

    public static final String phoneNum = "phone_number";
    public static final String name = "name";

    private TextView profileNum;
    private TextView profileName;
    private Chip chipSpam;
    private ChipGroup groupTags;
    private TextView location;

    private AppDB db;

    public ProfileFragment() {
        super(R.layout.fragment_profile);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

        db = ((MainActivity)getActivity()).db;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.profile);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24);

        initViews(view);

        String phone = setPhoneNum();

        setProfileName();

        setLocation(phone);

        updateSpam(phone);

        return view;
    }

    private String setPhoneNum() {
        String phone = getArguments().getString(phoneNum);
        profileNum.setText(phone);
        return phone;
    }

    private void setProfileName() {
        String profName = getArguments().getString(name);
        if (profName == null) {
            profileName.setVisibility(View.GONE);
        } else {
            profileName.setText(profName);
        }
    }

    private void setLocation(String phone) {
        for (String s : Constants.operatorCodesEkb) {
            if (phone.replace("+7", "").startsWith(s)){
                location.setText("Свердловская область, Россия");
            }
        }
    }

    private void updateSpam(String phone) {
        CallEntity call = db.callDao().getCall(phone);
        if (call != null) {
            if (call.isSpam) {
                chipSpam.setVisibility(View.VISIBLE);
            }else {
                chipSpam.setVisibility(View.GONE);
            }
            if (call.tags != null && !call.tags.isEmpty()) {
                String[] tags = call.tags.split(" ");
                groupTags.removeAllViews();
                for (String tag : tags) {
                    Chip chip = new Chip(this.getContext());
                    chip.setText(tag);
                    groupTags.addView(chip);
                }
            }
        }
    }

    private void initViews(View view) {
        profileNum = view.findViewById(R.id.tv_profile_num);
        profileName = view.findViewById(R.id.tv_profile_name);
        chipSpam = view.findViewById(R.id.chip_spam);
        groupTags = view.findViewById(R.id.group_tags);
        location = view.findViewById(R.id.tv_location);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.profile_menu, menu);

        String phone = profileNum.getText().toString();
        CallEntity call = db.callDao().getCall(phone);
        if (call == null || !call.isSpam){
            menu.getItem(2).setTitle("Спам");
        }else {
            menu.getItem(2).setTitle("Не спам");
        }

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().onBackPressed();
                return true;
            case spam:
                setOnSpamClickListener(item);
                return true;
            case R.id.browse:
                String dynamicUrl = "http://www.google.com/search?q=" + profileNum.getText();
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(dynamicUrl));
                startActivity(browserIntent);
                return true;
            case R.id.comments:
                Bundle bundle = new Bundle();
                bundle.putString(ProfileFragment.phoneNum, profileNum.getText().toString());
                if (!profileName.getText().toString().isEmpty()){
                    bundle.putString(ProfileFragment.name, profileName.getText().toString());
                }

                NavHostFragment navFragment = (NavHostFragment) ((AppCompatActivity) getContext()).getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
                navFragment.getNavController().navigate(R.id.action_profileFragment_to_commentsFragment, bundle);
                return true;
            default:
                return false;
        }
    }

    private void setOnSpamClickListener(@NonNull MenuItem item) {
        String phone = profileNum.getText().toString();
        CallEntity call = db.callDao().getCall(phone);
        if (call == null) {
            CallEntity newCall = new CallEntity();
            newCall.phone = phone;
            newCall.isSpam = true;
            db.callDao().insertAll(newCall);
            item.setTitle("Не спам");
        } else {
            if (call.isSpam) {
                call.isSpam = false;
                item.setTitle("Спам");
            } else {
                call.isSpam = true;
                item.setTitle("Не спам");
            }
            db.callDao().insertAll(call);
        }
        updateSpam(phone);
    }
}
