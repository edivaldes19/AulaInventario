package com.manuel.aulainventario.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
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
import com.manuel.aulainventario.providers.CollectionsProvider;
import com.manuel.aulainventario.providers.KinderProvider;
import com.manuel.aulainventario.providers.TeachersProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.manuel.aulainventario.utils.Validations.validateFieldsAsYouType;

public class EditInfoActivity extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;
    ShapeableImageView mImageViewBack;
    TextInputEditText mTextInputTeachername, mTextInputPhone;
    MaterialTextView mTextViewKinderSelected, mTextViewTurnSelected, mTextViewGradeSelected, mTextViewGroupSelected;
    Spinner mSpinnerKinder, mSpinnerTurn, mSpinnerGrade, mSpinnerGroup;
    MaterialButton mButtonEdit;
    AuthProvider mAuthProvider;
    TeachersProvider mTeachersProvider;
    KinderProvider mKinderProvider;
    CollectionsProvider mCollectionsProviderKindergartens, mCollectionsProviderShifts, mCollectionsProviderGrades, mCollectionsProviderGroups;
    ProgressDialog mDialog;
    ArrayList<String> mUsernameList, mPhoneList, mShiftsList, mGradesList, mGroupsList;
    String mIdKinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        coordinatorLayout = findViewById(R.id.coordinatorEdit);
        mImageViewBack = findViewById(R.id.imageViewBack);
        mTextInputTeachername = findViewById(R.id.textInputUsernameEdit);
        mTextInputPhone = findViewById(R.id.textInputPhoneEdit);
        mTextViewKinderSelected = findViewById(R.id.textViewKinderSelectedEdit);
        mTextViewTurnSelected = findViewById(R.id.textViewTurnSelectedEdit);
        mTextViewGradeSelected = findViewById(R.id.textViewGradeSelectedEdit);
        mTextViewGroupSelected = findViewById(R.id.textViewGroupSelectedEdit);
        mSpinnerKinder = findViewById(R.id.spinnerKinderEdit);
        mSpinnerTurn = findViewById(R.id.spinnerTurnEdit);
        mSpinnerGrade = findViewById(R.id.spinnerGradeEdit);
        mSpinnerGroup = findViewById(R.id.spinnerGroupEdit);
        mButtonEdit = findViewById(R.id.btnEdit);
        mAuthProvider = new AuthProvider();
        mTeachersProvider = new TeachersProvider();
        mKinderProvider = new KinderProvider();
        mCollectionsProviderKindergartens = new CollectionsProvider(this, "Kindergartens");
        mCollectionsProviderShifts = new CollectionsProvider(this, "Shifts");
        mCollectionsProviderGrades = new CollectionsProvider(this, "Grades");
        mCollectionsProviderGroups = new CollectionsProvider(this, "Groups");
        mUsernameList = new ArrayList<>();
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
        isUserInfoExist(mUsernameList, "teachername");
        isUserInfoExist(mPhoneList, "phone");
        mCollectionsProviderShifts.getAllTheDocumentsInACollectionAndSetTheAdapter(coordinatorLayout, mShiftsList, "turn", mSpinnerTurn, "Turno: ", mTextViewTurnSelected, "Error al obtener los turnos");
        mCollectionsProviderGrades.getAllTheDocumentsInACollectionAndSetTheAdapter(coordinatorLayout, mGradesList, "grade", mSpinnerGrade, "Grado: ", mTextViewGradeSelected, "Error al obtener los grados");
        mCollectionsProviderGroups.getAllTheDocumentsInACollectionAndSetTheAdapter(coordinatorLayout, mGroupsList, "group", mSpinnerGroup, "Grupo: ", mTextViewGroupSelected, "Error al obtener los grupos");
        getAllKindergartens();
        mButtonEdit.setOnClickListener(v -> {
            String username = Objects.requireNonNull(mTextInputTeachername.getText()).toString().trim();
            String phone = Objects.requireNonNull(mTextInputPhone.getText()).toString().trim();
            String kinder = mSpinnerKinder.getSelectedItem().toString().trim();
            String turn = mSpinnerTurn.getSelectedItem().toString().trim();
            String grade = mSpinnerGrade.getSelectedItem().toString().trim();
            String group = mSpinnerGroup.getSelectedItem().toString().trim();
            if (!username.isEmpty()) {
                if (!phone.isEmpty()) {
                    if (!kinder.isEmpty()) {
                        if (!turn.isEmpty()) {
                            if (!grade.isEmpty()) {
                                if (!group.isEmpty()) {
                                    if (mUsernameList != null && !mUsernameList.isEmpty()) {
                                        for (int i = 0; i < mUsernameList.size(); i++) {
                                            if (mUsernameList.get(i).equals(username)) {
                                                mUsernameList.remove(i);
                                                break;
                                            }
                                        }
                                        for (String s : mUsernameList) {
                                            if (s.equals(username)) {
                                                Snackbar.make(v, "Ya existe un docente con ese nombre", Snackbar.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                    }
                                    if (mPhoneList != null && !mPhoneList.isEmpty()) {
                                        for (int i = 0; i < mPhoneList.size(); i++) {
                                            if (mPhoneList.get(i).equals(phone)) {
                                                mPhoneList.remove(i);
                                                break;
                                            }
                                        }
                                        for (String s : mPhoneList) {
                                            if (s.equals(phone)) {
                                                Snackbar.make(v, "Ya existe un docente con ese teléfono", Snackbar.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                    }
                                    updateUser(username, phone, turn, grade, group);
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
        mImageViewBack.setOnClickListener(v -> finish());
        getTeacher();
    }

    private void getTeacher() {
        mTeachersProvider.getTeacher(mAuthProvider.getUid()).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("idKinder")) {
                    String idKinder = documentSnapshot.getString("idKinder");
                    if (idKinder != null) {
                        mKinderProvider.getKindergartens(idKinder).addOnSuccessListener(documentSnapshot1 -> {
                            if (documentSnapshot1.exists()) {
                                if (documentSnapshot1.contains("name")) {
                                    String name = documentSnapshot1.getString("name");
                                    mTextViewKinderSelected.setText(name);
                                    for (int i = 0; i < mSpinnerKinder.getCount(); i++) {
                                        if (mSpinnerKinder.getItemAtPosition(i).toString().equalsIgnoreCase(name)) {
                                            mSpinnerKinder.setSelection(i);
                                            break;
                                        }
                                    }
                                }
                            }
                        });
                    }
                }
                if (documentSnapshot.contains("teachername")) {
                    String teachername = documentSnapshot.getString("teachername");
                    mTextInputTeachername.setText(teachername);
                }
                if (documentSnapshot.contains("phone")) {
                    String phone = documentSnapshot.getString("phone");
                    mTextInputPhone.setText(phone);
                }
                if (documentSnapshot.contains("turn")) {
                    String turn = documentSnapshot.getString("turn");
                    mTextViewTurnSelected.setText(turn);
                    for (int i = 0; i < mSpinnerTurn.getCount(); i++) {
                        if (mSpinnerTurn.getItemAtPosition(i).toString().equalsIgnoreCase(turn)) {
                            mSpinnerTurn.setSelection(i);
                            break;
                        }
                    }
                }
                if (documentSnapshot.contains("grade")) {
                    String grade = documentSnapshot.getString("grade");
                    mTextViewGradeSelected.setText(grade);
                    for (int i = 0; i < mSpinnerGrade.getCount(); i++) {
                        if (mSpinnerGrade.getItemAtPosition(i).toString().equalsIgnoreCase(grade)) {
                            mSpinnerGrade.setSelection(i);
                            break;
                        }
                    }
                }
                if (documentSnapshot.contains("group")) {
                    String group = documentSnapshot.getString("group");
                    mTextViewGroupSelected.setText(group);
                    for (int i = 0; i < mSpinnerGroup.getCount(); i++) {
                        if (mSpinnerGroup.getItemAtPosition(i).toString().equalsIgnoreCase(group)) {
                            mSpinnerGroup.setSelection(i);
                            break;
                        }
                    }
                }
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

    public void getAllKindergartens() {
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
                ArrayAdapter<Kinder> arrayAdapter = new ArrayAdapter<>(EditInfoActivity.this, android.R.layout.simple_dropdown_item_1line, kinderList);
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

    private void updateUser(String username, String phone, String turn, String grade, String group) {
        String id = mAuthProvider.getUid();
        Teacher teacher = new Teacher();
        teacher.setId(id);
        teacher.setIdKinder(mIdKinder);
        teacher.setTeachername(username);
        teacher.setPhone(phone);
        teacher.setTurn(turn);
        teacher.setGrade(grade);
        teacher.setGroup(group);
        teacher.setTimestamp(new Date().getTime());
        mDialog.show();
        mTeachersProvider.update(teacher).addOnCompleteListener(task1 -> {
            mDialog.dismiss();
            if (task1.isSuccessful()) {
                finish();
                Toast.makeText(this, "Información actualizada", Toast.LENGTH_SHORT).show();
            } else {
                Snackbar.make(coordinatorLayout, "Error al registrar el docente en la base de datos", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}