package com.manuel.aulainventario.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.manuel.aulainventario.R;
import com.manuel.aulainventario.adapters.FragmentAdapter;
import com.manuel.aulainventario.providers.AuthProvider;

public class HomeActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager2 viewPager2;
    FragmentAdapter fragmentAdapter;
    AuthProvider authProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        tabLayout = findViewById(R.id.tabLayout);
        viewPager2 = findViewById(R.id.viewPager);
        authProvider = new AuthProvider();
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentAdapter = new FragmentAdapter(fragmentManager, getLifecycle());
        viewPager2.setAdapter(fragmentAdapter);
        tabLayout.addTab(tabLayout.newTab().setText("Técnico Pedagógico"));
        tabLayout.addTab(tabLayout.newTab().setText("Material Didáctico"));
        tabLayout.addTab(tabLayout.newTab().setText("Material de Consumo"));
        tabLayout.addTab(tabLayout.newTab().setText("Activo Fijo"));
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.pop_up_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.itemLogout) {
            AlertDialog.Builder alert = new AlertDialog.Builder(HomeActivity.this);
            alert.setTitle("¿Está seguro que desea cerrar sesión?");
            alert.setIcon(R.drawable.ic_logout);
            alert.setCancelable(false);
            alert.setPositiveButton("Cerrar sesión", (dialog, which) -> logout());
            alert.setNegativeButton("Cancelar", (dialog, which) -> {
            });
            alert.show();
        } else if (item.getItemId() == R.id.itemAcercaDe) {
            startActivity(new Intent(HomeActivity.this, InfoActivity.class));
        } else if (item.getItemId() == R.id.itemContactanos) {
            startActivity(new Intent(HomeActivity.this, ContactMeActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        authProvider.logout();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}