package com.manuel.aulainventario.activities;

import static com.manuel.aulainventario.utils.MyTools.compareDataString;
import static com.manuel.aulainventario.utils.MyTools.compareTeachersInformation;
import static com.manuel.aulainventario.utils.MyTools.isEmailValid;
import static com.manuel.aulainventario.utils.MyTools.isTeacherInfoExist;
import static com.manuel.aulainventario.utils.MyTools.validateFieldsAsYouType;
import static com.manuel.aulainventario.utils.MyTools.validatePasswordFieldsAsYouType;

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
import com.google.android.material.imageview.ShapeableImageView;
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

public class RegisterActivity extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;
    ShapeableImageView mImageViewBack;
    TextInputEditText mTextInputTeachername, mTextInputEmailR, mTextInputPhone, mTextInputPasswordR, mTextInputConfirmPasswordR;
    MaterialTextView mTextViewKinderSelected, mTextViewTurnSelected, mTextViewGradeSelected, mTextViewGroupSelected;
    Spinner mSpinnerKinder, mSpinnerTurn, mSpinnerGrade, mSpinnerGroup;
    MaterialButton materialButtonRegister;
    AuthProvider mAuthProvider;
    TeachersProvider mTeachersProvider;
    KinderProvider mKinderProvider;
    CollectionsProvider mCollectionsProviderShifts, mCollectionsProviderGrades, mCollectionsProviderGroups;
    ProgressDialog mDialog;
    ArrayList<String> mTeachernameList, mPhoneList, mShiftsList, mGradesList, mGroupsList;
    List<Teacher> mTeacherList;
    String mIdKinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        coordinatorLayout = findViewById(R.id.coordinatorRegister);
        mImageViewBack = findViewById(R.id.imageViewBack);
        mTextInputTeachername = findViewById(R.id.textInputTeachernameR);
        mTextInputEmailR = findViewById(R.id.textInputEmailR);
        mTextInputPhone = findViewById(R.id.textInputPhoneR);
        mTextInputPasswordR = findViewById(R.id.textInputPasswordR);
        mTextInputConfirmPasswordR = findViewById(R.id.textInputConfirmPasswordR);
        mTextViewKinderSelected = findViewById(R.id.textViewKinderSelectedR);
        mTextViewTurnSelected = findViewById(R.id.textViewTurnSelectedR);
        mTextViewGradeSelected = findViewById(R.id.textViewGradeSelectedR);
        mTextViewGroupSelected = findViewById(R.id.textViewGroupSelectedR);
        mSpinnerKinder = findViewById(R.id.spinnerKinderRegister);
        mSpinnerTurn = findViewById(R.id.spinnerTurnRegister);
        mSpinnerGrade = findViewById(R.id.spinnerGradeRegister);
        mSpinnerGroup = findViewById(R.id.spinnerGroupRegister);
        materialButtonRegister = findViewById(R.id.btnRegister);
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
        mTeacherList = new ArrayList<>();
        mDialog = new ProgressDialog(this);
        mDialog.setTitle("Registrando...");
        mDialog.setMessage("Por favor, espere un momento");
        mDialog.setCancelable(false);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mCollectionsProviderShifts.getAllTheDocumentsInACollectionAndSetTheAdapter(coordinatorLayout, mShiftsList, "turn", mSpinnerTurn, "Turno: ", mTextViewTurnSelected, "Error al obtener los turnos");
        mCollectionsProviderGrades.getAllTheDocumentsInACollectionAndSetTheAdapter(coordinatorLayout, mGradesList, "grade", mSpinnerGrade, "Grado: ", mTextViewGradeSelected, "Error al obtener los grados");
        mCollectionsProviderGroups.getAllTheDocumentsInACollectionAndSetTheAdapter(coordinatorLayout, mGroupsList, "group", mSpinnerGroup, "Grupo: ", mTextViewGroupSelected, "Error al obtener los grupos");
        validateFieldsAsYouType(mTextInputTeachername, "El nombre y apellido es obligatorio");
        validateFieldsAsYouType(mTextInputEmailR, "El correo electrónico es obligatorio");
        validateFieldsAsYouType(mTextInputPhone, "El número de teléfono es obligatorio");
        validatePasswordFieldsAsYouType(mTextInputPasswordR, "La contraseña es obligatoria");
        validatePasswordFieldsAsYouType(mTextInputConfirmPasswordR, "Debe confirmar su contraseña");
        isTeacherInfoExist(coordinatorLayout, mTeachersProvider, mTeachernameList, "teachername");
        isTeacherInfoExist(coordinatorLayout, mTeachersProvider, mPhoneList, "phone");
        getAllKindergartens();
        mImageViewBack.setOnClickListener(v -> finish());
        materialButtonRegister.setOnClickListener(v -> normalRegister());
    }

    private void normalRegister() {
        String teachername = Objects.requireNonNull(mTextInputTeachername.getText()).toString().trim();
        String email = Objects.requireNonNull(mTextInputEmailR.getText()).toString().trim();
        String phone = Objects.requireNonNull(mTextInputPhone.getText()).toString().trim();
        String kinder = mSpinnerKinder.getSelectedItem().toString().trim();
        String turn = mSpinnerTurn.getSelectedItem().toString().trim();
        String grade = mSpinnerGrade.getSelectedItem().toString().trim();
        String group = mSpinnerGroup.getSelectedItem().toString().trim();
        String password = Objects.requireNonNull(mTextInputPasswordR.getText()).toString().trim();
        String confirmPassword = Objects.requireNonNull(mTextInputConfirmPasswordR.getText()).toString().trim();
        if (!TextUtils.isEmpty(teachername)) {
            if (!TextUtils.isEmpty(email)) {
                if (!TextUtils.isEmpty(phone)) {
                    if (!TextUtils.isEmpty(kinder)) {
                        if (!TextUtils.isEmpty(turn)) {
                            if (!TextUtils.isEmpty(grade)) {
                                if (!TextUtils.isEmpty(group)) {
                                    if (!TextUtils.isEmpty(password)) {
                                        if (!TextUtils.isEmpty(confirmPassword)) {
                                            compareDataString(mTeachernameList, teachername, coordinatorLayout, "Ya existe un docente con ese nombre");
                                            compareDataString(mPhoneList, phone, coordinatorLayout, "Ya existe un docente con ese teléfono");
                                            compareTeachersInformation(mTeachersProvider, coordinatorLayout, mTeacherList);
                                            if (mTeacherList != null && !mTeacherList.isEmpty()) {
                                                for (int i = 0; i < mTeacherList.size(); i++) {
                                                    if ((mTeacherList.get(i).getGrade().equals(grade)) && (mTeacherList.get(i).getGroup().equals(group)) && (mTeacherList.get(i).getTurn().equals(turn))) {
                                                        Snackbar.make(coordinatorLayout, "Ya existe un docente ocupando ese grupo", Snackbar.LENGTH_SHORT).show();
                                                        return;
                                                    }
                                                }
                                            }
                                            if (isEmailValid(email)) {
                                                if (password.equals(confirmPassword)) {
                                                    if (password.length() >= 6) {
                                                        createTeacher(email, teachername, phone, turn, grade, group, password);
                                                    } else {
                                                        Snackbar.make(coordinatorLayout, "La contraseña debe ser mayor o igual a 6 caracteres", Snackbar.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Snackbar.make(coordinatorLayout, "Las contraseñas no coinciden", Snackbar.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Snackbar.make(coordinatorLayout, "Formato de correo electrónico inválido", Snackbar.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Snackbar.make(coordinatorLayout, "Debe confirmar su contraseña", Snackbar.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Snackbar.make(coordinatorLayout, "La contraseña es obligatoria", Snackbar.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Snackbar.make(coordinatorLayout, "Debe seleccionar un grupo", Snackbar.LENGTH_SHORT).show();
                                }
                            } else {
                                Snackbar.make(coordinatorLayout, "Debe seleccionar un grado", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Snackbar.make(coordinatorLayout, "Debe seleccionar un turno", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(coordinatorLayout, "Debe seleccionar un jardín de niños", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(coordinatorLayout, "El número de teléfono es obligatorio", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(coordinatorLayout, "El correo electrónico es obligatorio", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            Snackbar.make(coordinatorLayout, "El nombre y apellido es obligatorio", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void getAllKindergartens() {
        List<Kinder> kinderList = new ArrayList<>();
        mKinderProvider.getAllDocuments().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                    if (snapshot.exists()) {
                        if (snapshot.contains("id") && snapshot.contains("name")) {
                            String ids = snapshot.getString("id");
                            String names = snapshot.getString("name");
                            kinderList.add(new Kinder(ids, names, null, null, null));
                        }
                    }
                }
                ArrayAdapter<Kinder> arrayAdapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_dropdown_item_1line, kinderList);
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

    private void createTeacher(String email, String teachername, String phone, String turn, String grade, String group, String password) {
        mDialog.show();
        mAuthProvider.register(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String id = mAuthProvider.getUid();
                Teacher teacher = new Teacher();
                teacher.setId(id);
                teacher.setIdKinder(mIdKinder);
                teacher.setEmail(email);
                teacher.setTeachername(teachername);
                teacher.setPhone(phone);
                teacher.setTurn(turn);
                teacher.setGrade(grade);
                teacher.setGroup(group);
                teacher.setTimestamp(new Date().getTime());
                mTeachersProvider.create(teacher).addOnCompleteListener(task1 -> {
                    mDialog.dismiss();
                    if (task1.isSuccessful()) {
                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        Toast.makeText(RegisterActivity.this, "Bienvenido(a) " + teachername, Toast.LENGTH_LONG).show();
                    } else {
                        Snackbar.make(coordinatorLayout, "Error al registrar el docente en la base de datos", Snackbar.LENGTH_SHORT).show();
                    }
                });
            } else {
                mDialog.dismiss();
                Snackbar.make(coordinatorLayout, "Ya existe un docente con ese correo electrónico", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}