package com.toptop.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.toptop.R;
import com.toptop.RegisterActivity;


public class InputInfoRegister extends Fragment {
	public final static String TAG = "InputInfoRegister";
	EditText mEditTextName, mEditTextPhone;

	public InputInfoRegister() {
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
		View view = inflater.inflate(R.layout.fragment_input_info_register, container, false);

		// Binding
		Button btnContinue = view.findViewById(R.id.btn_continue_register);
		mEditTextName = view.findViewById(R.id.txt_fullname);
		mEditTextPhone = view.findViewById(R.id.txt_phone_number);

		btnContinue.setOnClickListener(v -> {
			String name = mEditTextName.getText().toString();
			String phonenumber = mEditTextPhone.getText().toString();
			if (isValidInputData(name, phonenumber)) {
				setData(name, phonenumber);
				openInputAccountRegister();
			}
		});

		return view;
	}

	// Open InputAccountRegister fragment
	private void openInputAccountRegister() {
		requireActivity().getSupportFragmentManager().beginTransaction()
				.replace(R.id.input_fragment,
						requireActivity().getSupportFragmentManager().findFragmentByTag(InputAccountRegister.TAG))
				.commit();
	}

	// Check input data
	public boolean isValidInputData(String name, String phonenumber) {
		if (name.isEmpty()) {
			mEditTextName.setError("Please enter your name");
			return false;
		}
		if (phonenumber.isEmpty()) {
			mEditTextPhone.setError("Please enter your phone number");
			return false;
		}
		if (!isPhoneNumberValid(phonenumber)) {
			mEditTextPhone.setError("Please enter a valid phone number");
			return false;
		}
		return true;
	}

	// is phone number valid
	private boolean isPhoneNumberValid(String phonenumber) {
		return phonenumber.length() == 10 && phonenumber.startsWith("0");
	}

	// set data for new user in registerActivity
	public void setData(String name, String phonenumber) {
		RegisterActivity registerActivity = (RegisterActivity) requireActivity();
		registerActivity.newUser.setFullname(name);
		registerActivity.newUser.setPhoneNumber(phonenumber);
	}

}