package com.example.gridx03.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gridx03.R;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity  extends AppCompatActivity {

    private EditText Name;
    private EditText Password;
    private TextView Info;
    private Button Login;
    private int counter = 5;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Name = (EditText) findViewById(R.id.et_username);
        Password = (EditText) findViewById(R.id.et_password);
        Login = (Button) findViewById(R.id.button_signin);
        Login.setOnClickListener(view -> {
            if (!isEmpty(Name) && !isEmpty(Password)) {
                validate(Name.getText().toString(), Password.getText().toString());
            } else {
                Toast.makeText(getApplicationContext(), "Please enter full infromation", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void validate(String userName, String userPassword) {
        if ((userName.equals("Admin")) && (userPassword.equals("1234"))) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);


        } else {
            counter--;

            Info.setText("No of attempts remaining: " + counter);

            if (counter == 0) {
                Login.setEnabled(false);
            }
        }

    }

    private boolean isEmpty(EditText etText) {
        return etText.getText().toString().trim().length() <= 0;
    }
}
