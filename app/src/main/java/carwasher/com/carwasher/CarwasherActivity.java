package carwasher.com.carwasher;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import carwasherRepo.fragments.CarwasherHomeFragment;
import carwasherRepo.fragments.ConsumerOrdersFragment;
import carwasherRepo.fragments.ConsumerProfileFragment;
import carwasherRepo.fragments.ConsumerSettingFragment;
import resources.SaveSharedPreference;

public class CarwasherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_carwasher);

        SaveSharedPreference.saveFirebaseTokenToserver(this);

        BottomNavigationView bottomNavigationView = (BottomNavigationView)
                findViewById(R.id.navigation_consumer);

        bottomNavigationView.setOnNavigationItemSelectedListener
                (new BottomNavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                        Fragment selectedFragment = null;
                        switch (item.getItemId()) {
                            case R.id.action_item1:
                                selectedFragment = CarwasherHomeFragment.newInstance();
                                break;
                            case R.id.action_item2:
                                selectedFragment = ConsumerOrdersFragment.newInstance();
                                break;
                            case R.id.action_item3:
                                selectedFragment = ConsumerProfileFragment.newInstance();
                                break;
                            case R.id.action_item4:
                                selectedFragment = ConsumerSettingFragment.newInstance();
                                break;
                        }
                        FragmentManager fragmentManager = getSupportFragmentManager();
                        FragmentTransaction transaction = fragmentManager.beginTransaction();
                        transaction.replace(R.id.frame_layout_consumer, selectedFragment);
                        fragmentManager.popBackStack();
                        transaction.commit();
                        return true;
                    }
                });

        //Manually displaying the first fragment - one time only
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout_consumer, CarwasherHomeFragment.newInstance());
        transaction.commit();

    }
}
