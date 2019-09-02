package carwasherRepo.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import carwasher.com.carwasher.R;


/**
 * Created by Dell on 8/21/2019.
 */

public class EditDetailsFragment extends Fragment {

    Button btnUpdateDetails, btnUpdatePassword;
    EditText newName, newEmail, newPhoneNumber, oldPswd, newPswd;

    public EditDetailsFragment() {
        // Required empty public constructor
    }

    public static EditDetailsFragment newInstance() {
        EditDetailsFragment fragment = new EditDetailsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.edit_profile_frragment, container, false);

        btnUpdateDetails = (Button) v.findViewById(R.id.btn_update_details);
        btnUpdatePassword = (Button) v.findViewById(R.id.btn_change_password);

        newName = (EditText) v.findViewById(R.id.nameUpdate);
        newEmail = (EditText) v.findViewById(R.id.emailUpdate);
        newPhoneNumber = (EditText) v.findViewById(R.id.phoneNoUpdate);
        oldPswd = (EditText) v.findViewById(R.id.currentPassword);
        newPswd = (EditText) v.findViewById(R.id.newPassword);

        btnUpdateDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDetails();
            }
        });

        btnUpdatePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePassword();
            }
        });

        return v;
    }

    public void updateDetails() {

    }

    public void updatePassword() {

    }
}
