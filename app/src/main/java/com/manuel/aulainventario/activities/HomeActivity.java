package com.manuel.aulainventario.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.manuel.aulainventario.R;
import com.manuel.aulainventario.adapters.FragmentAdapter;
import com.manuel.aulainventario.providers.AuthProvider;
import com.manuel.aulainventario.utils.ConnectionReceiver;

public class HomeActivity extends AppCompatActivity implements ConnectionReceiver.ReceiverListener {
    TabLayout mTabLayout;
    ViewPager2 mViewPager2;
    FragmentAdapter mFragmentAdapter;
    AuthProvider mAuthProvider;
    boolean isPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        mTabLayout = findViewById(R.id.tabLayout);
        mViewPager2 = findViewById(R.id.viewPager);
        mAuthProvider = new AuthProvider();
        FragmentManager fragmentManager = getSupportFragmentManager();
        mFragmentAdapter = new FragmentAdapter(fragmentManager, getLifecycle());
        mViewPager2.setAdapter(mFragmentAdapter);
        mTabLayout.addTab(mTabLayout.newTab().setText("Técnico Pedagógico"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Material Didáctico"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Material de Consumo"));
        mTabLayout.addTab(mTabLayout.newTab().setText("Activo Fijo"));
        mTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                mViewPager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
        mViewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                mTabLayout.selectTab(mTabLayout.getTabAt(position));
            }
        });
        checkConnection();
    }

    private void checkConnection() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(new ConnectionReceiver(), intentFilter);
        ConnectionReceiver.listener = this;
        ConnectivityManager manager = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isConnected = networkInfo != null && networkInfo.isConnectedOrConnecting();
        showSnackBar(isConnected);
    }

    private void showSnackBar(boolean isConnected) {
        if (!isConnected) {
            Toast.makeText(getApplicationContext(), "Error de red, verifique su conexión", Toast.LENGTH_LONG).show();
            finishAffinity();
        }
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
        } else if (item.getItemId() == R.id.itemAbout) {
            startActivity(new Intent(HomeActivity.this, InfoActivity.class));
        } else if (item.getItemId() == R.id.itemContactMe) {
            startActivity(new Intent(HomeActivity.this, ContactMeActivity.class));
        } else if (item.getItemId() == R.id.itemMyProfile) {
            startActivity(new Intent(HomeActivity.this, MyProfileActivity.class));
        } else if (item.getItemId() == R.id.itemMap) {
            startActivity(new Intent(HomeActivity.this, MapsActivity.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        mAuthProvider.logout();
        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onNetworkChange(boolean isConnected) {
        showSnackBar(isConnected);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkConnection();
    }

    @Override
    protected void onPause() {
        super.onPause();
        checkConnection();
    }

    @Override
    public void onBackPressed() {
        if (isPressed) {
            finishAffinity();
            System.exit(0);
        } else {
            Toast.makeText(getApplicationContext(), "Presione de nuevo para salir", Toast.LENGTH_SHORT).show();
            isPressed = true;
        }
        Runnable runnable = () -> isPressed = false;
        new Handler().postDelayed(runnable, 2500);
    }
}