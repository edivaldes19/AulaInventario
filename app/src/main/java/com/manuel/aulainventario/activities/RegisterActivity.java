package com.manuel.aulainventario.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
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
import com.manuel.aulainventario.providers.KinderProvider;
import com.manuel.aulainventario.providers.TeachersProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.manuel.aulainventario.utils.Validations.isEmailValid;
import static com.manuel.aulainventario.utils.Validations.validateFieldsAsYouType;
import static com.manuel.aulainventario.utils.Validations.validatePasswordFieldsAsYouType;

public class RegisterActivity extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;
    ShapeableImageView mImageViewBack;
    TextInputEditText mTextInputUsername, mTextInputEmailR, mTextInputPhone, mTextInputPasswordR, mTextInputConfirmPasswordR;
    MaterialTextView mTextViewKinderSelected;
    Spinner mSpinner;
    MaterialButton materialButtonRegister;
    AuthProvider mAuthProvider;
    TeachersProvider mTeachersProvider;
    KinderProvider mKinderProvider;
    ProgressDialog mDialog;
    List<String> mUsernameList, mPhoneList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        coordinatorLayout = findViewById(R.id.coordinatorRegister);
        mImageViewBack = findViewById(R.id.imageViewBack);
        mTextInputUsername = findViewById(R.id.textInputUsernameR);
        mTextInputEmailR = findViewById(R.id.textInputEmailR);
        mTextInputPhone = findViewById(R.id.textInputPhoneR);
        mTextInputPasswordR = findViewById(R.id.textInputPasswordR);
        mTextInputConfirmPasswordR = findViewById(R.id.textInputConfirmPasswordR);
        mTextViewKinderSelected = findViewById(R.id.textViewKinderSelectedR);
        mSpinner = findViewById(R.id.spinnerKinderRegister);
        materialButtonRegister = findViewById(R.id.btnRegister);
        mAuthProvider = new AuthProvider();
        mTeachersProvider = new TeachersProvider();
        mKinderProvider = new KinderProvider();
        mUsernameList = new ArrayList<>();
        mPhoneList = new ArrayList<>();
        mDialog = new ProgressDialog(this);
        mDialog.setTitle("Registrando...");
        mDialog.setMessage("Por favor, espere un momento");
        mDialog.setCancelable(false);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        validateFieldsAsYouType(mTextInputUsername, "El nombre y apellido es obligatorio");
        validateFieldsAsYouType(mTextInputEmailR, "El correo electrónico es obligatorio");
        validateFieldsAsYouType(mTextInputPhone, "El número de teléfono es obligatorio");
        validatePasswordFieldsAsYouType(mTextInputPasswordR, "La contraseña es obligatoria");
        validatePasswordFieldsAsYouType(mTextInputConfirmPasswordR, "Debe confirmar su contraseña");
        mUsernameList.clear();
        isUserInfoExist(mUsernameList, "username");
        mPhoneList.clear();
        isUserInfoExist(mPhoneList, "phone");
        getAllKindergartens();
        materialButtonRegister.setOnClickListener(v -> {
            String username = Objects.requireNonNull(mTextInputUsername.getText()).toString().trim();
            String email = Objects.requireNonNull(mTextInputEmailR.getText()).toString().trim();
            String phone = Objects.requireNonNull(mTextInputPhone.getText()).toString().trim();
            String kinder = mSpinner.getSelectedItem().toString().trim();
            String password = Objects.requireNonNull(mTextInputPasswordR.getText()).toString().trim();
            String confirmPassword = Objects.requireNonNull(mTextInputConfirmPasswordR.getText()).toString().trim();
            if (!username.isEmpty()) {
                if (!email.isEmpty()) {
                    if (!phone.isEmpty()) {
                        if (!kinder.isEmpty()) {
                            if (!password.isEmpty()) {
                                if (!confirmPassword.isEmpty()) {
                                    if (!mUsernameList.isEmpty()) {
                                        for (String s : mUsernameList) {
                                            if (s.equals(username)) {
                                                Snackbar.make(v, "Ya existe un docente con ese nombre", Snackbar.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                    }
                                    if (!mPhoneList.isEmpty()) {
                                        for (String s : mPhoneList) {
                                            if (s.equals(phone)) {
                                                Snackbar.make(v, "Ya existe un docente con ese teléfono", Snackbar.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                    }
                                    if (isEmailValid(email)) {
                                        if (password.equals(confirmPassword)) {
                                            if (password.length() >= 6) {
                                                createUser(username, email, phone, kinder, password);
                                            } else {
                                                Snackbar.make(v, "La contraseña debe ser mayor o igual a 6 caracteres", Snackbar.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Snackbar.make(v, "Las contraseñas no coinciden", Snackbar.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Snackbar.make(v, "Formato de correo electrónico inválido", Snackbar.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Snackbar.make(v, "Debe confirmar su contraseña", Snackbar.LENGTH_SHORT).show();
                                }
                            } else {
                                Snackbar.make(v, "La contraseña es obligatoria", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Snackbar.make(v, "Debe seleccionar un jardín de niños", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(v, "El número de teléfono es obligatorio", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(v, "El correo electrónico es obligatorio", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(v, "El nombre y apellido es obligatorio", Snackbar.LENGTH_SHORT).show();
            }
        });
        mImageViewBack.setOnClickListener(v -> finish());
    }

    public void isUserInfoExist(List<String> stringList, String field) {
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

    public void getAllKindergartens() {
        List<Kinder> kinderList = new ArrayList<>();
        mKinderProvider.getAllDocuments().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                    if (snapshot.exists()) {
                        if (snapshot.contains("name")) {
                            String names = snapshot.getString("name");
                            kinderList.add(new Kinder(names, null));
                        }
                    }
                }
                ArrayAdapter<Kinder> arrayAdapter = new ArrayAdapter<>(RegisterActivity.this, android.R.layout.simple_dropdown_item_1line, kinderList);
                mSpinner.setAdapter(arrayAdapter);
                mSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String kinder = parent.getItemAtPosition(position).toString().trim();
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

    private void createUser(final String username, final String email, final String phone, final String kinder, final String password) {
        mDialog.show();
        mAuthProvider.register(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String id = mAuthProvider.getUid();
                Teacher teacher = new Teacher();
                teacher.setId(id);
                teacher.setEmail(email);
                teacher.setTeachername(username);
                teacher.setPhone(phone);
                teacher.setKinder(kinder);
                teacher.setTimestamp(new Date().getTime());
                mTeachersProvider.create(teacher).addOnCompleteListener(task1 -> {
                    mDialog.dismiss();
                    if (task1.isSuccessful()) {
                        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    } else {
                        Snackbar.make(coordinatorLayout, "Error al registrar el docente en la base de datos", Snackbar.LENGTH_SHORT).show();
                    }
                });
                Toast.makeText(RegisterActivity.this, "Bienvenido(a) " + username, Toast.LENGTH_LONG).show();
            } else {
                mDialog.dismiss();
                Snackbar.make(coordinatorLayout, "Ya existe un docente con ese correo electrónico", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}