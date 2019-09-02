package carwasherRepo.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import carwasher.com.carwasher.MainActivity;
import carwasher.com.carwasher.R;
import resources.SaveSharedPreference;

/**
 * Created by Dell on 8/14/2019.
 */

public class ConsumerSettingFragment extends Fragment {

    private static final String CONTACT_EMAIL = "asmanjaskumar@gmail.com";
    private static final String CONTACT_PHONE = "9742236575";
    Button logOutButton,btn_Call, btn_Email;

    public static ConsumerSettingFragment newInstance() {
        ConsumerSettingFragment fragment;
        fragment = new ConsumerSettingFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ( getActivity()).setTitle("Settings & More");


        View v = inflater.inflate(R.layout.carwahser_setting_fragment, container, false);
        logOutButton= (Button) v.findViewById(R.id.btn_logout);
        btn_Call= (Button) v.findViewById(R.id.btn_contact_us_call);
        btn_Email= (Button) v.findViewById(R.id.btn_contact_us_email);

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SaveSharedPreference.logOut(getContext());
                Intent intent = new Intent(getContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
            }
        });
        btn_Email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("message/rfc822");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{CONTACT_EMAIL} );
                startActivity(Intent.createChooser(intent, "Send Email"));
            }
        });

        btn_Call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+CONTACT_PHONE));
                startActivity(intent);
            }
        });
        return v;
    }
}
