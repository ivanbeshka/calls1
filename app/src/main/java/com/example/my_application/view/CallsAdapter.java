package com.example.my_application.view;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.appcompat.widget.PopupMenu;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.my_application.Log;
import com.example.my_application.R;
import com.example.my_application.data.Call;
import com.example.my_application.db.AppDB;
import com.example.my_application.db.CallEntity;

import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class CallsAdapter extends RecyclerView.Adapter<CallsAdapter.ViewHolder> {

    private ArrayList<Call> calls;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvCallNum;
        private final TextView tvCallTime;
        private final AppCompatImageButton ibPopUpMenu;

        public ViewHolder(View view) {
            super(view);
// Define click listener for the ViewHolder's View

            ibPopUpMenu = view.findViewById(R.id.iv_call_more);
            tvCallNum = view.findViewById(R.id.call_num);
            tvCallTime = view.findViewById(R.id.call_date);
        }
    }

    public CallsAdapter(ArrayList<Call> dataSet) {
        calls = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
// Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.call_item, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NotNull ViewHolder viewHolder, final int position) {

// Get element from your dataset at this position and replace the
// contents of the view with that element
        Call currentCall = calls.get(position);

//call number
        String callsNum = currentCall.getCallsNum() > 1 ? " (" + currentCall.getCallsNum() + ")" : "";
        if (currentCall.getCachedName() != null && !currentCall.getCachedName().isEmpty()) {
            viewHolder.tvCallNum.setText(currentCall.getCachedName() + callsNum);
        } else {
            viewHolder.tvCallNum.setText(currentCall.getNum() + callsNum);
        }

//date
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy HH:mm");
        String callDayTime = format.format(currentCall.getDate());
        viewHolder.tvCallTime.setText(callDayTime);

//number color
        if (currentCall.getDuration() == 0) {
            int color = viewHolder.tvCallNum.getResources().getColor(R.color.missed_call);
            viewHolder.tvCallNum.setTextColor(color);
        } else {
            viewHolder.tvCallNum.setTextColor(viewHolder.tvCallTime.getCurrentTextColor());
        }

        setCallMenuClickListener(viewHolder, currentCall);

        viewHolder.tvCallNum.setOnClickListener(textView -> {
            Bundle bundle = new Bundle();
            bundle.putString(ProfileFragment.phoneNum, currentCall.getNum());
            bundle.putString(ProfileFragment.name, currentCall.getCachedName());
            NavHostFragment navFragment = (NavHostFragment) ((AppCompatActivity) textView.getContext()).getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);
            navFragment.getNavController().navigate(R.id.action_mainFragment_to_profileFragment, bundle);
        });
    }

    private void setCallMenuClickListener(@NotNull ViewHolder viewHolder, Call currentCall) {
        AppDB db = Room.databaseBuilder(viewHolder.itemView.getContext().getApplicationContext(),
            AppDB.class, "contact.db").allowMainThreadQueries().build();
        viewHolder.ibPopUpMenu.setOnClickListener(view -> {
            PopupMenu popup = new PopupMenu(view.getContext(), view);
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.copy:
                        ClipboardManager clipboard = (ClipboardManager)
                                view.getContext().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Number", currentCall.getNum());
                        clipboard.setPrimaryClip(clip);
                        return true;
                    case R.id.spam:

                        String phone = currentCall.getNum();
                        Log.debug(phone);
                        CallEntity call = db.callDao().getCall(phone);
                        if (call == null) {
                            call = new CallEntity();
                            call.phone = phone;
                            call.isSpam = true;
                            item.setTitle("Не спам");
                        } else {
                            if (call.isSpam) {
                                call.isSpam = false;
                                item.setTitle("Спам");
                            } else {
                                call.isSpam = true;
                                item.setTitle("Не спам");
                            }
                            db.callDao().delete(phone);
                        }
                        db.callDao().insertAll(call);
                        return true;
                    default:
                        return false;
                }
            });

            MenuInflater inflater = popup.getMenuInflater();
            Menu menu = popup.getMenu();
            inflater.inflate(R.menu.call_menu, menu);

            String phone = currentCall.getNum();
            CallEntity call = db.callDao().getCall(phone);
            if (call == null || !call.isSpam){
                menu.getItem(1).setTitle("Спам");
            }else {
                menu.getItem(1).setTitle("Не спам");
            }

            popup.show();
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return calls.size();
    }
}