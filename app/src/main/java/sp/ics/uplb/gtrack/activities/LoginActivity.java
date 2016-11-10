package sp.ics.uplb.gtrack.activities;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;

import sp.ics.uplb.gtrack.R;
import sp.ics.uplb.gtrack.controllers.SharedPref;
import sp.ics.uplb.gtrack.utilities.Common;
import sp.ics.uplb.gtrack.utilities.Constants;
import sp.ics.uplb.gtrack.utilities.Logger;
import sp.ics.uplb.gtrack.utilities.UserLoginTask;

public class LoginActivity extends AppCompatActivity {

    public UserLoginTask userLoginTask = null;
    public AutoCompleteTextView emailView;
    public EditText passwordView;
    private View progressView;
    public View loginFormView;
    public TextView statusBarView;
    private SecretKey secretKey = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailView = (AutoCompleteTextView) findViewById(R.id.email);
        passwordView = (EditText) findViewById(R.id.password);
        statusBarView = (TextView) findViewById(R.id.status_bar);

        Button emailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        if (emailSignInButton != null) {
            emailSignInButton.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    attemptLogin();
                }
            });
        }
        loginFormView = findViewById(R.id.login_form);
        progressView = findViewById(R.id.login_progress);

        if (!TextUtils.isEmpty(SharedPref.getString(getApplicationContext(),Constants.SHARED_PREF,Constants.USER_EMAIL, null))) {
            Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
            mainActivity.putExtra(Constants.USER_EMAIL,      SharedPref.getString(getApplicationContext(),Constants.SHARED_PREF,Constants.USER_EMAIL, null));
            mainActivity.putExtra(Constants.USER_NAME,       SharedPref.getString(getApplicationContext(),Constants.SHARED_PREF,Constants.USER_NAME, null));
            mainActivity.putExtra(Constants.USER_CODE,       SharedPref.getString(getApplicationContext(),Constants.SHARED_PREF,Constants.USER_CODE, null));
            mainActivity.putExtra(Constants.USER_FIREBASEID, SharedPref.getString(getApplicationContext(),Constants.SHARED_PREF,Constants.USER_FIREBASEID, null));
            startActivity(mainActivity);
        }
    }

    private void attemptLogin() {
        Common.updateStatusBar(statusBarView, ContextCompat.getColor(getApplicationContext(), R.color.message), Constants.GLOBAL_BLANK);
        String email = emailView.getText().toString();
        String password = passwordView.getText().toString();
        boolean cancel = false;
        if (TextUtils.isEmpty(password)) {
            Common.updateStatusBar(statusBarView, ContextCompat.getColor(getApplicationContext(), R.color.error), getString(R.string.error_password_required));
            cancel = true;
        }
        else if (!Common.isPasswordValid(password)) {
            Common.updateStatusBar(statusBarView, ContextCompat.getColor(getApplicationContext(), R.color.error), getString(R.string.error_invalid_password));
            cancel = true;
        }

        if (TextUtils.isEmpty(email)) {
            Common.updateStatusBar(statusBarView, ContextCompat.getColor(getApplicationContext(), R.color.error), getString(R.string.error_email_required));
            cancel = true;
        } else if (!Common.isEmailValid(email)) {
            Common.updateStatusBar(statusBarView, ContextCompat.getColor(getApplicationContext(), R.color.error), getString(R.string.error_invalid_email));
            cancel = true;
        }

        if (!cancel) {
            showProgress(true);
            userLoginTask = new UserLoginTask(this, email, password);
            userLoginTask.execute((Void) null);
        }
    }

    public void showProgress(final boolean show) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            loginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            progressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    progressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            progressView.setVisibility(show ? View.VISIBLE : View.GONE);
            loginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    public String encrypt(String initialPassword) {

        DESKeySpec keySpec = null;
        try {
            keySpec = new DESKeySpec(getString(R.string.encrypt_key_spec).getBytes(getString(R.string.encrypt_encoding)));
        } catch (InvalidKeyException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        SecretKeyFactory keyFactory = null;
        try {
            keyFactory = SecretKeyFactory.getInstance(getString(R.string.encrypt_key_factory));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        try {
            if (keyFactory != null) {
                secretKey = keyFactory.generateSecret(keySpec);
            }
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }

        byte[] cleartext = new byte[0];
        try {
            cleartext = initialPassword.getBytes(getString(R.string.encrypt_encoding));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(getString(R.string.encrypt_key_factory));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            if (cipher != null) {
                cipher.init(Cipher.ENCRYPT_MODE, secretKey);
            }
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        String encryptedPassword = null;
        try {
            if (cipher != null) {
                encryptedPassword = Base64.encodeToString(cipher.doFinal(cleartext), Base64.DEFAULT);
            }
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        if (!initialPassword.equals(decrypt(encryptedPassword))) {
            Common.updateStatusBar(statusBarView, ContextCompat.getColor(getApplicationContext(), R.color.error), getString(R.string.error_password_encryption));
        }
        Logger.print("encryptedPassword="+encryptedPassword);
        return encryptedPassword;

    }

    public String decrypt(String encryptedPassword) {

        byte[] encryptedPasswordBytes = Base64.decode(encryptedPassword, Base64.DEFAULT);
        Cipher cipher = null;
        try {
            cipher = Cipher.getInstance(getString(R.string.encrypt_key_factory));
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            e.printStackTrace();
        }
        try {
            if (cipher != null) {
                cipher.init(Cipher.DECRYPT_MODE, secretKey);
            }
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        byte[] initialPasswordInBytes = new byte[0];
        try {
            if (cipher != null) {
                initialPasswordInBytes = (cipher.doFinal(encryptedPasswordBytes));
            }
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            e.printStackTrace();
        }

        String initialPassword = null;
        try {
            initialPassword = new String(initialPasswordInBytes,getString(R.string.encrypt_encoding));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Logger.print("initialPassword="+initialPassword);
        return initialPassword;
    }

}