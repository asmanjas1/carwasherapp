package carwasher.com.carwasher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.gson.Gson;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import bean.Carwasher;
import butterknife.ButterKnife;
import butterknife.InjectView;
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
    public  ProgressDialog progressDialog;

    @InjectView(R.id.input_phoneNumberOtp) EditText input_phoneNumberOtp;
    @InjectView(R.id.input_otp) EditText input_otp;
    @InjectView(R.id.btn_sendOtp) Button btn_sendOtp;
    @InjectView(R.id.btn_verifyOtp) Button btn_verifyOtp;
    @InjectView(R.id.link_useEmailForLogin) TextView link_useEmailForLogin;
    @InjectView(R.id.link_useOtpForLogin) TextView link_useOtpForLogin;

    Gson gson = new Gson();
    String phoneNumber, otp;
    FirebaseAuth auth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verificationCode;
    String consumerString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        FirebaseApp.initializeApp(this);
        progressDialog =  CarConstant.getProgressDialog(MainActivity.this,"Logging in...");

        emailText = (EditText) findViewById(R.id.input_email);
        passwordText = (EditText) findViewById(R.id.input_password);
        loginButton = (Button) findViewById(R.id.btn_login);
        signUpText = (TextView) findViewById(R.id.link_signup);
        if(SaveSharedPreference.getIsUserLoggedIn(MainActivity.this)){
            Intent intent = new Intent(getApplicationContext(),CarwasherActivity.class);
            startActivity(intent);
            finish();
        } else {

            emailText.setVisibility(View.GONE);
            passwordText.setVisibility(View.GONE);
            loginButton.setVisibility(View.GONE);
            link_useOtpForLogin.setVisibility(View.GONE);

            setUpLoginMethod();
            setUpOtpLogin();
            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    doLogin();
                }
            });
            signUpText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
        }
    }

    public void setUpLoginMethod(){
        link_useOtpForLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailText.setVisibility(View.GONE);
                passwordText.setVisibility(View.GONE);
                loginButton.setVisibility(View.GONE);

                input_phoneNumberOtp.setVisibility(View.VISIBLE);
                input_otp.setVisibility(View.VISIBLE);
                btn_sendOtp.setVisibility(View.VISIBLE);
                btn_verifyOtp.setVisibility(View.VISIBLE);
                link_useOtpForLogin.setVisibility(View.GONE);
                link_useEmailForLogin.setVisibility(View.VISIBLE);
            }
        });
        link_useEmailForLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                input_phoneNumberOtp.setVisibility(View.GONE);
                input_otp.setVisibility(View.GONE);
                btn_sendOtp.setVisibility(View.GONE);
                btn_verifyOtp.setVisibility(View.GONE);

                emailText.setVisibility(View.VISIBLE);
                passwordText.setVisibility(View.VISIBLE);
                loginButton.setVisibility(View.VISIBLE);
                link_useEmailForLogin.setVisibility(View.GONE);
                link_useOtpForLogin.setVisibility(View.VISIBLE);
            }
        });
    }

    public void setUpOtpLogin(){
        StartFirebaseLogin();
        btn_sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = input_phoneNumberOtp.getText().toString();

                if(phoneNumber != null){
                    btn_sendOtp.setVisibility(View.GONE);
                    logInwithPhoneNumber(phoneNumber);
                }
            }
        });

        btn_verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp = input_otp.getText().toString();
                if( otp != null){
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
                    SigninWithPhone(credential);
                }

            }
        });
    }

    public void logInwithPhoneNumber(String number){
        RestInvokerService restInvokerService = RestClient.getClient().create(RestInvokerService.class);
        Call<Map<String, Object>> call = restInvokerService.doLoginByNumber(number);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Map<String, Object> map = response.body();
                if( map.get("resCode").equals(200.0)){
                    String ss = map.get("data").toString();
                    Carwasher con = gson.fromJson(ss, Carwasher.class);
                    if(con.getCarwasherId() == 0){
                        btn_sendOtp.setVisibility(View.VISIBLE);
                        Toast.makeText(MainActivity.this,"The Phone number is not registered with us.",Toast.LENGTH_SHORT).show();
                    } else {
                        consumerString = ss;
                        sendFinalOtp();
                    }
                } else {
                    btn_sendOtp.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                btn_sendOtp.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"Some Error in creating account, try again after some time.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendFinalOtp(){
        phoneNumber = input_phoneNumberOtp.getText().toString();
        if(phoneNumber != null){
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,
                    70,
                    TimeUnit.SECONDS,
                    MainActivity.this,
                    mCallback
            );
        }

    }

    private void StartFirebaseLogin() {

        auth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                btn_sendOtp.setVisibility(View.VISIBLE);
                saveConsumerObjAndLogin();
                Toast.makeText(MainActivity.this,"verification completed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                btn_sendOtp.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this,"verification failed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                btn_sendOtp.setVisibility(View.VISIBLE);
                Toast.makeText(MainActivity.this,"Code sent",Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void SigninWithPhone(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,"Correct OTP",Toast.LENGTH_LONG).show();
                            saveConsumerObjAndLogin();
                        } else {
                            Toast.makeText(MainActivity.this,"Incorrect OTP",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

    public void saveConsumerObjAndLogin() {
        auth.signOut();
        SaveSharedPreference.setCarwasherObj(MainActivity.this, consumerString);
        SaveSharedPreference.setIsUserLoggedIn(MainActivity.this);
        Intent intent = new Intent(getApplicationContext(), CarwasherActivity.class);
        startActivity(intent);
        finish();
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
