package com.manuel.aulainventario.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.manuel.aulainventario.R;
import com.manuel.aulainventario.models.Active;
import com.manuel.aulainventario.providers.ActiveProvider;
import com.manuel.aulainventario.providers.AuthProvider;
import com.manuel.aulainventario.providers.CollectionsProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static com.manuel.aulainventario.utils.Validations.calculateTotal;
import static com.manuel.aulainventario.utils.Validations.getLastPositionOfASpinner;
import static com.manuel.aulainventario.utils.Validations.validateFieldsAsYouType;

public class ActiveFormActivity extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;
    TextInputEditText mEditTextNumberA, mEditTextKeyA, mEditTextDescriptionA, mEditTextAmountA, mEditTextPriceA, mEditTextTotalA;
    MaterialTextView mTextViewConditionSelectedA;
    Spinner mSpinnerA;
    FloatingActionButton mFabClearA, mFabAddA;
    AuthProvider mAuthProvider;
    CollectionsProvider mCollectionsProvider;
    ActiveProvider mActiveProvider;
    ProgressDialog mProgressDialog;
    String mExtraIdActiveUpdate, mExtraActiveTitle;
    ArrayList<String> mConditionsList;

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
        mFabClearA = findViewById(R.id.fabClearA);
        mFabAddA = findViewById(R.id.fabAddA);
        mAuthProvider = new AuthProvider();
        mCollectionsProvider = new CollectionsProvider(this, "Conditions");
        mActiveProvider = new ActiveProvider();
        mConditionsList = new ArrayList<>();
        mExtraIdActiveUpdate = getIntent().getStringExtra("idActiveUpdate");
        mExtraActiveTitle = getIntent().getStringExtra("activeTitle");
        if (!TextUtils.isEmpty(mExtraActiveTitle)) {
            setTitle("Editar registro " + mExtraActiveTitle);
        } else {
            setTitle("Nuevo activo fijo");
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
        calculateTotal(mEditTextAmountA, mEditTextPriceA, mEditTextTotalA);
        calculateTotal(mEditTextPriceA, mEditTextAmountA, mEditTextTotalA);
        mCollectionsProvider.getAllTheDocumentsInACollectionAndSetTheAdapter(coordinatorLayout, mConditionsList, "condition", mSpinnerA, "Estado: ", mTextViewConditionSelectedA, "Error al obtener los estados");
        getDataFromAdapter();
        mFabClearA.setOnClickListener(v -> cleanForm());
        mFabAddA.setOnClickListener(v -> {
            if (getIntent().getBooleanExtra("activeSelect", false)) {
                //EDITAR REGISTRO
                String number = Objects.requireNonNull(mEditTextNumberA.getText()).toString().trim();
                String key = Objects.requireNonNull(mEditTextKeyA.getText()).toString().trim();
                String description = Objects.requireNonNull(mEditTextDescriptionA.getText()).toString().trim();
                String amount = Objects.requireNonNull(mEditTextAmountA.getText()).toString().trim();
                String price = Objects.requireNonNull(mEditTextPriceA.getText()).toString().trim();
                String total = Objects.requireNonNull(mEditTextTotalA.getText()).toString().trim();
                String condition = mSpinnerA.getSelectedItem().toString().trim();
                if (!TextUtils.isEmpty(number)) {
                    if (!TextUtils.isEmpty(key)) {
                        if (!TextUtils.isEmpty(description)) {
                            if (!TextUtils.isEmpty(amount)) {
                                if (!TextUtils.isEmpty(price)) {
                                    if (!TextUtils.isEmpty(total)) {
                                        if (!TextUtils.isEmpty(condition)) {
                                            Active active = new Active();
                                            active.setId(mExtraIdActiveUpdate);
                                            active.setNumber(Long.parseLong(number));
                                            active.setKey(key);
                                            active.setDescription(description);
                                            active.setAmount(Long.parseLong(amount));
                                            active.setPrice(Double.parseDouble(price));
                                            active.setTotal(Double.parseDouble(total));
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
                if (!TextUtils.isEmpty(number)) {
                    if (!TextUtils.isEmpty(key)) {
                        if (!TextUtils.isEmpty(description)) {
                            if (!TextUtils.isEmpty(amount)) {
                                if (!TextUtils.isEmpty(price)) {
                                    if (!TextUtils.isEmpty(total)) {
                                        if (!TextUtils.isEmpty(condition)) {
                                            Active active = new Active();
                                            active.setNumber(Long.parseLong(number));
                                            active.setKey(key);
                                            active.setDescription(description);
                                            active.setAmount(Long.parseLong(amount));
                                            active.setPrice(Double.parseDouble(price));
                                            active.setTotal(Double.parseDouble(total));
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
            mFabAddA.setImageResource(R.drawable.ic_edit);
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
                    double price = documentSnapshot.getDouble("price");
                    mEditTextPriceA.setText(String.valueOf(price));
                }
                if (documentSnapshot.contains("total")) {
                    double total = documentSnapshot.getDouble("total");
                    mEditTextTotalA.setText(String.valueOf(total));
                }
                if (documentSnapshot.contains("condition")) {
                    String condition = documentSnapshot.getString("condition");
                    mSpinnerA.setSelection(getLastPositionOfASpinner(mSpinnerA, condition));
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

    @SuppressLint("SetTextI18n")
    private void cleanForm() {
        mEditTextNumberA.setText(null);
        mEditTextKeyA.setText(null);
        mEditTextDescriptionA.setText(null);
        mEditTextAmountA.setText(null);
        mEditTextPriceA.setText(null);
        mEditTextTotalA.setText(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}