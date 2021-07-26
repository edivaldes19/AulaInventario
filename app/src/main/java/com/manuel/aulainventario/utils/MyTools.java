package com.manuel.aulainventario.utils;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.Spinner;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.manuel.aulainventario.models.Teacher;
import com.manuel.aulainventario.providers.TeachersProvider;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MyTools {
    public static boolean isEmailValid(String email) {
        String expression = "^[\\w.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static void validateFieldsAsYouType(TextInputEditText textInputEditText, String error) {
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(Objects.requireNonNull(textInputEditText.getText()).toString())) {
                    textInputEditText.setError(error);
                } else {
                    textInputEditText.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public static void validatePasswordFieldsAsYouType(TextInputEditText textInputEditText, String error) {
        textInputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (TextUtils.isEmpty(Objects.requireNonNull(textInputEditText.getText()).toString())) {
                    textInputEditText.setError(error, null);
                } else {
                    textInputEditText.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public static void calculateTotal(TextInputEditText multiplying, TextInputEditText multiplier, TextInputEditText result) {
        multiplying.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    double amount = Double.parseDouble(Objects.requireNonNull(multiplier.getText()).toString());
                    double price = Double.parseDouble(s.toString());
                    double total = amount * price;
                    DecimalFormat decimalFormat = new DecimalFormat("#.##");
                    result.setText(decimalFormat.format(total));
                } catch (NumberFormatException ignored) {
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    public static int getLastPositionOfASpinner(Spinner spinner, String s) {
        int position = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(s)) {
                position = i;
            }
        }
        return position;
    }

    public static void compareTeachersInformation(TeachersProvider teachersProvider, CoordinatorLayout coordinatorLayout, List<Teacher> teacherList) {
        teachersProvider.getAllTeacherDocuments().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                    if (snapshot.exists()) {
                        if (snapshot.contains("grade") && snapshot.contains("group") && snapshot.contains("turn")) {
                            String allGrades = snapshot.getString("grade");
                            String allGroups = snapshot.getString("group");
                            String allShifts = snapshot.getString("turn");
                            teacherList.add(new Teacher(null, null, null, null, null, allShifts, allGrades, allGroups, 0));
                        }
                    }
                }
            } else {
                Snackbar.make(coordinatorLayout, "Error al obtener la información de los docentes", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}