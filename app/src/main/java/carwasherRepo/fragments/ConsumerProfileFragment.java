package carwasherRepo.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import bean.Carwasher;
import carwasher.com.carwasher.AddAddressActivity;
import carwasher.com.carwasher.R;
import resources.SaveSharedPreference;

/**
 * Created by Dell on 8/14/2019.
 */

public class ConsumerProfileFragment extends Fragment implements View.OnClickListener{

    TextView userName, emailID, phoneNo;
    Button btn_manage_address, btn_edit_consumer_details;

    public ConsumerProfileFragment(){}

    public static ConsumerProfileFragment newInstance() {
        ConsumerProfileFragment fragment;
        fragment = new ConsumerProfileFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Profile");

        View v = inflater.inflate(R.layout.carwasher_profile_fragment, container, false);

        userName = (TextView) v.findViewById (R.id.userName);
        emailID = (TextView) v.findViewById (R.id.emailID);
        phoneNo = (TextView) v.findViewById (R.id.phoneNo);
        btn_manage_address = (Button) v.findViewById(R.id.btn_manage_address);
        btn_edit_consumer_details = (Button) v.findViewById(R.id.btn_edit_consumer_details);

        Carwasher bean = SaveSharedPreference.getCarwasherFromGson(getContext());
        if(bean!=null){
            userName.setText(bean.getName());
            emailID.setText(bean.getEmail());
            phoneNo.setText(bean.getPhoneNumber());
        }else{
            userName.setText("Not Provided");
            emailID.setText("Not Provided");
            phoneNo.setText("Not Provided");
        }

        btn_manage_address.setOnClickListener(this);
        btn_edit_consumer_details.setOnClickListener(this);

        return v;
    }

    @Override
    public void onClick(View v) {
        Fragment fragment = null;
        switch (v.getId()) {
            case R.id.btn_manage_address:
                Intent carIntent = new Intent(getActivity(), AddAddressActivity.class);
                startActivity(carIntent);
                break;

            case R.id.btn_edit_consumer_details:
                fragment = EditDetailsFragment.newInstance();
                replaceFragment(fragment);
                break;
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout_consumer, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}
