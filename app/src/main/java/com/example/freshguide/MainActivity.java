package com.example.freshguide;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.example.freshguide.receiver.NetworkChangeReceiver;
import com.example.freshguide.util.SessionManager;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity implements NetworkChangeReceiver.NetworkListener {

    private NavController navController;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rootView = findViewById(R.id.main);

        NavHostFragment navHostFragment = (NavHostFragment) getSupportFragmentManager()
                .findFragmentById(R.id.nav_host_fragment);
        navController = navHostFragment.getNavController();

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        NavigationUI.setupWithNavController(bottomNav, navController);

        // Route to correct start destination based on role
        SessionManager session = SessionManager.getInstance(this);
        if (session.isAdmin()) {
            bottomNav.setVisibility(View.GONE); // Admin uses its own nav structure
            navController.navigate(R.id.adminDashboardFragment);
        }

        // Network change receiver (checklist 3.2)
        NetworkChangeReceiver.setListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        NetworkChangeReceiver.setListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        NetworkChangeReceiver.clearListener();
    }

    @Override
    public void onNetworkChanged(boolean isConnected) {
        if (!isConnected && rootView != null) {
            Snackbar.make(rootView, "No internet connection", Snackbar.LENGTH_LONG).show();
        }
    }

    // Checklist 4.1-4.3: Options menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SessionManager session = SessionManager.getInstance(this);
        menu.findItem(R.id.action_logout).setVisible(session.isLoggedIn());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            new com.example.freshguide.repository.AuthRepository(this).logout();
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.action_search) {
            return true;
        } else if (id == R.id.action_filter) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
