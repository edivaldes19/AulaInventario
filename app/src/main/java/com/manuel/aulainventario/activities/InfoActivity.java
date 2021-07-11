package com.manuel.aulainventario.activities;

import android.graphics.Paint;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textview.MaterialTextView;
import com.manuel.aulainventario.R;

public class InfoActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        MaterialTextView materialTextView = findViewById(R.id.textViewBack);
        materialTextView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
        materialTextView.setOnClickListener(v -> finish());
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}