package carwasher.com.carwasher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
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

public class SignupActivity extends AppCompatActivity {

    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.input_confirmPassword) EditText _passwordConfirmText;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;
    @InjectView(R.id.textViewDetailsMsg) TextView textViewDetailsMsg;

    @InjectView(R.id.input_phoneNumberOtp) EditText input_phoneNumberOtp;
    @InjectView(R.id.input_otp) EditText input_otp;
    @InjectView(R.id.btn_sendOtp) Button btn_sendOtp;
    @InjectView(R.id.btn_verifyOtp) Button btn_verifyOtp;

    Gson gson = new Gson();
    String phoneNumber, otp;
    FirebaseAuth auth;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;
    private String verificationCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        StartFirebaseLogin();

        _nameText.setVisibility(View.GONE);
        _emailText.setVisibility(View.GONE);
        _passwordText.setVisibility(View.GONE);
        _passwordConfirmText.setVisibility(View.GONE);
        _signupButton.setVisibility(View.GONE);
        textViewDetailsMsg.setVisibility(View.GONE);


        btn_sendOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                phoneNumber = input_phoneNumberOtp.getText().toString();
                if(phoneNumber != null){
                    btn_sendOtp.setVisibility(View.GONE);
                    checkForExistingPhoneNumber(phoneNumber);
                }
            }
        });

        btn_verifyOtp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp = input_otp.getText().toString();
                if(otp != null){
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationCode, otp);
                    SigninWithPhone(credential);
                }

            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(i);
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    private void StartFirebaseLogin() {

        auth = FirebaseAuth.getInstance();
        mCallback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                btn_sendOtp.setVisibility(View.VISIBLE);
                setSignUpView();
                Toast.makeText(SignupActivity.this,"verification completed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                btn_sendOtp.setVisibility(View.VISIBLE);
                Toast.makeText(SignupActivity.this,"verification failed",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(String s, PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationCode = s;
                Toast.makeText(SignupActivity.this,"Code sent",Toast.LENGTH_SHORT).show();
                btn_sendOtp.setVisibility(View.VISIBLE);
            }
        };
    }

    private void SigninWithPhone(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            setSignUpView();
                            Toast.makeText(SignupActivity.this,"Correct OTP",Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(SignupActivity.this,"Incorrect OTP",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void setSignUpView(){
        input_phoneNumberOtp.setVisibility(View.GONE);
        input_otp.setVisibility(View.GONE);
        btn_sendOtp.setVisibility(View.GONE);
        btn_verifyOtp.setVisibility(View.GONE);

        _nameText.setVisibility(View.VISIBLE);
        _emailText.setVisibility(View.VISIBLE);
        _passwordText.setVisibility(View.VISIBLE);
        _passwordConfirmText.setVisibility(View.VISIBLE);
        _signupButton.setVisibility(View.VISIBLE);
        textViewDetailsMsg.setVisibility(View.VISIBLE);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });
    }

    public void signup() {
        if (!validate()) {
            return;
        }

        final ProgressDialog progressDialog =  CarConstant.getProgressDialog(this,"Creating Account");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        // TODO: Implement your own signup logic here.

        RestInvokerService restInvokerService = RestClient.getClient().create(RestInvokerService.class);
        Carwasher carwasher = new Carwasher();
        carwasher.setEmail(email);
        carwasher.setName(name);
        carwasher.setPhoneNumber(phoneNumber);
        carwasher.setPassword(password);
        Call<Map<String, Object>> call = restInvokerService.createCarwasher(carwasher);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Map<String, Object> map = response.body();
                if( map.get("resCode").equals(200.0)){
                    Toast.makeText(getApplicationContext(),"Success",Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    onSignupSuccess();
                } else {
                    progressDialog.dismiss();
                    onSignupFailed();
                }

            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                progressDialog.dismiss();
                onSignupFailed();
                // Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void checkForExistingPhoneNumber(String number){
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
                        sendOtp();
                    } else {
                        btn_sendOtp.setVisibility(View.VISIBLE);
                        input_phoneNumberOtp.setError("Phone number already exist, please use other number.");
                        return;
                    }
                } else {
                    btn_sendOtp.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(),"Some Error in creating account, try again after some time.",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                btn_sendOtp.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"Some Error in creating account, try again after some time.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void sendOtp(){
        phoneNumber = input_phoneNumberOtp.getText().toString();
        if(phoneNumber != null){
            PhoneAuthProvider.getInstance().verifyPhoneNumber(
                    phoneNumber,
                    70,
                    TimeUnit.SECONDS,
                    SignupActivity.this,
                    mCallback
            );
        }
    }

    public void onSignupSuccess() {
        phoneNumber = input_phoneNumberOtp.getText().toString();
        saveConsmerByPhone(phoneNumber);
    }

    public void saveConsmerByPhone(String number){
        RestInvokerService restInvokerService = RestClient.getClient().create(RestInvokerService.class);
        Call<Map<String, Object>> call = restInvokerService.doLoginByNumber(number);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Map<String, Object> map = response.body();
                if( map.get("resCode").equals(200.0)){
                    String ss = map.get("data").toString();
                    SaveSharedPreference.setCarwasherObj(SignupActivity.this, ss);
                    SaveSharedPreference.setIsUserLoggedIn(SignupActivity.this);
                    finishFirebaseAuth();
                    setResult(RESULT_OK, null);
                    Intent intent = new Intent(getApplicationContext(), CarwasherActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                btn_sendOtp.setVisibility(View.VISIBLE);
                Toast.makeText(getApplicationContext(),"Some Error in creating account, try again after some time.",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void finishFirebaseAuth(){
        auth.signOut();
    }

    public void onSignupFailed() {
        Toast.makeText(getBaseContext(), "Sign up failed", Toast.LENGTH_LONG).show();
    }

    public boolean validate() {
        boolean valid = true;

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String confirmPassword = _passwordConfirmText.getText().toString();

        if (name.isEmpty() || name.length() < 3) {
            _nameText.setError("please set a name of at least 3 characters");
            valid = false;
        } else {
            _nameText.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("please enter password between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        if (confirmPassword.isEmpty() || confirmPassword.length() < 4 || confirmPassword.length() > 10) {
            _passwordConfirmText.setError("please enter password between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordConfirmText.setError(null);
        }

        if( !password.equals(confirmPassword)){
            Toast.makeText(getBaseContext(), "Please set same password and confirm password.", Toast.LENGTH_LONG).show();
            valid = false;
        }

        return valid;
    }
}
