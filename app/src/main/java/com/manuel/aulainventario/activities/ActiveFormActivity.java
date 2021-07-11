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
import com.manuel.aulainventario.models.Active;
import com.manuel.aulainventario.models.Condition;
import com.manuel.aulainventario.providers.ActiveProvider;
import com.manuel.aulainventario.providers.AuthProvider;
import com.manuel.aulainventario.providers.ConditionsProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import static com.manuel.aulainventario.utils.Validations.validateFieldsAsYouType;

public class ActiveFormActivity extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;
    TextInputEditText mEditTextNumberA, mEditTextKeyA, mEditTextDescriptionA, mEditTextAmountA, mEditTextPriceA, mEditTextTotalA;
    MaterialTextView mTextViewConditionSelectedA;
    Spinner mSpinnerA;
    MaterialButton mButtonClearA, mButtonAddA;
    AuthProvider mAuthProvider;
    ConditionsProvider mConditionsProvider;
    ActiveProvider mActiveProvider;
    ProgressDialog mProgressDialog;
    String mExtraIdActiveUpdate, mExtraActiveTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_form);
        coordinatorLayout = findViewById(R.id.coordinatorActive);
        mEditTextNumberA = findViewById(R.id.textInputNumberA);
        mEditTextKeyA = findViewById(R.id.textInputKeyA);
        mEditTextDescriptionA = findViewById(R.id.textInputDescriptionA);
        mEditTextAmountA = findViewById(R.id.textInputAmountA);
        mEditTextPriceA = findViewById(R.id.textInputPriceA);
        mEditTextTotalA = findViewById(R.id.textInputTotalA);
        mTextViewConditionSelectedA = findViewById(R.id.textViewConditionSelectedA);
        mSpinnerA = findViewById(R.id.spinnerConditionA);
        mButtonClearA = findViewById(R.id.btnClearA);
        mButtonAddA = findViewById(R.id.btnAddA);
        mAuthProvider = new AuthProvider();
        mConditionsProvider = new ConditionsProvider();
        mActiveProvider = new ActiveProvider();
        mExtraIdActiveUpdate = getIntent().getStringExtra("idActiveUpdate");
        mExtraActiveTitle = getIntent().getStringExtra("activeTitle");
        if (mExtraActiveTitle != null && !mExtraActiveTitle.isEmpty()) {
            setTitle("Editar registro " + mExtraActiveTitle);
        } else {
            setTitle("Nuevo registro");
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Guardando registro...");
        mProgressDialog.setMessage("Por favor, espere un momento");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        validateFieldsAsYouType(mEditTextNumberA, "El número es obligatorio");
        validateFieldsAsYouType(mEditTextKeyA, "La clave es obligatoria");
        validateFieldsAsYouType(mEditTextDescriptionA, "La descripción es obligatoria");
        validateFieldsAsYouType(mEditTextAmountA, "La cantidad es obligatoria");
        validateFieldsAsYouType(mEditTextPriceA, "El precio es obligatorio");
        validateFieldsAsYouType(mEditTextTotalA, "El total es obligatorio");
        getAllConditions();
        getDataFromAdapter();
        mButtonClearA.setOnClickListener(v -> cleanForm());
        mButtonAddA.setOnClickListener(v -> {
            if (getIntent().getBooleanExtra("activeSelect", false)) {
                //EDITAR REGISTRO
                String number = Objects.requireNonNull(mEditTextNumberA.getText()).toString().trim();
                String key = Objects.requireNonNull(mEditTextKeyA.getText()).toString().trim();
                String description = Objects.requireNonNull(mEditTextDescriptionA.getText()).toString().trim();
                String amount = Objects.requireNonNull(mEditTextAmountA.getText()).toString().trim();
                String price = Objects.requireNonNull(mEditTextPriceA.getText()).toString().trim();
                String total = Objects.requireNonNull(mEditTextTotalA.getText()).toString().trim();
                String condition = mSpinnerA.getSelectedItem().toString().trim();
                if (!number.isEmpty()) {
                    if (!key.isEmpty()) {
                        if (!description.isEmpty()) {
                            if (!amount.isEmpty()) {
                                if (!price.isEmpty()) {
                                    if (!total.isEmpty()) {
                                        if (!condition.isEmpty()) {
                                            Active active = new Active();
                                            active.setId(mExtraIdActiveUpdate);
                                            active.setNumber(Long.parseLong(number));
                                            active.setKey(key);
                                            active.setDescription(description);
                                            active.setAmount(Long.parseLong(amount));
                                            active.setPrice(Long.parseLong(price));
                                            active.setTotal(Long.parseLong(total));
                                            active.setCondition(condition);
                                            active.setTimestamp(new Date().getTime());
                                            updateInfo(active);
                                        } else {
                                            Snackbar.make(v, "El estado es obligatorio", Snackbar.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Snackbar.make(v, "El total es obligatorio", Snackbar.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Snackbar.make(v, "El precio es obligatorio", Snackbar.LENGTH_SHORT).show();
                                }
                            } else {
                                Snackbar.make(v, "La cantidad es obligatoria", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Snackbar.make(v, "La descripción es obligatoria", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(v, "La clave es obligatoria", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(v, "El número es obligatorio", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                //CREAR REGISTRO
                String number = Objects.requireNonNull(mEditTextNumberA.getText()).toString().trim();
                String key = Objects.requireNonNull(mEditTextKeyA.getText()).toString().trim();
                String description = Objects.requireNonNull(mEditTextDescriptionA.getText()).toString().trim();
                String amount = Objects.requireNonNull(mEditTextAmountA.getText()).toString().trim();
                String price = Objects.requireNonNull(mEditTextPriceA.getText()).toString().trim();
                String total = Objects.requireNonNull(mEditTextTotalA.getText()).toString().trim();
                String condition = mSpinnerA.getSelectedItem().toString().trim();
                if (!number.isEmpty()) {
                    if (!key.isEmpty()) {
                        if (!description.isEmpty()) {
                            if (!amount.isEmpty()) {
                                if (!price.isEmpty()) {
                                    if (!total.isEmpty()) {
                                        if (!condition.isEmpty()) {
                                            Active active = new Active();
                                            active.setNumber(Long.parseLong(number));
                                            active.setKey(key);
                                            active.setDescription(description);
                                            active.setAmount(Long.parseLong(amount));
                                            active.setPrice(Long.parseLong(price));
                                            active.setTotal(Long.parseLong(total));
                                            active.setCondition(condition);
                                            active.setIdTeacher(mAuthProvider.getUid());
                                            active.setTimestamp(new Date().getTime());
                                            saveInfo(active);
                                        } else {
                                            Snackbar.make(v, "El estado es obligatorio", Snackbar.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Snackbar.make(v, "El total es obligatorio", Snackbar.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Snackbar.make(v, "El precio es obligatorio", Snackbar.LENGTH_SHORT).show();
                                }
                            } else {
                                Snackbar.make(v, "La cantidad es obligatoria", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Snackbar.make(v, "La descripción es obligatoria", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(v, "La clave es obligatoria", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(v, "El número es obligatorio", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void getDataFromAdapter() {
        if (getIntent().getBooleanExtra("activeSelect", false)) {
            getActive();
            mButtonAddA.setIconResource(R.drawable.ic_edit);
            mButtonAddA.setText("Editar");
        }
    }

    private void getActive() {
        mActiveProvider.getActiveById(mExtraIdActiveUpdate).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("number")) {
                    long number = documentSnapshot.getLong("number");
                    mEditTextNumberA.setText(String.valueOf(number));
                }
                if (documentSnapshot.contains("key")) {
                    String key = documentSnapshot.getString("key");
                    mEditTextKeyA.setText(key);
                }
                if (documentSnapshot.contains("description")) {
                    String description = documentSnapshot.getString("description");
                    mEditTextDescriptionA.setText(description);
                }
                if (documentSnapshot.contains("amount")) {
                    long amount = documentSnapshot.getLong("amount");
                    mEditTextAmountA.setText(String.valueOf(amount));
                }
                if (documentSnapshot.contains("price")) {
                    long price = documentSnapshot.getLong("price");
                    mEditTextPriceA.setText(String.valueOf(price));
                }
                if (documentSnapshot.contains("total")) {
                    long total = documentSnapshot.getLong("total");
                    mEditTextTotalA.setText(String.valueOf(total));
                }
                if (documentSnapshot.contains("condition")) {
                    String condition = documentSnapshot.getString("condition");
                    mTextViewConditionSelectedA.setText(condition);
                    if (condition != null) {
                        switch (condition) {
                            case "Bueno":
                                mSpinnerA.setSelection(0);
                                break;
                            case "Malo":
                                mSpinnerA.setSelection(1);
                                break;
                            case "Regular":
                                mSpinnerA.setSelection(2);
                                break;
                        }
                    }
                }
            }
        });
    }

    private void saveInfo(Active active) {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
        mActiveProvider.save(active).addOnCompleteListener(task -> {
            mProgressDialog.dismiss();
            if (task.isSuccessful()) {
                finish();
                Toast.makeText(this, "Registro creado", Toast.LENGTH_SHORT).show();
            } else {
                Snackbar.make(coordinatorLayout, "Error al crear el registro", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void updateInfo(Active active) {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
        mActiveProvider.update(active).addOnCompleteListener(task -> {
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
                ArrayAdapter<Condition> arrayAdapter = new ArrayAdapter<>(ActiveFormActivity.this, android.R.layout.simple_dropdown_item_1line, conditionList);
                mSpinnerA.setAdapter(arrayAdapter);
                mSpinnerA.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @SuppressLint("SetTextI18n")
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String condition = parent.getItemAtPosition(position).toString().trim();
                        mTextViewConditionSelectedA.setText("Estado: " + condition);
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
        mEditTextNumberA.setText(null);
        mEditTextKeyA.setText(null);
        mEditTextDescriptionA.setText(null);
        mEditTextAmountA.setText(null);
        mEditTextPriceA.setText(null);
        mEditTextTotalA.setText(null);
        mTextViewConditionSelectedA.setText("Estado: ");
        mSpinnerA.setSelection(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}