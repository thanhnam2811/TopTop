package com.toptop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toptop.R;
import com.toptop.RegisterActivity;
import com.toptop.utils.firebase.FirebaseUtil;

public class InputAccountRegister extends Fragment {
	public final static String TAG = "InputAccountRegister";

	EditText mEditTextUsername, mEditTextPassword, mEditTextConfirmPassword;

	public InputAccountRegister() {
		// Required empty public constructor
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

		btnBack.setOnClickListener(v -> {
			openInputInfoRegister();
		});

		btnRegister.setOnClickListener(v -> {
			String username = mEditTextUsername.getText().toString();
			String password = mEditTextPassword.getText().toString();
			String confirmPassword = mEditTextConfirmPassword.getText().toString();

			// check if username is existed
			Query query = FirebaseUtil.getUserByUsername(username);
			query.addListenerForSingleValueEvent(new ValueEventListener() {
				@Override
				public void onDataChange(@NonNull DataSnapshot snapshot) {
					if (snapshot.exists()) {
						mEditTextUsername.setError("Username is existed");
					} else {
						if (isValidInput(username, password, confirmPassword)) {
							setData(username, password);
							((RegisterActivity) requireActivity()).finishRegister(true);
						}
					}
				}

				@Override
				public void onCancelled(@NonNull DatabaseError error) {
					Toast.makeText(requireContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
				}
			});
		});

		return view;
	}


	// open InputInfoRegister fragment
	public void openInputInfoRegister() {
		requireActivity().getSupportFragmentManager().beginTransaction()
				.replace(R.id.input_fragment,
						requireActivity().getSupportFragmentManager().findFragmentByTag(InputInfoRegister.TAG))
				.commit();
	}

	// is valid input data
	private boolean isValidInput(String username, String password, String confirmPassword) {
		if (username.isEmpty()) {
			mEditTextUsername.setError("Username is required");
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

	// set data for new user in register
	public void setData(String username, String password) {
		RegisterActivity registerActivity = (RegisterActivity) requireActivity();
		registerActivity.newUser.setUsername(username);
		registerActivity.newUser.setPassword(password);
	}

}