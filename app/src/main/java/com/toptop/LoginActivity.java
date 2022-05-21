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
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.Query;
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
	private static final boolean LOGIN_FAIL = false;

	private FirebaseAuth mAuth;


	EditText email, password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		mAuth = FirebaseAuth.getInstance();

		getWindow().setStatusBarColor(Color.WHITE);
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);

		// Binding
		TextView register = findViewById(R.id.txtRegister);
		Button login = findViewById(R.id.btnLogin);
		ImageView loginGoogle = findViewById(R.id.loginGoogle);
		ImageView loginFacebook = findViewById(R.id.loginFacebook);
		email = findViewById(R.id.txtEmail);
		password = findViewById(R.id.txtPassword);
		TextView forgotPassword = findViewById(R.id.txt_forgot_password);

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

		// Forgot password
		forgotPassword.setOnClickListener(v -> handleForgotPassword());
	}

	private void handleForgotPassword() {
		String emailText = email.getText().toString();
		if (emailText.isEmpty()) {
			email.setError("Vui lòng nhập email hoặc username!");
		} else {
			if (emailText.contains("@")) {
				sendEmailResetPassword(emailText);
			} else {
				Query query = FirebaseUtil.getUserByUsername(emailText);
				query.get().addOnSuccessListener(documentSnapshot -> {
					if (documentSnapshot.exists()) {
						User user = new User(documentSnapshot.getChildren().iterator().next());
						sendEmailResetPassword(user.getEmail());
					}
				});
			}
		}
	}

	private void sendEmailResetPassword(String email) {
		mAuth.sendPasswordResetEmail(email)
				.addOnCompleteListener(task -> {
					if (task.isSuccessful()) {
						Toast.makeText(LoginActivity.this, "Email xác nhận đã được gửi đến email của bạn", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(LoginActivity.this, "Email không tồn tại", Toast.LENGTH_SHORT).show();
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
				fireBaseAuthWithGoogle(account);
			} else {
				Toast.makeText(this, "Account is null", Toast.LENGTH_SHORT).show();
			}
		} catch (ApiException e) {
			Log.e(TAG, "Google sign in failed, error code: " + e.getStatusCode());
		}
	}

	private void fireBaseAuthWithGoogle(GoogleSignInAccount account) {
		AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
		mAuth.signInWithCredential(credential)
				.addOnCompleteListener(this, task -> {
					FirebaseUser firebaseUser = mAuth.getCurrentUser();
					if (task.isSuccessful() && firebaseUser != null) {
						// Sign in success, update UI with the signed-in user's information
						Log.d(TAG, "signInWithCredential:success");
						Query query = FirebaseUtil.getUserByEmail(firebaseUser.getEmail());
						query.get().addOnSuccessListener(dataSnapshot -> {
							if (dataSnapshot.exists()) {
								User user = new User(dataSnapshot.getChildren().iterator().next());
								Toast.makeText(LoginActivity.this, "Welcome back, Google User: " + user.getFullname(), Toast.LENGTH_SHORT).show();
								finishLogin(LOGIN_SUCCESS, user);
							} else {
								// Create user
								User user = new User();
								user.setUsername("GG-" + account.getId());
								user.setEmail(account.getEmail());
								user.setFullname(account.getDisplayName());
								if (account.getPhotoUrl() != null) {
									user.setAvatar(account.getPhotoUrl().toString());
								}
								UserFirebase.addUser(user);
								Toast.makeText(LoginActivity.this, "Welcome, Google User: " + user.getFullname(), Toast.LENGTH_SHORT).show();
								finishLogin(LOGIN_SUCCESS, user);
							}
						});
					} else {
						Toast.makeText(LoginActivity.this, "Đăng nhập thất bại, vui lòng kiểm tra internet", Toast.LENGTH_SHORT).show();
					}
				});
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
		// Check if email and password are empty
		if (isValidInput()) {
			String emailText = email.getText().toString();
			String passwordText = password.getText().toString();
			if (emailText.contains("@"))
				loginFirebase(emailText, passwordText);
			else {
				Query query = FirebaseUtil.getUserByUsername(emailText);
				query.get().addOnSuccessListener(dataSnapshot -> {
					if (dataSnapshot.exists()) {
						User user = new User(dataSnapshot.getChildren().iterator().next());
						loginFirebase(user.getEmail(), passwordText);
					}
				});
			}
		}
	}

	private void loginFirebase(String emailText, String passwordText) {
		mAuth.signInWithEmailAndPassword(emailText, passwordText)
				.addOnCompleteListener(this, task -> {
					FirebaseUser user = mAuth.getCurrentUser();
					if (task.isSuccessful() && user != null) {
						// Sign in success, update UI with the signed-in user's information
						Log.d(TAG, "signInWithEmail:success");
						Query query = FirebaseUtil.getUserByEmail(user.getEmail());
						query.get().addOnSuccessListener(snapshot -> {
							if (snapshot.exists()) {
								User user1 = new User(snapshot.getChildren().iterator().next());
								Toast.makeText(LoginActivity.this, "Welcome back, FirebaseUser: " + user1.getFullname(), Toast.LENGTH_SHORT).show();
								finishLogin(LOGIN_SUCCESS, user1);
							} else {
								finishLogin(LOGIN_FAIL, null);
							}
						}).addOnFailureListener(e -> {
							Log.e(TAG, "loginFirebase: " + e.getMessage());
							finishLogin(LOGIN_FAIL, null);
						});
					} else {
						Toast.makeText(LoginActivity.this, "Đăng nhập thất bại, vui lòng kiểm tra lại thông tin đăng nhập", Toast.LENGTH_SHORT).show();
						password.setError("Vui lòng kiểm tra lại mật khẩu");
					}
				});
	}

	private boolean isValidInput() {
		if (email.getText().toString().isEmpty()) {
			email.setError("Username is required");
			email.requestFocus();
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