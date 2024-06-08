package com.example.sellerapp1;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.example.sellerapp1.databinding.ActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity implements OnFragmentInteractionListener {

    private ActivityMainBinding bindingVar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bindingVar = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(bindingVar.getRoot());
        replaceFragment(new HomeFragment());

        bindingVar.btmNav.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.home:
                    replaceFragment(new HomeFragment());
                    break;
                case R.id.products:
                    replaceFragment(new ProductsFragment());
                    break;
                case R.id.orders:
                    replaceFragment(new OrdersFragment());
                    break;
                case R.id.profile:
                    replaceFragment(new SettingsFragment());
                    break;
            }
            return true;
        });
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onFragmentInteraction(int itemId) {
        BottomNavigationView bottomNavigationView = findViewById(R.id.btmNav);
        bottomNavigationView.setSelectedItemId(itemId);
    }
}
