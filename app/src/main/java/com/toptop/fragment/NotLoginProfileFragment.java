package com.toptop.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.toptop.MainActivity;
import com.toptop.R;
import com.toptop.utils.MyUtil;

public class NotLoginProfileFragment extends Fragment {


	public static final String TAG = "NotLoginProfileFragment";

	public NotLoginProfileFragment() {
		// Required empty public constructor
	}
	private static final NotLoginProfileFragment instance = new NotLoginProfileFragment();
	public static NotLoginProfileFragment getInstance() {
		return instance;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_profile_no_login, container, false);

		// Bind view
		Button btnLogin = view.findViewById(R.id.btn_login);
		btnLogin.setOnClickListener(v -> ((MainActivity) requireActivity()).openLoginActivity());

		Button btnRegister = view.findViewById(R.id.btn_register);
		btnRegister.setOnClickListener(v -> ((MainActivity) requireActivity()).openRegisterActivity());

		return view;
	}
}