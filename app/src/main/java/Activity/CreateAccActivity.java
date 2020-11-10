package Activity;

import android.content.Context;
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

import Other.CreateAccHandler;

public class CreateAccActivity extends AppCompatActivity {

    private TextView createUserNameTextView;
    private TextView createPasswordTextView;
    private EditText createUserNameEditText;
    private EditText createPasswordEditText;

    private Button createAccButton;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    private Context context = this;

    private boolean userExists = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.createacc_activity);
        initView();
    }

    private void initView() {
        createUserNameTextView = findViewById(R.id.createUserNameTextView);
        createPasswordTextView = findViewById(R.id.createPasswordTextView);
        createUserNameEditText = findViewById(R.id.createUserNameEditText);
        createPasswordEditText = findViewById(R.id.createPasswordEditText);

        createAccButton = findViewById(R.id.createAccButton);

        createAccButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateUserName() && validatePassword() && userExists){
                    createAcc();
                }
            }
        });

        createUserNameEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false){
                    validateUserName();
                    isUserExists();
                }
            }
        });
        createPasswordEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus == false){
                    validatePassword();
                }
            }
        });
    }

    private void isUserExists() {
        final String getUserName = createUserNameEditText.getText().toString();

        databaseReference = FirebaseDatabase.getInstance().getReference("Users");
        Query checkUser = databaseReference.orderByChild("userName").equalTo(getUserName);

        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String userNamefromDB = dataSnapshot.child(getUserName).child("userName").getValue(String.class);
                    if (userNamefromDB.equals(getUserName)){
                        userExists(userNamefromDB);
                    }
                }else {
                    userExists = true;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private boolean userExists(String username) {
        if (!username.isEmpty()){
            createUserNameTextView.setText("User already exist!");
            createUserNameTextView.setTextColor(getResources().getColor(R.color.red));
            return userExists = false;
        }
        return userExists = true;
    }

    private boolean validatePassword() {
        if (TextUtils.isEmpty(createPasswordEditText.getText().toString())){
            createPasswordTextView.setText("Password must be added!");
            createPasswordTextView.setTextColor(getResources().getColor(R.color.red));
            return false;
        }else {
            createPasswordTextView.setText("Password");
            createPasswordTextView.setTextColor(getResources().getColor(R.color.black));
        }
        return true;
    }

    private boolean validateUserName() {
        String validateLength = createUserNameEditText.getText().toString();
        String whiteSpace = "\\A\\w{4,20}\\z";
        if (TextUtils.isEmpty(createUserNameEditText.getText().toString())){
            createUserNameTextView.setText("User Name must be added!");
            createUserNameTextView.setTextColor(getResources().getColor(R.color.red));
            return false;
        }else if (validateLength.length() >=15){
            createUserNameTextView.setText("User Name is too long! (more than 15 characters)");
            createUserNameTextView.setTextColor(getResources().getColor(R.color.red));
            return false;
        }else if (!validateLength.matches(whiteSpace)){
            createUserNameTextView.setText("White Spaces are not allowed!");
            createUserNameTextView.setTextColor(getResources().getColor(R.color.red));
            return false;
        }else{
            createUserNameTextView.setText("User Name");
            createUserNameTextView.setTextColor(getResources().getColor(R.color.black));
        }
        return true;
    }

    private void createAcc() {
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Users");
        String username = createUserNameEditText.getText().toString();
        String password = createPasswordEditText.getText().toString();
        CreateAccHandler handler = new CreateAccHandler(username, password);
        databaseReference.child(username).setValue(handler);
        Toast.makeText(context, "Account Created!", Toast.LENGTH_LONG).show();
    }
}
