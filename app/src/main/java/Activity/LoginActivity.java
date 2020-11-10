package Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.speedometer.R;

public class LoginActivity extends AppCompatActivity {

    private TextView userNameTextView;
    private TextView passwordTextView;
    private TextView createAccount;
    private EditText userNameEditText;
    private EditText passwordEditText;

    private Button loginButton;

    private Context context = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        initView();
    }

    private void initView() {
        userNameTextView = findViewById(R.id.userNameTextView);
        passwordTextView = findViewById(R.id.passwordTextView);
        createAccount = findViewById(R.id.createAcc);
        userNameEditText = findViewById(R.id.userNameEditText);
        passwordEditText = findViewById(R.id.passwordEditText);

        loginButton = findViewById(R.id.loginButton);

        createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CreateAccActivity.class);
                startActivity(intent);
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateUserName() && validatePassword()){
                    login();
                }

            }
        });

        userNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false){
                    validateUserName();
                }
            }
        });
        passwordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false){
                    validatePassword();
                }
            }
        });
    }

    private boolean validatePassword() {
        if (TextUtils.isEmpty(passwordEditText.getText().toString())){
            passwordTextView.setText("Password must be added!");
            passwordTextView.setTextColor(getResources().getColor(R.color.red));
            return false;
        }else {
            passwordTextView.setText("Password");
            passwordTextView.setTextColor(getResources().getColor(R.color.black));
        }
        return true;
    }

    private boolean validateUserName() {
        if (TextUtils.isEmpty(userNameEditText.getText().toString())){
            userNameTextView.setText("User Name must be added!");
            userNameTextView.setTextColor(getResources().getColor(R.color.red));
            return false;
        }else {
            userNameTextView.setText("User Name");
            userNameTextView.setTextColor(getResources().getColor(R.color.black));
        }
        return true;
    }

    private void login() {

    }
}
