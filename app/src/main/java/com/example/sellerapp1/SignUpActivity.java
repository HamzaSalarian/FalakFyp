package com.example.sellerapp1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        auth = FirebaseAuth.getInstance();

        // Set an OnClickListener on buttonSignUp
        buttonSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Create an Intent to start the MainActivity

                String email = editTextEmail.getText().toString().trim();
                String cnic = editTextCNIC.getText().toString().trim();
                String password = editTextPassword.getText().toString().trim();
                String cPassword = editTextCPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    editTextEmail.setError("Enter email address!");
                    return;
                }

                if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    editTextEmail.setError("Enter a valid email address!");
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    editTextPassword.setError("Enter password!");
                    return;
                }
                if (!isValidPassword(password)) {
                    editTextPassword.setError("Password can only contain special characters: ^%£\"");
                    return;
                }
                if(TextUtils.isEmpty(cPassword)){
                    editTextCPassword.setError("Confirm password!");
                    return;
                }

                if(!password.equals(cPassword)){
                    editTextCPassword.setError("Passwords do not match!");
                    return;
                }
                if (TextUtils.isEmpty(cnic)) {
                    editTextCNIC.setError("Enter CNIC!");
                    return;
                }

                if (!isFemaleCNIC(cnic)) {
                    editTextCNIC.setError("Invalid CNIC or you are not female, so not verified");
                    Toast.makeText(SignUpActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    editTextPassword.setError("Password too short, enter minimum 6 characters!");
                    return;
                }


                auth.createUserWithEmailAndPassword(email,password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()){
                                    sendEmailVerification();

                                }else {
                                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                        editTextEmail.setError("This email address is already in use.");
                                    } else {
                                        editTextEmail.setError("Authentication failed: " + task.getException().getMessage());
                                    }
                                }
                            }
                        });


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

    private void sendEmailVerification() {
        Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).sendEmailVerification()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(SignUpActivity.this, "Verification email sent to " + FirebaseAuth.getInstance().getCurrentUser().getEmail(), Toast.LENGTH_SHORT).show();
                            FirebaseAuth.getInstance().signOut();
                            finish();
                        }else {
                            editTextEmail.setError("Failed to send verification email.");
                        }
                    }
                });
    }
}
