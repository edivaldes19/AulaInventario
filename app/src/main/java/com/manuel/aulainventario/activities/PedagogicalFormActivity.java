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
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.manuel.aulainventario.R;
import com.manuel.aulainventario.models.Condition;
import com.manuel.aulainventario.models.Pedagogical;
import com.manuel.aulainventario.providers.AuthProvider;
import com.manuel.aulainventario.providers.ConditionsProvider;
import com.manuel.aulainventario.providers.PedagogicalProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.manuel.aulainventario.utils.Validations.validateFieldsAsYouType;

public class PedagogicalFormActivity extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;
    TextInputEditText mEditTextNumberP, mEditTextDescriptionP, mEditTextAmountP;
    MaterialTextView mTextViewConditionSelectedP;
    Spinner mSpinnerP;
    MaterialButton mButtonClear, mButtonAdd;
    AuthProvider mAuthProvider;
    ConditionsProvider mConditionsProvider;
    PedagogicalProvider mPedagogicalProvider;
    ProgressDialog mProgressDialog;
    String mExtraIdPedagogicalUpdate, mExtraPedagogicalTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedagogico_form);
        coordinatorLayout = findViewById(R.id.coordinatorPedagogical);
        mEditTextNumberP = findViewById(R.id.textInputNumberP);
        mEditTextDescriptionP = findViewById(R.id.textInputDescriptionP);
        mEditTextAmountP = findViewById(R.id.textInputAmountP);
        mTextViewConditionSelectedP = findViewById(R.id.textViewConditionSelectedP);
        mSpinnerP = findViewById(R.id.spinnerConditionP);
        mButtonClear = findViewById(R.id.btnClearP);
        mButtonAdd = findViewById(R.id.btnAddP);
        mAuthProvider = new AuthProvider();
        mConditionsProvider = new ConditionsProvider();
        mPedagogicalProvider = new PedagogicalProvider();
        mExtraIdPedagogicalUpdate = getIntent().getStringExtra("idPedagogicalUpdate");
        mExtraPedagogicalTitle = getIntent().getStringExtra("pedagogicalTitle");
        if (mExtraPedagogicalTitle != null && !mExtraPedagogicalTitle.isEmpty()) {
            setTitle("Editar registro " + mExtraPedagogicalTitle);
        } else {
            setTitle("Nuevo registro");
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Guardando registro...");
        mProgressDialog.setMessage("Por favor, espere un momento");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        validateFieldsAsYouType(mEditTextNumberP, "El número es obligatorio");
        validateFieldsAsYouType(mEditTextDescriptionP, "La descripción es obligatoria");
        validateFieldsAsYouType(mEditTextAmountP, "La cantidad es obligatoria");
        getAllConditions();
        getDataFromAdapter();
        mButtonClear.setOnClickListener(v -> cleanForm());
        mButtonAdd.setOnClickListener(v -> {
            if (getIntent().getBooleanExtra("pedagogicalSelect", false)) {
                //EDITAR REGISTRO
                String number = Objects.requireNonNull(mEditTextNumberP.getText()).toString().trim();
                String description = Objects.requireNonNull(mEditTextDescriptionP.getText()).toString().trim();
                String amount = Objects.requireNonNull(mEditTextAmountP.getText()).toString().trim();
                String condition = mSpinnerP.getSelectedItem().toString().trim();
                if (!number.isEmpty()) {
                    if (!description.isEmpty()) {
                        if (!amount.isEmpty()) {
                            if (!condition.isEmpty()) {
                                Pedagogical pedagogical = new Pedagogical();
                                pedagogical.setId(mExtraIdPedagogicalUpdate);
                                pedagogical.setNumber(Long.parseLong(number));
                                pedagogical.setDescription(description);
                                pedagogical.setAmount(Long.parseLong(amount));
                                pedagogical.setCondition(condition);
                                pedagogical.setTimestamp(new Date().getTime());
                                updateInfo(pedagogical);
                            } else {
                                Snackbar.make(v, "El estado es obligatorio", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Snackbar.make(v, "La cantidad es obligatoria", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(v, "La descripción es obligatoria", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(v, "El número es obligatorio", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                //CREAR REGISTRO
                String number = Objects.requireNonNull(mEditTextNumberP.getText()).toString().trim();
                String description = Objects.requireNonNull(mEditTextDescriptionP.getText()).toString().trim();
                String amount = Objects.requireNonNull(mEditTextAmountP.getText()).toString().trim();
                String condition = mSpinnerP.getSelectedItem().toString().trim();
                if (!number.isEmpty()) {
                    if (!description.isEmpty()) {
                        if (!amount.isEmpty()) {
                            if (!condition.isEmpty()) {
                                Pedagogical pedagogical = new Pedagogical();
                                pedagogical.setNumber(Long.parseLong(number));
                                pedagogical.setDescription(description);
                                pedagogical.setAmount(Long.parseLong(amount));
                                pedagogical.setCondition(condition);
                                pedagogical.setIdTeacher(mAuthProvider.getUid());
                                pedagogical.setTimestamp(new Date().getTime());
                                saveInfo(pedagogical);
                            } else {
                                Snackbar.make(v, "El estado es obligatorio", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Snackbar.make(v, "La cantidad es obligatoria", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(v, "La descripción es obligatoria", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(v, "El número es obligatorio", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getDataFromAdapter() {
        if (getIntent().getBooleanExtra("pedagogicalSelect", false)) {
            getPedagogical();
            mButtonAdd.setIconResource(R.drawable.ic_edit);
            mButtonAdd.setText("Editar");
        }
    }

    private void getPedagogical() {
        mPedagogicalProvider.getPedagogicalById(mExtraIdPedagogicalUpdate).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("number")) {
                    long number = documentSnapshot.getLong("number");
                    mEditTextNumberP.setText(String.valueOf(number));
                }
                if (documentSnapshot.contains("description")) {
                    String description = documentSnapshot.getString("description");
                    mEditTextDescriptionP.setText(description);
                }
                if (documentSnapshot.contains("amount")) {
                    long amount = documentSnapshot.getLong("amount");
                    mEditTextAmountP.setText(String.valueOf(amount));
                }
                if (documentSnapshot.contains("condition")) {
                    String condition = documentSnapshot.getString("condition");
                    mTextViewConditionSelectedP.setText(condition);
                    if (condition != null) {
                        switch (condition) {
                            case "Bueno":
                                mSpinnerP.setSelection(0);
                                break;
                            case "Malo":
                                mSpinnerP.setSelection(1);
                                break;
                            case "Regular":
                                mSpinnerP.setSelection(2);
                                break;
                        }
                    }
                }
            }
        });
    }

    private void saveInfo(Pedagogical pedagogical) {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
        mPedagogicalProvider.save(pedagogical).addOnCompleteListener(task -> {
            mProgressDialog.dismiss();
            if (task.isSuccessful()) {
                finish();
                Toast.makeText(this, "Registro creado", Toast.LENGTH_SHORT).show();
            } else {
                Snackbar.make(coordinatorLayout, "Error al crear el registro", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void updateInfo(Pedagogical pedagogical) {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
        mPedagogicalProvider.update(pedagogical).addOnCompleteListener(task -> {
            mProgressDialog.dismiss();
            if (task.isSuccessful()) {
                finish();
                Toast.makeText(this, "Registro editado", Toast.LENGTH_SHORT).show();
            } else {
                Snackbar.make(coordinatorLayout, "Error al editar el registro", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    public void getAllConditions() {
        List<Condition> conditionList = new ArrayList<>();
        mConditionsProvider.getAllDocuments().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
                    if (snapshot.exists()) {
                        if (snapshot.contains("condition")) {
                            String conditions = snapshot.getString("condition");
                            conditionList.add(new Condition(conditions));
                        }
                    }
                }
                ArrayAdapter<Condition> arrayAdapter = new ArrayAdapter<>(PedagogicalFormActivity.this, android.R.layout.simple_dropdown_item_1line, conditionList);
                mSpinnerP.setAdapter(arrayAdapter);
                mSpinnerP.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String condition = parent.getItemAtPosition(position).toString().trim();
                        mTextViewConditionSelectedP.setText("Estado: " + condition);
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
            } else {
                Snackbar.make(coordinatorLayout, "Error al obtener los estados", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void cleanForm() {
        mEditTextNumberP.setText(null);
        mEditTextDescriptionP.setText(null);
        mEditTextAmountP.setText(null);
        mTextViewConditionSelectedP.setText("Estado: ");
        mSpinnerP.setSelection(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}