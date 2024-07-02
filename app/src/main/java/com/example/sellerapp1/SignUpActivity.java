package com.example.sellerapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;

import java.io.IOException;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignUpActivity extends AppCompatActivity {

    Button buttonSignUp;
    EditText editTextEmail,editTextCNIC;
    EditText editTextPassword,editTextCPassword;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Find the buttonSignUp view
        buttonSignUp = findViewById(R.id.buttonSignUp);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPassword = findViewById(R.id.editTextPassword);
        editTextCNIC = findViewById(R.id.editTextCNIC);
        editTextCPassword = findViewById(R.id.editTextCPassword);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://falakshop.online//")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        //WordPressApiService apiService = retrofit.create(WordPressApiService.class);

        // Set an OnClickListener on buttonSignUp
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = "Hamza";
                String email = editTextEmail.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                Log.d("SignupAttempt", "Username: " + username + ", Email: " + email);
                // Call the API



            }
        });

    }

    private boolean isFemaleCNIC(String cnic) {
        if(cnic.length() != 13){
            Toast.makeText(SignUpActivity.this,"Cnic Digits should be 13",Toast.LENGTH_SHORT).show();
            return false;
        }
        char lastChar = cnic.charAt(cnic.length()-1);
        return Character.isDigit(lastChar) && Character.getNumericValue(lastChar) % 2 == 0;
    }

    private boolean isValidPassword(String password) {
        String specialCharacters = "^@%£";
        Pattern pattern = Pattern.compile("[^a-zA-Z0-9" + Pattern.quote(specialCharacters)+ "]");
        Matcher matcher = pattern.matcher(password);
        return !matcher.find();
    }


}
