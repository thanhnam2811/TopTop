package com.toptop;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.toptop.utils.MyUtil;

public class ChangePasswordActivity extends AppCompatActivity {

	EditText edtOldPassword, edtNewPassword, edtConfirmPassword;
	TextView txtChangePassword;
	ImageView icBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_password);
		MyUtil.setStatusBarColor(MyUtil.STATUS_BAR_LIGHT_MODE, this);

		// Bind views
		edtOldPassword = findViewById(R.id.edt_old_password);
		edtNewPassword = findViewById(R.id.edt_new_password);
		edtConfirmPassword = findViewById(R.id.edt_confirm_password);
		txtChangePassword = findViewById(R.id.txt_change_password);
		icBack = findViewById(R.id.ic_back);

		// Set onclick listener
		txtChangePassword.setOnClickListener(v -> handleChangePassword());
		icBack.setOnClickListener(v -> finish());
	}

	private void handleChangePassword() {
		if (isValidInput()) {
			String oldPassword = edtOldPassword.getText().toString();
			String newPassword = edtNewPassword.getText().toString();

			FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
			if (user != null) {
				String email = user.getEmail();
				assert email != null;
				AuthCredential credential = EmailAuthProvider.getCredential(email, oldPassword);

				user.reauthenticate(credential).addOnCompleteListener(task -> {
					if (task.isSuccessful()) {
						user.updatePassword(newPassword).addOnCompleteListener(task1 -> {
							if (!task1.isSuccessful()) {
								Toast.makeText(ChangePasswordActivity.this, "Cập nhật mật khẩu thất bại! \n Lỗi: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
							} else {
								Toast.makeText(ChangePasswordActivity.this, "Cập nhật mật khẩu thành công", Toast.LENGTH_SHORT).show();
								finish();
							}
						});
					}
				});
			} else {
				Toast.makeText(ChangePasswordActivity.this, "Không tìm thấy tài khoản", Toast.LENGTH_SHORT).show();
			}
		}
	}

	private boolean isValidInput() {
		String oldPassword = edtOldPassword.getText().toString();
		String newPassword = edtNewPassword.getText().toString();
		String confirmPassword = edtConfirmPassword.getText().toString();

		if (oldPassword.isEmpty()) {
			edtOldPassword.setError("Vui lòng nhập mật khẩu cũ");
			return false;
		} else if (newPassword.isEmpty()) {
			edtNewPassword.setError("Vui lòng nhập mật khẩu mới");
			return false;
		} else if (newPassword.length() < 6) {
			edtNewPassword.setError("Mật khẩu phải có ít nhất 6 ký tự");
			return false;
		}
		else if (confirmPassword.isEmpty()) {
			edtConfirmPassword.setError("Vui lòng nhập lại mật khẩu mới");
			return false;
		} else if (!newPassword.equals(confirmPassword)) {
			edtConfirmPassword.setError("Mật khẩu mới và xác nhận mật khẩu mới không khớp");
			return false;
		} else {
			return true;
		}
	}
}