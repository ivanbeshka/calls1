package com.example.my_application.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.NavOptions;
import androidx.navigation.fragment.NavHostFragment;

import com.example.my_application.R;
import com.example.my_application.db.UserDao;
import com.example.my_application.db.UserEntity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class RegisterFragment extends Fragment {

    private TextView toLogin;
    private MaterialButton btnRegister;
    private TextInputEditText etLogin;
    private TextInputEditText etPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        initView(view);

        toLogin.setOnClickListener(v -> {
            navigateTo(R.id.loginFragment);
        });

        btnRegister.setOnClickListener(v -> {
            UserDao userDao = ((MainActivity) getActivity()).db.userDao();
            String phone = etLogin.getEditableText().toString();

            List<UserEntity> userEntity = userDao.getUser(phone);
            if (userEntity == null || userEntity.size() == 0){
                UserEntity user = new UserEntity();
                user.password = etPassword.getEditableText().toString();
                user.phone = phone;
                userDao.insertUser(user);
                navigateTo(R.id.mainFragment);
                setRegistered(phone);
            } else {
                Toast.makeText(getContext(), "Логин уже используется", Toast.LENGTH_LONG).show();
            }
        });

        return view;
    }

    private void setRegistered(String phone) {
        SharedPreferences sharedPref = getActivity().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        sharedPref.edit().putBoolean(getString(R.string.preference_is_registered), true).apply();
        sharedPref.edit().putString(getString(R.string.preference_current_phone), phone).apply();
    }

    private void navigateTo(int resId) {
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        NavController navController = navHostFragment.getNavController();
        navController.navigate(resId, null, new NavOptions.Builder().setPopUpTo(R.id.registerFragment, true).build());
    }

    private void initView(View view) {
        etLogin = view.findViewById(R.id.et_login);
        etPassword = view.findViewById(R.id.et_password);
        toLogin = view.findViewById(R.id.tv_to_login);
        btnRegister = view.findViewById(R.id.btn_register);
    }
}
