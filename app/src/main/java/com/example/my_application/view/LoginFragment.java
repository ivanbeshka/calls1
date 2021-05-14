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

import org.jetbrains.annotations.NotNull;

import java.time.Duration;
import java.util.List;

public class LoginFragment extends Fragment {

    private TextView toRegister;
    private MaterialButton btnLogin;
    private TextInputEditText etLogin;
    private TextInputEditText etPassword;

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        initView(view);

        toRegister.setOnClickListener(v -> {
            navigateTo(R.id.registerFragment);
        });

        btnLogin.setOnClickListener(v -> {
            String login = etLogin.getEditableText().toString();
            String pass = etPassword.getEditableText().toString();
            UserDao userDao = ((MainActivity) getActivity()).db.userDao();
            List<UserEntity> userEntity = userDao.getUser(login);
            if (userEntity != null && userEntity.size() > 0) {
                UserEntity user = userEntity.get(0);
                if (user.password.equals(pass)) {
                    navigateTo(R.id.mainFragment);
                    setRegistered(user.phone);
                } else {
                    Toast.makeText(getContext(), "Неправильный пароль", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getContext(), "Пользователь не наиден", Toast.LENGTH_LONG).show();
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

    private void initView(View view) {
        etLogin = view.findViewById(R.id.et_login);
        etPassword = view.findViewById(R.id.et_password);
        toRegister = view.findViewById(R.id.tv_to_register);
        btnLogin = view.findViewById(R.id.btn_login);
    }

    private void navigateTo(int resId) {
        NavHostFragment navHostFragment =
                (NavHostFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.nav_host_fragment);

        NavController navController = navHostFragment.getNavController();
        navController.navigate(resId, null, new NavOptions.Builder().setPopUpTo(R.id.loginFragment, true).build());
    }
}
