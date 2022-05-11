package com.toptop;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.toptop.models.User;
import com.toptop.utils.firebase.FirebaseUtil;
import com.toptop.utils.firebase.UserFirebase;

import java.util.Arrays;

public class LoginActivity extends AppCompatActivity {

	public static final String USER = "user";
	// Tag
	private static final String TAG = "LoginActivity";

	private static final String GOOGLE_CLIENT_TOKEN = "409625614338-2nmpv2j05vqnno51icu7jfes7udn5l4k.apps.googleusercontent.com";

	private static final int LOGIN_GOOGLE = 1;
	private static final int LOGIN_FACEBOOK = 2;
	private static final boolean LOGIN_SUCCESS = true;


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
		ImageView loginGoogle = findViewById(R.id.loginGoogle);
		ImageView loginFacebook = findViewById(R.id.loginFacebook);
		username = findViewById(R.id.txtUsername);
		password = findViewById(R.id.txtPassword);

		// Configure sign-in to request the user's ID, email address, and basic
		// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestIdToken(GOOGLE_CLIENT_TOKEN)
				.requestEmail()
				.requestProfile()
				.build();

		GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, gso);

		// Check for existing Google Sign In account, if the user is already signed in
		// the GoogleSignInAccount will be non-null.
		GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
		if (account != null) {
			// Log out
			googleSignInClient.signOut();
		}

		// Register
		register.setOnClickListener(v -> handleRegister());

		// Login
		login.setOnClickListener(v -> handleLogin());

		// Login with Google
		loginGoogle.setOnClickListener(v -> {
			Intent signInIntent = googleSignInClient.getSignInIntent();
			startActivityForResult(signInIntent, LOGIN_GOOGLE);
		});

		CallbackManager callbackManager = CallbackManager.Factory.create();

		String EMAIL = "email";
		LoginButton loginButton = (LoginButton) findViewById(R.id.loginFacebook_button);
		loginButton.setReadPermissions(Arrays.asList(EMAIL));
		// If you are using in a fragment, call loginButton.setFragment(this);

		// Callback registration
		loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
			@Override
			public void onSuccess(LoginResult loginResult) {
				// App code
				Toast.makeText(LoginActivity.this, "Login success", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onCancel() {
				// App code
				Toast.makeText(LoginActivity.this, "Login cancel", Toast.LENGTH_SHORT).show();
			}

			@Override
			public void onError(FacebookException exception) {
				// App code
				Toast.makeText(LoginActivity.this, "Login error", Toast.LENGTH_SHORT).show();
			}
		});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == LOGIN_GOOGLE) {
			Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
			handleSignInResult(task);
		}
	}

	private void handleSignInResult(Task<GoogleSignInAccount> task) {
		try {
			GoogleSignInAccount account = task.getResult(ApiException.class);
			if (account != null) {
				String id = account.getId();
				Query query = FirebaseUtil.getUserByUsername(id);
				query.get().addOnSuccessListener(snapshot -> {
					User user = new User();
					if (snapshot.exists()) {
						user = new User(snapshot.getChildren().iterator().next());
						Toast.makeText(LoginActivity.this, "Welcome back " + user.getFullname(), Toast.LENGTH_SHORT).show();
					} else {
						user.setUsername(id);
						user.setFullname(account.getDisplayName());
						user.setEmail(account.getEmail());
						user.setPhoneNumber("");
						if (account.getPhotoUrl() != null) {
							user.setAvatar(account.getPhotoUrl().toString());
						}
						UserFirebase.addUser(user);
						Toast.makeText(LoginActivity.this, "Welcome " + user.getFullname(), Toast.LENGTH_SHORT).show();
					}
					finishLogin(LOGIN_SUCCESS, user);
				});
			}

		} catch (ApiException e) {
			Log.e(TAG, "Google sign in failed, error code: " + e.getStatusCode());
		}
	}

	// Open RegisterActivity
	private void handleRegister() {
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		intent.putExtra(MainActivity.EXTRA_REGISTER, true);
		setResult(RESULT_CANCELED, intent);
		finish();
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
						finishLogin(LOGIN_SUCCESS, user);
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
				Log.e(TAG, error.getMessage());
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

	// Finish activity
	private void finishLogin(boolean isLogin, User user) {
		Intent intent = new Intent(LoginActivity.this, MainActivity.class);
		if (isLogin == LOGIN_SUCCESS) {
			setResult(RESULT_OK, intent);
			intent.putExtra(USER, user);
		} else {
			setResult(RESULT_CANCELED, intent);
		}
		finish();
	}
}