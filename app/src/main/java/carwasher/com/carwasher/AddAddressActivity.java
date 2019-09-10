package carwasher.com.carwasher;

import android.*;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import bean.Carwasher;
import bean.CarwasherAddress;
import butterknife.ButterKnife;
import butterknife.InjectView;
import resources.CarConstant;
import resources.GPSTracker;
import resources.RestClient;
import resources.RestInvokerService;
import resources.SaveSharedPreference;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddAddressActivity extends AppCompatActivity {

    @InjectView(R.id.address_addressLine)
    EditText addressLine;
    @InjectView(R.id.address_locality) EditText addressLocality;
    @InjectView(R.id.address_landmark) EditText addressLandmark;
    @InjectView(R.id.address_city) EditText addressCity;
    @InjectView(R.id.address_state) EditText addressState;
    @InjectView(R.id.address_pincode) EditText addressPincode;
    @InjectView(R.id.btnGPSShowLocation)
    Button btnGPS;
    @InjectView(R.id.btn_add_address) Button btnAddAddress;

    TextView textViewAddressLine,textViewAddressLocalityLandmark,textViewAddressTotal,textViewAdressStatus,textViewCurrentAdd;
    CardView currentAddressCardView;

    GPSTracker gps;
    private static final int REQUEST_CODE_PERMISSION = 2;
    String mPermission = android.Manifest.permission.ACCESS_FINE_LOCATION;
    public ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_address);
        ButterKnife.inject(this);
        setGPSTrackerValues();
        textViewAddressLine = (TextView) findViewById(R.id.textViewAddressLine);
        textViewAddressLocalityLandmark = (TextView) findViewById(R.id.textViewAddressLocalityLandmark);
        textViewAddressTotal = (TextView) findViewById(R.id.textViewAddressTotal);
        textViewAdressStatus = (TextView) findViewById(R.id.textViewAdressStatus);
        textViewCurrentAdd = (TextView) findViewById(R.id.textViewCurrentAdd);
        currentAddressCardView = (CardView) findViewById(R.id.currentAddressCardView);

        Carwasher carwasher =  SaveSharedPreference.getCarwasherFromGson(this);
        CarwasherAddress address = carwasher.getCarwasherAddress();
        if(address != null){
            textViewAddressLine.setText(address.getAddressLine());
            textViewAddressLocalityLandmark.setText(address.getLocality());
            textViewAddressTotal.setText(address.getCity()+" "+address.getState()+" "+address.getPincode());
        } else {
            textViewAdressStatus.setVisibility(View.GONE);
            currentAddressCardView.setVisibility(View.GONE);
            textViewCurrentAdd.setText("Please add address to use our service!");
        }
        btnAddAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveAddress();
            }
        });
    }

    public void saveAddress () {
        if( !validate()){
            return;
        }
        progressDialog = CarConstant.getProgressDialog(this,"Adding Address...");
        progressDialog.show();

        CarwasherAddress carwasherAddress = new CarwasherAddress();
        carwasherAddress.setPincode(Integer.valueOf(addressPincode.getText().toString()));
        //String landmark = addressLandmark.getText().toString();
        String addressLine1 = addressLine.getText().toString();
        carwasherAddress.setAddressLine( addressLine1 );
        carwasherAddress.setLocality(addressLocality.getText().toString());
        carwasherAddress.setState(addressState.getText().toString());
        carwasherAddress.setCity(addressCity.getText().toString());
        Carwasher carwasher = new Carwasher();
        carwasher.setCarwasherId(SaveSharedPreference.getCarwasherFromGson(this).getCarwasherId());
        carwasherAddress.setCarwasher(carwasher);

        RestInvokerService restInvokerService = RestClient.getClient().create(RestInvokerService.class);
        Call<Map<String, Object>> call = restInvokerService.addAddress(carwasherAddress);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                Map<String, Object> map = response.body();
                if( map.get("resCode").equals(200.0)){
                    progressDialog.dismiss();
                    String ss = map.get("data").toString();
                    SaveSharedPreference.setCarwasherObj(AddAddressActivity.this, ss);
                    Toast.makeText(getBaseContext(), "Address Added Successfully.", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(getBaseContext(), "Error adding in vehicle.", Toast.LENGTH_LONG).show();
                    return;
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                progressDialog.dismiss();
                return;
            }
        });
    }

    public boolean validate() {
        boolean valid = true;

        String addressLine1 = addressLine.getText().toString();
        String addressLocality1 = addressLocality.getText().toString();
        String addressCity1 = addressCity.getText().toString();
        String addressState1 = addressState.getText().toString();
        String addressPincode1 = addressPincode.getText().toString();

        if (addressLine1.isEmpty()) {
            addressLine.setError("please enter value.");
            valid = false;
        } else {
            addressLine.setError(null);
        }

        if (addressLocality1.isEmpty()) {
            addressLocality.setError("please enter locality.");
            valid = false;
        } else {
            addressLocality.setError(null);
        }

        if (addressCity1.isEmpty()) {
            addressCity.setError("please enter city.");
            valid = false;
        } else {
            addressCity.setError(null);
        }

        if (addressState1.isEmpty()) {
            addressState.setError("please enter state.");
            valid = false;
        } else {
            addressState.setError(null);
        }

        if (addressPincode1.isEmpty()) {
            addressPincode.setError("please enter pincode.");
            valid = false;
        } else {
            addressPincode.setError(null);
        }
        return valid;
    }

    public void setGPSTrackerValues() {
        btnGPS = (Button) findViewById(R.id.btnGPSShowLocation);

        btnGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLocationPermission();
                // create class object
                gps = new GPSTracker(getBaseContext());
                // check if GPS enabled
                if(gps.canGetLocation()){
                    double latitude = gps.getLatitude();
                    double longitude = gps.getLongitude();
                    if(latitude==0.0 || longitude==0.0){
                        showSettingsAlert();
                    }else{
                        String aa = hereLocation(latitude,longitude);
                        // \n is for new line
                        Toast.makeText(getApplicationContext(), "Your Location is: "+ aa , Toast.LENGTH_LONG).show();
                    }
                }else{
                    // can't get location
                    // GPS or Network is not enabled
                    // Ask user to enable GPS/network in settings
                    gps.showSettingsAlert();
                }
            }
        });
    }

    public void setLocationPermission() {
        try {
            if (ActivityCompat.checkSelfPermission(this, mPermission)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{mPermission},
                        REQUEST_CODE_PERMISSION);
                // If any permission above not allowed by user, this condition will execute every time, else your else part will work
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }

    public String hereLocation(double let, double lon){

        List<String> list= new ArrayList<>();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        List<Address> addressList ;
        try{
            addressList = geocoder.getFromLocation(let,lon,1);
            if(addressList.size()>0){
                Address ad= addressList.get(0);
                addressPincode.setText(ad.getPostalCode());
                addressCity.setText(ad.getLocality());
                addressState.setText(ad.getAdminArea());
                String locality = ad.getAddressLine(0);
                String locality1 = locality.substring(0,locality.indexOf(ad.getLocality())-2);
                addressLocality.setText(locality1);
                list.add(ad.getLocality());
                list.add(ad.getCountryCode());
                list.add(ad.getAdminArea());
                list.add(ad.getPostalCode());
                list.add(ad.getAddressLine(0));
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return list.toString();
    }
}
