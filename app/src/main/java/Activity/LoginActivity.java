package Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.speedometer.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import Other.LoggedAccHandler;

public class LoginActivity extends AppCompatActivity {

    private static final String USR_INFO = "USER_INFO";

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
        final String userName = userNameEditText.getText().toString();
        final String password = passwordEditText.getText().toString();

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        DatabaseReference reference = databaseReference.child("Users").child(userName);
        Query checkUser = reference.orderByChild("userName").equalTo(userName);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    validatePasswordError(true);
                    validateUserNameError(true);
                    String pswrdFromDB = dataSnapshot.child(USR_INFO).child("password").getValue(String.class);
                    if (pswrdFromDB.equals(password)){
                        String userNameFromDB = dataSnapshot.child(USR_INFO).child("userName").getValue(String.class);
                        LoggedAccHandler lah = new LoggedAccHandler();
                        lah.setLoggedUserName(userNameFromDB);
                        Toast.makeText(context, "Logged!", Toast.LENGTH_LONG).show();
                        finish();
                    }else{
                        validatePasswordError(false);
                    }
                }else {
                    validateUserNameError(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean validateUserNameError(boolean b) {
        if (!b){
            userNameTextView.setText("Wrong Username!");
            userNameTextView.setTextColor(getResources().getColor(R.color.red));
            return false;
        }else {
            userNameTextView.setText("User Name");
            userNameTextView.setTextColor(getResources().getColor(R.color.black));
        }
        return true;
    }

    private boolean validatePasswordError(boolean b) {
        if (!b){
            passwordTextView.setText("Wrong Password!");
            passwordTextView.setTextColor(getResources().getColor(R.color.red));
            return false;
        }else {
            passwordTextView.setText("Password");
            passwordTextView.setTextColor(getResources().getColor(R.color.black));
        }
        return true;
    }
}
