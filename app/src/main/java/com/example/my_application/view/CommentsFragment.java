package com.example.my_application.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.RecyclerView;

import com.example.my_application.R;
import com.example.my_application.db.AppDB;
import com.example.my_application.db.CommentEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import static com.example.my_application.view.ProfileFragment.name;
import static com.example.my_application.view.ProfileFragment.phoneNum;

public class CommentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private CommentsAdapter adapter;
    private EditText etComment;
    private FloatingActionButton fabSend;

    private AppDB db;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_comments, container, false);

        setActionBar();

        db = ((MainActivity) getActivity()).db;

        recyclerView = view.findViewById(R.id.rv_comments);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL));
        setRecyclerViewData();

        etComment = view.findViewById(R.id.et_my_comment);
        fabSend = view.findViewById(R.id.btn_send_comment);

        fabSend.setOnClickListener(fab -> {
            String comment = etComment.getText().toString();
            SimpleDateFormat format = new SimpleDateFormat("dd-MM-yy HH:mm");
            String nowDate = format.format(new Date());
            if (!comment.isEmpty()) {
                String commentPhone = getCommentPhone();

                CommentEntity commentEntity = new CommentEntity();
                commentEntity.comment = comment;
                commentEntity.commentPhone = commentPhone;
                commentEntity.date = nowDate;
                commentEntity.phone = getPhone();
                db.commentDao().insertAll(commentEntity);

                etComment.setText("");

                setRecyclerViewData();
            }
        });

        return view;
    }

    private String getCommentPhone() {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        return sharedPref.getString(getString(R.string.preference_current_phone), "");
    }

    private void setRecyclerViewData() {
        adapter = new CommentsAdapter((ArrayList<CommentEntity>) db.commentDao().getComments(getPhone()));
        recyclerView.setAdapter(adapter);
    }

    private void setActionBar() {
        ActionBar actionBar = ((MainActivity) getActivity()).getSupportActionBar();
        String profileName = getProfileName();
        if (profileName != null && !profileName.equals("name")) {
            actionBar.setTitle(profileName);
        } else {
            actionBar.setTitle(getPhone());
        }
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_baseline_arrow_back_ios_new_24);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return false;
    }

    private String getPhone() {
        return getArguments().getString(phoneNum);
    }

    private String getProfileName() {
        return getArguments().getString(name);
    }
}
