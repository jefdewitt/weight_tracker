package com.zybooks.cs360appjefdewitt;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    Intent intent;
    private GridFragment gridFragment;
    private GoalFragment goalFragment;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the Intent that started this activity and extract the string
        Intent intentFromSignIn = getIntent();
        String message = intentFromSignIn.getStringExtra(SignInActivity.EXTRA_MESSAGE);

        gridFragment = GridFragment.newInstance(message);
        goalFragment = GoalFragment.newInstance(message);

        // Get a ref to bottom nav view element in main layout
        bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        // Set grid icon to selected when main activity is first started
        bottomNav.setSelectedItemId(R.id.nav_grid);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                gridFragment).commit();
    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;

                    switch (item.getItemId()) {
                        case R.id.nav_home:
                            startActivity(intent);
                            break;
                        case R.id.nav_grid:
                            selectedFragment = gridFragment;
                            break;
                        case R.id.nav_notifications:
                            selectedFragment = new NotificationsFragment();
                            break;
                        case R.id.nav_goal:
                            selectedFragment = goalFragment;
                            break;
                        default:
                            throw new IllegalStateException("Unexpected nav value: " + item.getItemId());
                    }

                    if (selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                                selectedFragment).commit();
                    }

                    return true;
                }
            };

}