package com.manuel.aulainventario.activities;

import static com.manuel.aulainventario.utils.MyTools.compareDataString;
import static com.manuel.aulainventario.utils.MyTools.compareTeachersInformation;
import static com.manuel.aulainventario.utils.MyTools.deleteCurrentInformationString;
import static com.manuel.aulainventario.utils.MyTools.setPositionByGrade;
import static com.manuel.aulainventario.utils.MyTools.setPositionByGroup;
import static com.manuel.aulainventario.utils.MyTools.setPositionByKindergarten;
import static com.manuel.aulainventario.utils.MyTools.setPositionByTurn;
import static com.manuel.aulainventario.utils.MyTools.validateFieldsAsYouType;

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
    ProgressDialog mDialog, mProgressDialogGetting;
    ArrayList<String> mTeachernameList, mPhoneList, mShiftsList, mGradesList, mGroupsList;
    List<Teacher> mTeacherList;
    String mIdKinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_info);
        coordinatorLayout = findViewById(R.id.coordinatorEdit);
        mImageViewBack = findViewById(R.id.imageViewBack);
        mTextInputTeachername = findViewById(R.id.textInputTeachernameEdit);
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
        mTeachernameList = new ArrayList<>();
        mPhoneList = new ArrayList<>();
        mShiftsList = new ArrayList<>();
        mGradesList = new ArrayList<>();
        mGroupsList = new ArrayList<>();
        mTeacherList = new ArrayList<>();
        mDialog = new ProgressDialog(this);
        mDialog.setTitle("Editando información...");
        mDialog.setMessage("Por favor, espere un momento");
        mDialog.setCancelable(false);
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mProgressDialogGetting = new ProgressDialog(this);
        mProgressDialogGetting.setTitle("Obteniendo datos...");
        mProgressDialogGetting.setMessage("Por favor, espere un momento");
        mProgressDialogGetting.setCancelable(false);
        mProgressDialogGetting.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mCollectionsProviderShifts.getAllTheDocumentsInACollectionAndSetTheAdapter(coordinatorLayout, mShiftsList, "turn", mSpinnerTurn, "Turno: ", mTextViewTurnSelected, "Error al obtener los turnos");
        mCollectionsProviderGrades.getAllTheDocumentsInACollectionAndSetTheAdapter(coordinatorLayout, mGradesList, "grade", mSpinnerGrade, "Grado: ", mTextViewGradeSelected, "Error al obtener los grados");
        mCollectionsProviderGroups.getAllTheDocumentsInACollectionAndSetTheAdapter(coordinatorLayout, mGroupsList, "group", mSpinnerGroup, "Grupo: ", mTextViewGroupSelected, "Error al obtener los grupos");
        validateFieldsAsYouType(mTextInputTeachername, "El nombre y apellido es obligatorio");
        validateFieldsAsYouType(mTextInputPhone, "El número de teléfono es obligatorio");
        isUserInfoExist(mTeachernameList, "teachername");
        isUserInfoExist(mPhoneList, "phone");
        getAllKindergartens();
        mImageViewBack.setOnClickListener(v -> finish());
        mButtonEdit.setOnClickListener(v -> editInfo());
    }

    @Override
    protected void onStart() {
        super.onStart();
        getTeacher();
    }

    private void editInfo() {
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
                                deleteCurrentInformationString(mTeachernameList, teachername);
                                deleteCurrentInformationString(mPhoneList, phone);
                                deleteCurrentInformationString(mShiftsList, turn);
                                deleteCurrentInformationString(mGradesList, grade);
                                deleteCurrentInformationString(mGroupsList, group);
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
                                updateUser(teachername, phone, turn, grade, group);
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
            Snackbar.make(coordinatorLayout, "El nombre y apellido es obligatorio", Snackbar.LENGTH_SHORT).show();
        }
    }

    private void getTeacher() {
        mProgressDialogGetting.show();
        mTeachersProvider.getTeacher(mAuthProvider.getUid()).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("idKinder")) {
                    String idKinder = documentSnapshot.getString("idKinder");
                    if (!TextUtils.isEmpty(idKinder)) {
                        mKinderProvider.getKindergartens(idKinder).addOnSuccessListener(documentSnapshot1 -> {
                            if (documentSnapshot1.exists()) {
                                if (documentSnapshot1.contains("name")) {
                                    String name = documentSnapshot1.getString("name");
                                    mTextViewKinderSelected.setText(name);
                                    mSpinnerKinder.setSelection(setPositionByKindergarten(mSpinnerKinder, name));
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
                    setPositionByTurn(mSpinnerTurn, turn);
                }
                if (documentSnapshot.contains("grade")) {
                    String grade = documentSnapshot.getString("grade");
                    setPositionByGrade(mSpinnerGrade, grade);
                }
                if (documentSnapshot.contains("group")) {
                    String group = documentSnapshot.getString("group");
                    setPositionByGroup(mSpinnerGroup, group);
                }
            }
            mProgressDialogGetting.dismiss();
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
                            kinderList.add(new Kinder(ids, names, null, null, null));
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

    private void updateUser(String teachername, String phone, String turn, String grade, String group) {
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
                startActivity(new Intent(this, MyProfileActivity.class));
                finish();
                Toast.makeText(this, "Información actualizada", Toast.LENGTH_SHORT).show();
            } else {
                Snackbar.make(coordinatorLayout, "Error al registrar el docente en la base de datos", Snackbar.LENGTH_SHORT).show();
            }
        });
    }
}