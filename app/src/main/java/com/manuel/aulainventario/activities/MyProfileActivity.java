package com.manuel.aulainventario.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.manuel.aulainventario.R;
import com.manuel.aulainventario.providers.AuthProvider;
import com.manuel.aulainventario.providers.KinderProvider;
import com.manuel.aulainventario.providers.TeachersProvider;
import com.manuel.aulainventario.utils.RelativeTime;

public class MyProfileActivity extends AppCompatActivity {
    MaterialButton mButtonEditInfo;
    MaterialTextView mTextViewTeachername, mTextViewPhone, mTextViewKinder, mTextViewTurn, mTextViewKey, mTextViewAddress, mTextViewGrade, mTextViewGroup, mTextViewLastUpdate;
    AuthProvider mAuthProvider;
    TeachersProvider mTeachersProvider;
    KinderProvider mKinderProvider;
    ProgressDialog mProgressDialogGetting;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        setTitle("Mi información");
        mButtonEditInfo = findViewById(R.id.btnEditInfo);
        mTextViewTeachername = findViewById(R.id.textViewTeachernameProfile);
        mTextViewPhone = findViewById(R.id.textViewPhoneProfile);
        mTextViewKinder = findViewById(R.id.textViewKinderProfile);
        mTextViewTurn = findViewById(R.id.textViewTurnProfile);
        mTextViewKey = findViewById(R.id.textViewKeyProfile);
        mTextViewAddress = findViewById(R.id.textViewAddressProfile);
        mTextViewGrade = findViewById(R.id.textViewGradeProfile);
        mTextViewGroup = findViewById(R.id.textViewGroupProfile);
        mTextViewLastUpdate = findViewById(R.id.textViewLastUpdate);
        mTeachersProvider = new TeachersProvider();
        mAuthProvider = new AuthProvider();
        mKinderProvider = new KinderProvider();
        mProgressDialogGetting = new ProgressDialog(this);
        mProgressDialogGetting.setTitle("Obteniendo datos...");
        mProgressDialogGetting.setMessage("Por favor, espere un momento");
        mProgressDialogGetting.setCancelable(false);
        mProgressDialogGetting.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mButtonEditInfo.setOnClickListener(v -> {
            startActivity(new Intent(this, EditInfoActivity.class));
            finish();
        });
        getTeacher();
    }

    @SuppressLint("SetTextI18n")
    private void getTeacher() {
        mProgressDialogGetting.show();
        mTeachersProvider.getTeacher(mAuthProvider.getUid()).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("idKinder")) {
                    String idKinder = documentSnapshot.getString("idKinder");
                    if (!TextUtils.isEmpty(idKinder)) {
                        mKinderProvider.getKindergartens(idKinder).addOnSuccessListener(documentSnapshot2 -> {
                            if (documentSnapshot2.exists()) {
                                if (documentSnapshot2.contains("name")) {
                                    String name = documentSnapshot2.getString("name");
                                    mTextViewKinder.setText(name);
                                }
                                if (documentSnapshot2.contains("gardenKey")) {
                                    String gardenKey = documentSnapshot2.getString("gardenKey");
                                    mTextViewKey.setText(gardenKey);
                                }
                                if (documentSnapshot2.contains("address")) {
                                    String address = documentSnapshot2.getString("address");
                                    mTextViewAddress.setText(address);
                                }
                            }
                        });
                    }
                }
                if (documentSnapshot.contains("teachername")) {
                    String teachername = documentSnapshot.getString("teachername");
                    mTextViewTeachername.setText(teachername);
                }
                if (documentSnapshot.contains("phone")) {
                    String phone = documentSnapshot.getString("phone");
                    mTextViewPhone.setText(phone);
                }
                if (documentSnapshot.contains("kinder")) {
                    String kinder = documentSnapshot.getString("kinder");
                    mTextViewKinder.setText(kinder);
                }
                if (documentSnapshot.contains("turn")) {
                    String turn = documentSnapshot.getString("turn");
                    mTextViewTurn.setText(turn);
                }
                if (documentSnapshot.contains("grade")) {
                    String grade = documentSnapshot.getString("grade");
                    mTextViewGrade.setText(grade + "°");
                }
                if (documentSnapshot.contains("group")) {
                    String group = documentSnapshot.getString("group");
                    mTextViewGroup.setText("\"" + group + "\"");
                }
                if (documentSnapshot.contains("timestamp")) {
                    long timestamp = documentSnapshot.getLong("timestamp");
                    String time = RelativeTime.getTimeAgo(timestamp);
                    mTextViewLastUpdate.setText(time);
                }
            }
            mProgressDialogGetting.dismiss();
        });
    }
}