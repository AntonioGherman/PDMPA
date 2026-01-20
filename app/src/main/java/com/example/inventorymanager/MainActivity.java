package com.example.inventorymanager;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        View topBar = findViewById(R.id.top_bar);

        TextView title = topBar.findViewById(R.id.tvTitle);
        TextView subtitle = topBar.findViewById(R.id.tvSubtitle);
        ImageButton btnAdd = topBar.findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(v -> {
            // acțiune globală (ex: adăugare produs)
        });

        BottomNavigationView bottomNav = findViewById(R.id.bottom_nav);

        NavHostFragment navHostFragment =
                (NavHostFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.nav_host_fragment);

        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(bottomNav, navController);
    }
}
