package com.toptop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.google.firebase.database.Query;
import com.toptop.R;
import com.toptop.RegisterActivity;
import com.toptop.utils.firebase.FirebaseUtil;

import java.util.Objects;

public class InputAccountRegister extends Fragment {
	public final static String TAG = "InputAccountRegister";

	EditText mEditTextUsername, mEditTextPassword, mEditTextConfirmPassword;

	private static final InputAccountRegister instance = new InputAccountRegister();
	private InputAccountRegister() {}
	public static InputAccountRegister getInstance() {
		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		View view = inflater.inflate(R.layout.fragment_input_account_register, container, false);

		// Binding
		Button btnBack = view.findViewById(R.id.btn_back_register);
		Button btnRegister = view.findViewById(R.id.btn_register);
		mEditTextUsername = view.findViewById(R.id.txt_username);
		mEditTextPassword = view.findViewById(R.id.txt_password);
		mEditTextConfirmPassword = view.findViewById(R.id.txt_password_confirm);

		btnBack.setOnClickListener(v -> openInputInfoRegister());

		btnRegister.setOnClickListener(v -> {
			String username = mEditTextUsername.getText().toString();
			String password = mEditTextPassword.getText().toString();
			String confirmPassword = mEditTextConfirmPassword.getText().toString();

			// check if email is existed
			Query query = FirebaseUtil.getUserByUsername(username);
			query.get().addOnCompleteListener(task -> {
				if (task.isSuccessful()) {
					if (task.getResult().getChildrenCount() > 0) {
						mEditTextUsername.setError("Username đã tồn tại");
					} else {
						if (isValidInput(username, password, confirmPassword)) {
							RegisterActivity act = (RegisterActivity) requireActivity();
							act.newUser.setUsername(username);
							act.finishRegister(true, act.newUser.getEmail(), password);
						}
					}
				}
			});
		});

		return view;
	}


	// open InputInfoRegister fragment
	public void openInputInfoRegister() {
		requireActivity().getSupportFragmentManager().beginTransaction()
				.replace(R.id.input_fragment,
						Objects.requireNonNull(requireActivity().getSupportFragmentManager().findFragmentByTag(InputInfoRegister.TAG)))
				.commit();
	}

	// is valid input data
	private boolean isValidInput(String email, String password, String confirmPassword) {
		if (email.isEmpty()) {
			mEditTextUsername.setError("Vui lòng nhập tên tài khoản");
			return false;
		}
		if (password.isEmpty()) {
			mEditTextPassword.setError("Password is required");
			return false;
		}
		if (confirmPassword.isEmpty()) {
			mEditTextConfirmPassword.setError("Confirm password is required");
			return false;
		}
		if (!password.equals(confirmPassword)) {
			mEditTextConfirmPassword.setError("Password and confirm password must be same");
			return false;
		}
		return true;
	}

}