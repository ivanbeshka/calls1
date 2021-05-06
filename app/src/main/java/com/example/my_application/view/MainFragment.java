package com.example.my_application.view;

import android.app.SearchManager;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CallLog;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.my_application.Log;
import com.example.my_application.R;
import com.example.my_application.data.Call;
import com.example.my_application.db.AppDB;
import com.example.my_application.db.CallEntity;
import com.example.my_application.view.CallsAdapter;
import com.example.my_application.view.MainActivity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

public class MainFragment extends Fragment {
    private RecyclerView recyclerView;
    private CallsAdapter callsAdapter;
    private TextView callsNotFound;
    private TextView searchInGoogle;
    private String savedQuery;

    public MainFragment() {
        super(R.layout.fragment_main);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(R.string.app_name);
        actionBar.setDisplayHomeAsUpEnabled(false);

        recyclerView = view.findViewById(R.id.rv_calls);
        callsNotFound = view.findViewById(R.id.tv_not_found);
        searchInGoogle = view.findViewById(R.id.tv_search_google);
        searchInGoogle.setMovementMethod(LinkMovementMethod.getInstance());

        getCallDetails();

        return view;
    }

    private void getCallDetails() {
        ArrayList<Call> calls = getCalls();

        AppDB db = Room.databaseBuilder(getContext().getApplicationContext(), AppDB.class, "contact.db").allowMainThreadQueries().build();

        ArrayList<Call> dbCalls = new ArrayList<>();
        for (CallEntity dbNumber : db.callDao().getAll()) {
            dbCalls.add(new Call(dbNumber.phone, new Date(), 1, 1, ""));
        }
        callsAdapter = new CallsAdapter(calls, dbCalls);
        recyclerView.setAdapter(callsAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search);
        SearchView searchView =
                (SearchView) searchItem.getActionView();
        searchView.setIconifiedByDefault(false);

        if (!TextUtils.isEmpty(savedQuery)) {
            searchItem.expandActionView();
            searchView.setQuery(savedQuery, true);
            showSearches(savedQuery);
        }

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem menuItem) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem menuItem) {
                callsAdapter.showUserCalls();
                searchView.clearFocus();
                searchView.setQuery("", false);
                recyclerView.setVisibility(View.VISIBLE);
                callsNotFound.setVisibility(View.GONE);
                searchInGoogle.setVisibility(View.GONE);
                return true;
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                Log.debug(s);
                savedQuery = s;
                showSearches(s);
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

    private void showSearches(String s) {
        callsAdapter.filter(s);
        if (callsAdapter.callsIsEmpty()){

            String dynamicUrl = "http://www.google.com/search?q=" + s;
            String linkedText = String.format("<a href=\"%s\">Поиск в Google</a> ", dynamicUrl);

            searchInGoogle.setText(Html.fromHtml(linkedText));
            recyclerView.setVisibility(View.GONE);
            callsNotFound.setVisibility(View.VISIBLE);
            searchInGoogle.setVisibility(View.VISIBLE);
        }else {
            recyclerView.setVisibility(View.VISIBLE);
            callsNotFound.setVisibility(View.GONE);
            searchInGoogle.setVisibility(View.GONE);
        }
    }

    private ArrayList<Call> getCalls() {
        ArrayList<Call> calls = new ArrayList<>();

        Uri contacts = CallLog.Calls.CONTENT_URI;
        Cursor managedCursor = getActivity().getContentResolver().query(contacts, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        int name = managedCursor.getColumnIndex(CallLog.Calls.CACHED_NAME);

        while (managedCursor.moveToNext()) {
            String phNumber = managedCursor.getString(number);
            if (phNumber.startsWith("8")){
                phNumber = phNumber.replaceFirst("8", "+7");
            }
            String callDate = managedCursor.getString(date);
            int currentDuration = managedCursor.getInt(duration);
            String cachedName = managedCursor.getString(name);

            Date currentDate = new Date(Long.parseLong(callDate));

            calls.add(new Call(phNumber, currentDate, currentDuration, 1, cachedName));
        }
        managedCursor.close();

        Collections.sort(calls);
        return getSortedCalls(calls);
    }

    private ArrayList<Call> getSortedCalls(ArrayList<Call> calls) {
        ArrayList<Call> sortedCalls = new ArrayList<>();
        boolean first = true;
        for (Call call : calls) {
            if (first) {
                sortedCalls.add(call);
                first = false;
                continue;
            }

            Call last = sortedCalls.get(sortedCalls.size() - 1);
            if (call.getNum().equals(last.getNum()) &&
                    ((call.getDuration() > 0 && last.getDuration() > 0) || (call.getDuration() == 0 && last.getDuration() == 0))) {
                last.setCallsNum(last.getCallsNum() + 1);
            } else {
                sortedCalls.add(call);
            }
        }
        return sortedCalls;
    }
}
