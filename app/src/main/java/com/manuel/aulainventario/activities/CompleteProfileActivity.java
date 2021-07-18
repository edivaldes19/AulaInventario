package com.manuel.aulainventario.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.manuel.aulainventario.R;
import com.manuel.aulainventario.models.Kinder;
import com.manuel.aulainventario.models.Teacher;
import com.manuel.aulainventario.providers.AuthProvider;
import com.manuel.aulainventario.providers.CollectionsProvider;
import com.manuel.aulainventario.providers.KinderProvider;
import com.manuel.aulainventario.providers.TeachersProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.manuel.aulainventario.utils.Validations.validateFieldsAsYouType;

public class CompleteProfileActivity extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;
    TextInputEditText mTextInputTeachername, mTextInputPhone;
    MaterialTextView mTextViewKinderSelected, mTextViewTurnSelected, mTextViewGradeSelected, mTextViewGroupSelected;
    Spinner mSpinnerKinder, mSpinnerTurn, mSpinnerGrade, mSpinnerGroup;
    MaterialButton materialButtonRegister;
    AuthProvider mAuthProvider;
    TeachersProvider mTeachersProvider;
    KinderProvider mKinderProvider;
    CollectionsProvider mCollectionsProviderShifts, mCollectionsProviderGrades, mCollectionsProviderGroups;
    ProgressDialog mDialog;
    ArrayList<String> mTeachernameList, mPhoneList, mShiftsList, mGradesList, mGroupsList;
    String mIdKinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);
        coordinatorLayout = findViewById(R.id.coordinatorComplete);
        mTextInputTeachername = findViewById(R.id.textInputTeachernameConfirm);
        mTextInputPhone = findViewById(R.id.textInputPhone);
        mTextViewKinderSelected = findViewById(R.id.textViewKinderSelectedC);
        mTextViewTurnSelected = findViewById(R.id.textViewTurnSelectedC);
        mTextViewGradeSelected = findViewById(R.id.textViewGradeSelectedC);
        mTextViewGroupSelected = findViewById(R.id.textViewGroupSelectedC);
        mSpinnerKinder = findViewById(R.id.spinnerKinderComplete);
        mSpinnerTurn = findViewById(R.id.spinnerTurnComplete);
        mSpinnerGrade = findViewById(R.id.spinnerGradeComplete);
        mSpinnerGroup = findViewById(R.id.spinnerGroupComplete);
        materialButtonRegister = findViewById(R.id.btnConfirm);
        mAuthProvider = new AuthProvider();
        mTeachersProvider = new TeachersProvider();
        mKinderProvider = new KinderProvider();
        mCollectionsProviderShifts = new CollectionsProvider(this, "Shifts");
        mCollectionsProviderGrades = new CollectionsProvider(this, "Grades");
        mCollectionsProviderGroups = new CollectionsProvider(this, "Groups");
        mTeachernameList = new ArrayList<>();
        mPhoneList = new ArrayList<>();
        mShiftsList = new ArrayList<>();
        mGradesList = new ArrayList<>();
        mGroupsList = new ArrayList<>();
        mDialog = new ProgressDialog(this);
        mDialog.setTitle("Registrando...");
        mDialog.setMessage("Por favor, espere un momento");
        mDialog.setCancelable(false);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        validateFieldsAsYouType(mTextInputTeachername, "El nombre y apellido es obligatorio");
        validateFieldsAsYouType(mTextInputPhone, "El número de teléfono es obligatorio");
        isUserInfoExist(mTeachernameList, "teachername");
        isUserInfoExist(mPhoneList, "phone");
        mCollectionsProviderShifts.getAllTheDocumentsInACollectionAndSetTheAdapter(coordinatorLayout, mShiftsList, "turn", mSpinnerTurn, "Turno: ", mTextViewTurnSelected, "Error al obtener los turnos");
        mCollectionsProviderGrades.getAllTheDocumentsInACollectionAndSetTheAdapter(coordinatorLayout, mGradesList, "grade", mSpinnerGrade, "Grado: ", mTextViewGradeSelected, "Error al obtener los grados");
        mCollectionsProviderGroups.getAllTheDocumentsInACollectionAndSetTheAdapter(coordinatorLayout, mGroupsList, "group", mSpinnerGroup, "Grupo: ", mTextViewGroupSelected, "Error al obtener los grupos");
        getAllKindergartens();
        materialButtonRegister.setOnClickListener(v -> {
            String teachername = Objects.requireNonNull(mTextInputTeachername.getText()).toString().trim();
            String phone = Objects.requireNonNull(mTextInputPhone.getText()).toString().trim();
            String kinder = mSpinnerKinder.getSelectedItem().toString().trim();
            String turn = mSpinnerTurn.getSelectedItem().toString().trim();
            String grade = mSpinnerGrade.getSelectedItem().toString().trim();
            String group = mSpinnerGroup.getSelectedItem().toString().trim();
            if (!TextUtils.isEmpty(teachername)) {
                if (!TextUtils.isEmpty(phone)) {
                    if (!TextUtils.isEmpty(kinder)) {
                        if (!TextUtils.isEmpty(turn)) {
                            if (!TextUtils.isEmpty(grade)) {
                                if (!TextUtils.isEmpty(group)) {
                                    if (mTeachernameList != null && !mTeachernameList.isEmpty()) {
                                        for (String s : mTeachernameList) {
                                            if (s.equals(teachername)) {
                                                Snackbar.make(v, "Ya existe un docente con ese nombre", Snackbar.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                    }
                                    if (mPhoneList != null && !mPhoneList.isEmpty()) {
                                        for (String s : mPhoneList) {
                                            if (s.equals(phone)) {
                                                Snackbar.make(v, "Ya existe un docente con ese teléfono", Snackbar.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                    }
                                    updateTeacher(teachername, phone, turn, grade, group);
                                } else {
                                    Snackbar.make(v, "Debe seleccionar un grupo", Snackbar.LENGTH_SHORT).show();
                                }
                            } else {
                                Snackbar.make(v, "Debe seleccionar un grado", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Snackbar.make(v, "Debe seleccionar un turno", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(v, "Debe seleccionar un jardín de niños", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(v, "El número de teléfono es obligatorio", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(v, "El nombre y apellido es obligatorio", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void isUserInfoExist(ArrayList<String> stringList, String field) {
        mTeachersProvider.getAllTeacherDocuments().addOnCompleteListener(task -> {
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

    private void getAllKindergartens() {
        List<Kinder> kinderList = new ArrayList<>();
        mKinderProvider.getAllDocuments().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                    if (snapshot.exists()) {
                        if (snapshot.contains("id") && snapshot.contains("name")) {
                            String ids = snapshot.getString("id");
                            String names = snapshot.getString("name");
                            kinderList.add(new Kinder(ids, names, null, null));
                        }
                    }
                }
                ArrayAdapter<Kinder> arrayAdapter = new ArrayAdapter<>(CompleteProfileActivity.this, android.R.layout.simple_dropdown_item_1line, kinderList);
                mSpinnerKinder.setAdapter(arrayAdapter);
                mSpinnerKinder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String kinder = parent.getItemAtPosition(position).toString().trim();
                        mIdKinder = kinderList.get(position).getId().trim();
                        mTextViewKinderSelected.setText("J/N " + kinder);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            } else {
                Snackbar.make(coordinatorLayout, "Error al obtener la información de los jardines de niños", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void updateTeacher(String teachername, String phone, String turn, String grade, String group) {
        String id = mAuthProvider.getUid();
        Teacher teacher = new Teacher();
        teacher.setId(id);
        teacher.setIdKinder(mIdKinder);
        teacher.setTeachername(teachername);
        teacher.setPhone(phone);
        teacher.setTurn(turn);
        teacher.setGrade(grade);
        teacher.setGroup(group);
        teacher.setTimestamp(new Date().getTime());
        mDialog.show();
        mTeachersProvider.update(teacher).addOnCompleteListener(task1 -> {
            mDialog.dismiss();
            if (task1.isSuccessful()) {
                startActivity(new Intent(CompleteProfileActivity.this, HomeActivity.class));
                finish();
                Toast.makeText(this, "Bienvenido(a) " + teachername, Toast.LENGTH_SHORT).show();
            } else {
                Snackbar.make(coordinatorLayout, "Error al registrar el docente en la base de datos", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}