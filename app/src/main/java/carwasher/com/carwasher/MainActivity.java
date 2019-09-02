package carwasher.com.carwasher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;

import java.util.Map;

import bean.Carwasher;
import resources.CarConstant;
import resources.RestClient;
import resources.RestInvokerService;
import resources.SaveSharedPreference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    Button loginButton;
    private TextView signUpText;
    private EditText emailText;
    private EditText passwordText;
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseApp.initializeApp(this);
        progressDialog =  CarConstant.getProgressDialog(MainActivity.this,"Logging in...");

        //  Log.d("tokens: ", SaveSharedPreference.getFirebaseToken(this));

        emailText = (EditText) findViewById(R.id.input_email);
        passwordText = (EditText) findViewById(R.id.input_password);
        if(SaveSharedPreference.getIsUserLoggedIn(MainActivity.this)){
            Intent intent = new Intent(getApplicationContext(),CarwasherActivity.class);
            startActivity(intent);
            finish();
        } else {
            loginButton = (Button) findViewById(R.id.btn_login);
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doLogin();
                }
            });

            signUpText = (TextView) findViewById(R.id.link_signup);
            signUpText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                    startActivity(intent);
                }
            });
        }
    }

    public void doLogin(){
        if(!validate()){
            return;
        }


        progressDialog.show();
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        authLogin(email, password);
    }

    private void authLogin(String email, String password){
        try{
            RestInvokerService restInvokerService = RestClient.getClient().create(RestInvokerService.class);
            Carwasher carwasher = new Carwasher();carwasher.setEmail(email);carwasher.setPassword(password);
            Call<Map<String, Object>> call = restInvokerService.loginCarwasher(carwasher);
            call.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    Log.d("response:",response.body().toString());
                    Map<String, Object> map = response.body();
                    if( map.get("resCode").equals(200.0)){
                        String ss = map.get("data").toString();
                        SaveSharedPreference.setCarwasherObj(MainActivity.this, ss);
                        SaveSharedPreference.setIsUserLoggedIn(MainActivity.this);
                        progressDialog.dismiss();
                        Intent intent = new Intent(getApplicationContext(), CarwasherActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        progressDialog.dismiss();
                        authFailed();
                        return;
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    progressDialog.dismiss();
                    authFailed();
                    return;
                }
            });

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void authFailed(){
        Toast.makeText(getBaseContext(), "Please Enter Valid UserName or Password", Toast.LENGTH_LONG).show();
    }
    public boolean validate() {
        boolean valid = true;
        String email = emailText.getText().toString();
        String password = passwordText.getText().toString();
        if (TextUtils.isEmpty(email) || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailText.setError("please enter a valid email address");
            valid = false;
            return valid;
        } else {
            emailText.setError(null);
        }

        if (TextUtils.isEmpty(password) ) {
            passwordText.setError("please enter your password");
            valid = false;
            return valid;
        } else {
            passwordText.setError(null);
        }
        return valid;
    }
}
