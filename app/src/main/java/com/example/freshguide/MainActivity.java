package com.example.freshguide;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        setupGreeting();
        setupBottomNavigation();
        setupQuickActionCards();
    }

    private void setupGreeting() {
        TextView tvGreeting = findViewById(R.id.tv_greeting);
        TextView tvDate = findViewById(R.id.tv_date);

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        String timeGreeting;
        if (hour < 12) {
            timeGreeting = getString(R.string.greeting_morning);
        } else if (hour < 17) {
            timeGreeting = getString(R.string.greeting_afternoon);
        } else {
            timeGreeting = getString(R.string.greeting_evening);
        }

        tvGreeting.setText(timeGreeting + ", Freshman!");
        String date = new SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(new Date());
        tvDate.setText(date);
    }

    private void setupBottomNavigation() {
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setSelectedItemId(R.id.nav_home);
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                return true;
            } else if (id == R.id.nav_schedule) {
                Toast.makeText(this, "Schedule — coming soon", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_settings) {
                Toast.makeText(this, "Settings — coming soon", Toast.LENGTH_SHORT).show();
                return true;
            } else if (id == R.id.nav_profile) {
                Toast.makeText(this, "Profile — coming soon", Toast.LENGTH_SHORT).show();
                return true;
            }
            return false;
        });
    }

    private void setupQuickActionCards() {
        MaterialCardView cardFindRoom = findViewById(R.id.card_find_room);
        MaterialCardView cardSchedule = findViewById(R.id.card_schedule);
        MaterialCardView cardDepartment = findViewById(R.id.card_department);
        MaterialCardView cardEmergency = findViewById(R.id.card_emergency);

        cardFindRoom.setOnClickListener(v ->
                Toast.makeText(this, "Find a Room — coming soon", Toast.LENGTH_SHORT).show());
        cardSchedule.setOnClickListener(v ->
                Toast.makeText(this, "My Schedule — coming soon", Toast.LENGTH_SHORT).show());
        cardDepartment.setOnClickListener(v ->
                Toast.makeText(this, "Department Directory — coming soon", Toast.LENGTH_SHORT).show());
        cardEmergency.setOnClickListener(v ->
                Toast.makeText(this, "Emergency Exit — coming soon", Toast.LENGTH_SHORT).show());
    }
}
