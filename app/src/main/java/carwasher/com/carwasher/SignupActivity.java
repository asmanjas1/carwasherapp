package carwasher.com.carwasher;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Map;

import bean.Carwasher;
import butterknife.ButterKnife;
import butterknife.InjectView;
import resources.CarConstant;
import resources.RestClient;
import resources.RestInvokerService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignupActivity extends AppCompatActivity {

    @InjectView(R.id.input_name) EditText _nameText;
    @InjectView(R.id.input_email) EditText _emailText;
    @InjectView(R.id.input_password) EditText _passwordText;
    @InjectView(R.id.input_confirmPassword) EditText _passwordConfirmText;
    @InjectView(R.id.input_phoneNumber) EditText _phoneNumber;
    @InjectView(R.id.btn_signup) Button _signupButton;
    @InjectView(R.id.link_login) TextView _loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.inject(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
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

    public void signup() {
        if (!validate()) {
            return;
        }

        final ProgressDialog progressDialog =  CarConstant.getProgressDialog(this,"Creating Account");
        progressDialog.show();

        String name = _nameText.getText().toString();
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        String phoneNumber = _phoneNumber.getText().toString();
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
                response.body();
                _loginLink.setText(response.toString());
                Toast.makeText(getApplicationContext(),response.body().toString(),Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                onSignupSuccess();
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                progressDialog.dismiss();
                onSignupFailed();
               // Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void onSignupSuccess() {
        setResult(RESULT_OK, null);
        finish();
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
        String phoneNumber = _phoneNumber.getText().toString();

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

        if (phoneNumber.isEmpty() || !(phoneNumber.length() == 10)) {
            _phoneNumber.setError("enter a valid contact number");
            valid = false;
        } else {
            _phoneNumber.setError(null);
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
