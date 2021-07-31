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
import java.util.ArrayList;
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

    public static void isTeacherInfoExist(CoordinatorLayout coordinatorLayout, TeachersProvider teachersProvider, ArrayList<String> stringList, String field) {
        teachersProvider.getAllTeacherDocuments().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                    if (snapshot.exists()) {
                        if (snapshot.contains(field)) {
                            String allFields = snapshot.getString(field);
                            stringList.add(allFields);
                        }
                    }
                }
            } else {
                Snackbar.make(coordinatorLayout, "Error al obtener la información de los docentes", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public static void setPositionByCondition(Spinner spinner, String condition) {
        if (!TextUtils.isEmpty(condition)) {
            switch (condition) {
                case "Bueno":
                    spinner.setSelection(0);
                    break;
                case "Malo":
                    spinner.setSelection(1);
                    break;
                case "Regular":
                    spinner.setSelection(2);
                    break;
            }
        }
    }

    public static void setPositionByGrade(Spinner spinner, String grade) {
        if (!TextUtils.isEmpty(grade)) {
            switch (grade) {
                case "1":
                    spinner.setSelection(0);
                    break;
                case "2":
                    spinner.setSelection(1);
                    break;
                case "3":
                    spinner.setSelection(2);
                    break;
            }
        }
    }

    public static void setPositionByGroup(Spinner spinner, String group) {
        if (!TextUtils.isEmpty(group)) {
            switch (group) {
                case "A":
                    spinner.setSelection(0);
                    break;
                case "B":
                    spinner.setSelection(1);
                    break;
                case "C":
                    spinner.setSelection(2);
                    break;
                case "D":
                    spinner.setSelection(3);
                    break;
            }
        }
    }

    public static void setPositionByTurn(Spinner spinner, String turn) {
        if (!TextUtils.isEmpty(turn)) {
            switch (turn) {
                case "Matutino":
                    spinner.setSelection(0);
                    break;
                case "Vespertino":
                    spinner.setSelection(1);
                    break;
            }
        }
    }

    public static int setPositionByKindergarten(Spinner spinner, String kinder) {
        int position = 0;
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(kinder)) {
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

    public static void compareDataString(ArrayList<String> strings, String text, CoordinatorLayout coordinatorLayout, String error) {
        if (strings != null && !strings.isEmpty()) {
            for (String s : strings) {
                if (s.equals(text)) {
                    Snackbar.make(coordinatorLayout, error, Snackbar.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    public static void compareDataNumbers(ArrayList<Long> longs, long number, CoordinatorLayout coordinatorLayout, String error) {
        if (longs != null && !longs.isEmpty()) {
            for (Long l : longs) {
                if (l == number) {
                    Snackbar.make(coordinatorLayout, error, Snackbar.LENGTH_SHORT).show();
                    return;
                }
            }
        }
    }

    public static void deleteCurrentInformationString(ArrayList<String> strings, String text) {
        if (strings != null && !strings.isEmpty()) {
            for (int i = 0; i < strings.size(); i++) {
                if (strings.get(i).equals(text)) {
                    strings.remove(i);
                    break;
                }
            }
        }
    }

    public static void deleteCurrentInformationNumbers(ArrayList<Long> longs, long number) {
        if (longs != null && !longs.isEmpty()) {
            for (int i = 0; i < longs.size(); i++) {
                if (longs.get(i) == number) {
                    longs.remove(i);
                    break;
                }
            }
        }
    }
}