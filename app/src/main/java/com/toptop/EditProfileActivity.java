package com.toptop;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.toptop.models.User;
import com.toptop.utils.MyUtil;
import com.toptop.utils.firebase.UserFirebase;

public class EditProfileActivity extends AppCompatActivity {
	EditText edtFullName, edtEmail, edtPhoneNumber;
	TextView txtChangePassword, txtDeleteAccount, txtSave;
	ImageView icBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_profile);

		// Get user's information
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		User user = (User) bundle.getSerializable(User.TAG);

		// Bind views
		edtFullName = findViewById(R.id.edt_fullname);
		edtEmail = findViewById(R.id.edt_email);
		edtPhoneNumber = findViewById(R.id.edt_phonenumber);
		txtChangePassword = findViewById(R.id.txt_change_password);
		txtDeleteAccount = findViewById(R.id.txt_delete_account);
		icBack = findViewById(R.id.ic_back);
		txtSave = findViewById(R.id.txt_save);

		// Set user's information
		edtFullName.setText(user.getFullname());
		edtEmail.setText(user.getEmail());
		edtPhoneNumber.setText(user.getPhoneNumber());

		// Set onclick listener
		icBack.setOnClickListener(v -> finish());
		txtSave.setOnClickListener(v -> handleSave(user));
		txtChangePassword.setOnClickListener(v -> handleChangePassword());

		MyUtil.setStatusBarColor(MyUtil.STATUS_BAR_LIGHT_MODE, this);
	}

	private void handleChangePassword() {
		Intent intent = new Intent(this, ChangePasswordActivity.class);
		startActivity(intent);
	}

	private void handleSave(User user) {
		// Get user's information
		String fullname = edtFullName.getText().toString();
		String email = edtEmail.getText().toString();
		String phoneNumber = edtPhoneNumber.getText().toString();

		if (isValid(fullname, email, phoneNumber)) {
			// Set user's information
			user.setFullname(fullname);
			user.setEmail(email);
			user.setPhoneNumber(phoneNumber);
			UserFirebase.updateUser(user);

			// Go to profile activity
			Toast.makeText(this, "Lưu thành công", Toast.LENGTH_SHORT).show();
			finish();
		} else
			Toast.makeText(this, "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
	}

	private boolean isValid(String fullname, String email, String phoneNumber) {
		if (fullname.isEmpty()) {
			edtFullName.setError("Hãy nhập họ tên");
			return false;
		} else if (email.isEmpty()) {
			edtEmail.setError("Không được để trống");
			return false;
		} else if (phoneNumber.isEmpty()) {
			edtPhoneNumber.setError("Không được để trống");
			return false;
		}
		return true;
	}
}