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


public class InputInfoRegister extends Fragment {
	public final static String TAG = "InputInfoRegister";
	EditText mEditTextName, mEditTextPhone, mEditTextEmail;

	private static final InputInfoRegister instance = new InputInfoRegister();
	private InputInfoRegister() {
	}
	public static InputInfoRegister getInstance() {
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
		View view = inflater.inflate(R.layout.fragment_input_info_register, container, false);

		// Binding
		Button btnContinue = view.findViewById(R.id.btn_continue_register);
		mEditTextName = view.findViewById(R.id.txt_fullname);
		mEditTextPhone = view.findViewById(R.id.txt_phone_number);
		mEditTextEmail = view.findViewById(R.id.txt_email);

		btnContinue.setOnClickListener(v -> handleContinueButton());

		return view;
	}

	private void handleContinueButton() {
		String name = mEditTextName.getText().toString();
		String phonenumber = mEditTextPhone.getText().toString();
		String email = mEditTextEmail.getText().toString();

		if (isValidInputData(name, phonenumber, email)) {
			setData(name, phonenumber, email);

			Query query = FirebaseUtil.getUserByEmail(email);
			query.get().addOnCompleteListener(task -> {
				if (task.isSuccessful()) {
					if (task.getResult().getChildrenCount() > 0)
						mEditTextEmail.setError("Email đã tồn tại");
					else
						openInputAccountRegister();
				}
			});
		}
	}

	// Open InputAccountRegister fragment
	private void openInputAccountRegister() {
		requireActivity().getSupportFragmentManager().beginTransaction()
				.replace(R.id.input_fragment,
						Objects.requireNonNull(requireActivity().getSupportFragmentManager().findFragmentByTag(InputAccountRegister.TAG)))
				.commit();
	}

	// Check input data
	public boolean isValidInputData(String name, String phonenumber, String email) {
		if (email.isEmpty()) {
			mEditTextEmail.setError("Vui lòng nhập email");
			return false;
		}
		if (name.isEmpty()) {
			mEditTextName.setError("Vui lòng nhập họ tên");
			return false;
		}
		if (phonenumber.isEmpty()) {
			mEditTextPhone.setError("Vui lòng nhập số điện thoại");
			return false;
		}
		if (!isPhoneNumberValid(phonenumber)) {
			mEditTextPhone.setError("Số điện thoại không hợp lệ");
		}
		return true;
	}

	// is phone number valid
	private boolean isPhoneNumberValid(String phonenumber) {
		phonenumber = phonenumber.trim().replace("+84", "0");
		return phonenumber.length() == 10 && phonenumber.startsWith("0");
	}

	// set data for new user in registerActivity
	public void setData(String name, String phonenumber, String email) {
		RegisterActivity registerActivity = (RegisterActivity) requireActivity();
		registerActivity.newUser.setFullname(name);
		registerActivity.newUser.setPhoneNumber(phonenumber);
		registerActivity.newUser.setEmail(email);
	}

}