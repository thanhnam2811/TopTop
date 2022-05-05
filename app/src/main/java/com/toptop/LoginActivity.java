package com.toptop;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toptop.models.User;
import com.toptop.utils.firebase.FirebaseUtil;

public class LoginActivity extends AppCompatActivity {

	// Tag
	private static final String TAG = "LoginActivity";
	EditText username, password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		getWindow().setStatusBarColor(Color.WHITE);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

		// Binding
		TextView register = findViewById(R.id.txtRegister);
		Button login = findViewById(R.id.btnLogin);
		username = findViewById(R.id.txtUsername);
		password = findViewById(R.id.txtPassword);

		// Register
		register.setOnClickListener(v -> handleRegister());

		// Login
		login.setOnClickListener(v -> handleLogin());
	}

	// Open RegisterActivity
	private void handleRegister() {
		finish();
		Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
		startActivity(intent);
	}

	// Login
	private void handleLogin() {
		// Check if username and password are empty
		if (isValidInput()) {
			loginFirebase();
		}
	}

	private void loginFirebase() {
		String usernameText = username.getText().toString();
		String passwordText = password.getText().toString();

		Query query = FirebaseUtil.getUserByUsername(usernameText);
		query.addValueEventListener(new ValueEventListener() {
			@Override
			public void onDataChange(@NonNull DataSnapshot snapshot) {
				if (snapshot.exists()) {
					// Log
					Log.i(TAG, "Login with username: " + usernameText + " and password: " + passwordText);
					User user = new User(snapshot.getChildren().iterator().next());
					if (passwordText.equals(user.getPassword())) {
						MainActivity.setCurrentUser(user);
						finish();
					} else {
						password.setError("Wrong password");
						password.requestFocus();
						Toast.makeText(LoginActivity.this, "Wrong password", Toast.LENGTH_SHORT).show();
					}
				} else {
					username.setError("User not found");
					username.requestFocus();
					Toast.makeText(LoginActivity.this, "User not found", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onCancelled(@NonNull DatabaseError error) {
				Toast.makeText(LoginActivity.this, error.getMessage(), Toast.LENGTH_SHORT).show();
			}
		});
	}

	private boolean isValidInput() {
		if (username.getText().toString().isEmpty()) {
			username.setError("Username is required");
			username.requestFocus();
			return false;
		} else if (password.getText().toString().isEmpty()) {
			password.setError("Password is required");
			password.requestFocus();
			return false;
		} else {
			return true;
		}
	}
}