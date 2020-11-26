package co.edu.eci.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {

    public static final String TOKEN = "TOKEN";
    private AuthService authSe;
    private final ExecutorService executorService = Executors.newFixedThreadPool(1);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://10.0.2.2:8080/") //localhost for emulator
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        authSe = retrofit.create(AuthService.class);
    }

    public void onClickLogin(View view){

        EditText editUser = (EditText) findViewById(R.id.editUser);
        String user = editUser.getText().toString();
        EditText editPassword = (EditText) findViewById(R.id.editTextTextPassword);
        String password = editPassword.getText().toString();
        Intent intent = new Intent(this, MainActivity.class);
        System.out.println("" + user + " Llega al boton del login "+ password );
        if (user.equalsIgnoreCase("") || password.equalsIgnoreCase("") ) {
            editUser.setError("Ingresa tu usuario o  password ");
        }
        if (!user.matches("") && !password.matches("")) {
            LoginWrapper logUser = new LoginWrapper();
            logUser.setEmail(user);
            logUser.setPassword(password);

            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        System.out.println(logUser.getEmail());
                        Response<Token> token = authSe.login(logUser).execute();
                        System.out.println(token.body().getAccessToken());
                        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString(TOKEN, token.body().getAccessToken());
                        editor.commit();
                        startActivity(intent);
                        finish();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            });
        }
    }
}