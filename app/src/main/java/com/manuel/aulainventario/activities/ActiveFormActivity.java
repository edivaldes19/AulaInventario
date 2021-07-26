package com.manuel.aulainventario.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
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

import static com.manuel.aulainventario.utils.MyTools.calculateTotal;
import static com.manuel.aulainventario.utils.MyTools.getLastPositionOfASpinner;
import static com.manuel.aulainventario.utils.MyTools.validateFieldsAsYouType;

public class ActiveFormActivity extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;
    TextInputEditText mEditTextNumberA, mEditTextKeyA, mEditTextDescriptionA, mEditTextAmountA, mEditTextPriceA, mEditTextTotalA;
    MaterialTextView mTextViewConditionSelectedA;
    Spinner mSpinnerA;
    FloatingActionButton mFabClearA, mFabAddA;
    AuthProvider mAuthProvider;
    CollectionsProvider mCollectionsProvider, mCollectionsProviderForNumbers;
    ActiveProvider mActiveProvider;
    ProgressDialog mProgressDialog, mProgressDialogGetting;
    String mExtraIdActiveUpdate, mExtraActiveTitle;
    ArrayList<String> mConditionsList, mKeysList;
    ArrayList<Long> mNumbersList;

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
        mCollectionsProviderForNumbers = new CollectionsProvider(this, "Active");
        mActiveProvider = new ActiveProvider();
        mConditionsList = new ArrayList<>();
        mNumbersList = new ArrayList<>();
        mKeysList = new ArrayList<>();
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
        mProgressDialogGetting = new ProgressDialog(this);
        mProgressDialogGetting.setTitle("Obteniendo datos...");
        mProgressDialogGetting.setMessage("Por favor, espere un momento");
        mProgressDialogGetting.setCancelable(false);
        mProgressDialogGetting.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mCollectionsProvider.getAllTheDocumentsInACollectionAndSetTheAdapter(coordinatorLayout, mConditionsList, "condition", mSpinnerA, "Estado: ", mTextViewConditionSelectedA, "Error al obtener los estados");
        mCollectionsProviderForNumbers.getNumbersByTeacher(mAuthProvider.getUid(), coordinatorLayout, mNumbersList);
        mActiveProvider.getKeysByTeacher(mAuthProvider, coordinatorLayout, mKeysList);
        validateFieldsAsYouType(mEditTextNumberA, "El número es obligatorio");
        validateFieldsAsYouType(mEditTextKeyA, "La clave es obligatoria");
        validateFieldsAsYouType(mEditTextDescriptionA, "La descripción es obligatoria");
        validateFieldsAsYouType(mEditTextAmountA, "La cantidad es obligatoria");
        validateFieldsAsYouType(mEditTextPriceA, "El precio es obligatorio");
        validateFieldsAsYouType(mEditTextTotalA, "El total es obligatorio");
        calculateTotal(mEditTextAmountA, mEditTextPriceA, mEditTextTotalA);
        calculateTotal(mEditTextPriceA, mEditTextAmountA, mEditTextTotalA);
        mFabClearA.setOnClickListener(v -> cleanForm());
        mFabAddA.setOnClickListener(v -> addOrEditFixedAsset());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent().getBooleanExtra("activeSelect", false)) {
            getActive();
            mFabAddA.setImageResource(R.drawable.ic_edit);
        }
    }

    private void addOrEditFixedAsset() {
        if (getIntent().getBooleanExtra("activeSelect", false)) {
            String numberField = Objects.requireNonNull(mEditTextNumberA.getText()).toString().trim();
            String key = Objects.requireNonNull(mEditTextKeyA.getText()).toString().trim();
            String description = Objects.requireNonNull(mEditTextDescriptionA.getText()).toString().trim();
            String amountField = Objects.requireNonNull(mEditTextAmountA.getText()).toString().trim();
            String priceField = Objects.requireNonNull(mEditTextPriceA.getText()).toString().trim();
            String totalField = Objects.requireNonNull(mEditTextTotalA.getText()).toString().trim();
            String condition = mSpinnerA.getSelectedItem().toString().trim();
            if (!TextUtils.isEmpty(numberField)) {
                long number = Long.parseLong(numberField);
                if (number != 0) {
                    if (!TextUtils.isEmpty(key)) {
                        if (!TextUtils.isEmpty(description)) {
                            if (!TextUtils.isEmpty(amountField)) {
                                long amount = Long.parseLong(amountField);
                                if (amount != 0) {
                                    if (!TextUtils.isEmpty(priceField)) {
                                        double price = Double.parseDouble(priceField);
                                        if (price != 0) {
                                            if (!TextUtils.isEmpty(totalField)) {
                                                double total = Double.parseDouble(totalField);
                                                if (total != 0) {
                                                    if (!TextUtils.isEmpty(condition)) {
                                                        if (mNumbersList != null && !mNumbersList.isEmpty()) {
                                                            for (Long aLong : mNumbersList) {
                                                                if (aLong == number) {
                                                                    Snackbar.make(coordinatorLayout, "Ya existe un registro con ese número", Snackbar.LENGTH_SHORT).show();
                                                                    return;
                                                                }
                                                            }
                                                        }
                                                        if (mKeysList != null && !mKeysList.isEmpty()) {
                                                            for (String s : mKeysList) {
                                                                if (s.equals(key)) {
                                                                    Snackbar.make(coordinatorLayout, "Ya existe un registro con esa clave", Snackbar.LENGTH_SHORT).show();
                                                                    return;
                                                                }
                                                            }
                                                        }
                                                        Active active = new Active();
                                                        active.setId(mExtraIdActiveUpdate);
                                                        active.setNumber(number);
                                                        active.setKey(key);
                                                        active.setDescription(description);
                                                        active.setAmount(amount);
                                                        active.setPrice(price);
                                                        active.setTotal(total);
                                                        active.setCondition(condition);
                                                        active.setTimestamp(new Date().getTime());
                                                        updateInfo(active);
                                                    } else {
                                                        Snackbar.make(coordinatorLayout, "El estado es obligatorio", Snackbar.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Snackbar.make(coordinatorLayout, "El total no puede ser 0", Snackbar.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Snackbar.make(coordinatorLayout, "El total es obligatorio", Snackbar.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Snackbar.make(coordinatorLayout, "El precio no puede ser 0", Snackbar.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Snackbar.make(coordinatorLayout, "El precio es obligatorio", Snackbar.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Snackbar.make(coordinatorLayout, "La cantidad no puede ser 0", Snackbar.LENGTH_SHORT).show();
                                }
                            } else {
                                Snackbar.make(coordinatorLayout, "La cantidad es obligatoria", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Snackbar.make(coordinatorLayout, "La descripción es obligatoria", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(coordinatorLayout, "La clave es obligatoria", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(coordinatorLayout, "El número no puede ser 0", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(coordinatorLayout, "El número es obligatorio", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            String numberField = Objects.requireNonNull(mEditTextNumberA.getText()).toString().trim();
            String key = Objects.requireNonNull(mEditTextKeyA.getText()).toString().trim();
            String description = Objects.requireNonNull(mEditTextDescriptionA.getText()).toString().trim();
            String amountField = Objects.requireNonNull(mEditTextAmountA.getText()).toString().trim();
            String priceField = Objects.requireNonNull(mEditTextPriceA.getText()).toString().trim();
            String totalField = Objects.requireNonNull(mEditTextTotalA.getText()).toString().trim();
            String condition = mSpinnerA.getSelectedItem().toString().trim();
            if (!TextUtils.isEmpty(numberField)) {
                long number = Long.parseLong(numberField);
                if (number != 0) {
                    if (!TextUtils.isEmpty(key)) {
                        if (!TextUtils.isEmpty(description)) {
                            if (!TextUtils.isEmpty(amountField)) {
                                long amount = Long.parseLong(amountField);
                                if (amount != 0) {
                                    if (!TextUtils.isEmpty(priceField)) {
                                        double price = Double.parseDouble(priceField);
                                        if (price != 0) {
                                            if (!TextUtils.isEmpty(totalField)) {
                                                double total = Double.parseDouble(totalField);
                                                if (total != 0) {
                                                    if (!TextUtils.isEmpty(condition)) {
                                                        if (mNumbersList != null && !mNumbersList.isEmpty()) {
                                                            for (Long aLong : mNumbersList) {
                                                                if (aLong == number) {
                                                                    Snackbar.make(coordinatorLayout, "Ya existe un registro con ese número", Snackbar.LENGTH_SHORT).show();
                                                                    return;
                                                                }
                                                            }
                                                        }
                                                        if (mKeysList != null && !mKeysList.isEmpty()) {
                                                            for (String s : mKeysList) {
                                                                if (s.equals(key)) {
                                                                    Snackbar.make(coordinatorLayout, "Ya existe un registro con esa clave", Snackbar.LENGTH_SHORT).show();
                                                                    return;
                                                                }
                                                            }
                                                        }
                                                        Active active = new Active();
                                                        active.setNumber(number);
                                                        active.setKey(key);
                                                        active.setDescription(description);
                                                        active.setAmount(amount);
                                                        active.setPrice(price);
                                                        active.setTotal(total);
                                                        active.setCondition(condition);
                                                        active.setIdTeacher(mAuthProvider.getUid());
                                                        active.setTimestamp(new Date().getTime());
                                                        saveInfo(active);
                                                    } else {
                                                        Snackbar.make(coordinatorLayout, "El estado es obligatorio", Snackbar.LENGTH_SHORT).show();
                                                    }
                                                } else {
                                                    Snackbar.make(coordinatorLayout, "El total no puede ser 0", Snackbar.LENGTH_SHORT).show();
                                                }
                                            } else {
                                                Snackbar.make(coordinatorLayout, "El total es obligatorio", Snackbar.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            Snackbar.make(coordinatorLayout, "El precio no puede ser 0", Snackbar.LENGTH_SHORT).show();
                                        }
                                    } else {
                                        Snackbar.make(coordinatorLayout, "El precio es obligatorio", Snackbar.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Snackbar.make(coordinatorLayout, "La cantidad no puede ser 0", Snackbar.LENGTH_SHORT).show();
                                }
                            } else {
                                Snackbar.make(coordinatorLayout, "La cantidad es obligatoria", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Snackbar.make(coordinatorLayout, "La descripción es obligatoria", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(coordinatorLayout, "La clave es obligatoria", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(coordinatorLayout, "El número no puede ser 0", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(coordinatorLayout, "El número es obligatorio", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void getActive() {
        mProgressDialogGetting.show();
        mActiveProvider.getActiveById(mExtraIdActiveUpdate).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("number")) {
                    long number = documentSnapshot.getLong("number");
                    mEditTextNumberA.setText(String.valueOf(number));
                    if (mNumbersList != null && !mNumbersList.isEmpty()) {
                        for (int i = 0; i < mNumbersList.size(); i++) {
                            if (mNumbersList.get(i) == number) {
                                mNumbersList.remove(i);
                                break;
                            }
                        }
                    }
                }
                if (documentSnapshot.contains("key")) {
                    String key = documentSnapshot.getString("key");
                    mEditTextKeyA.setText(key);
                    if (mKeysList != null && !mKeysList.isEmpty()) {
                        for (int i = 0; i < mKeysList.size(); i++) {
                            if (mKeysList.get(i).equals(key)) {
                                mKeysList.remove(i);
                                break;
                            }
                        }
                    }
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
            mProgressDialogGetting.dismiss();
        });
    }

    private void saveInfo(Active active) {
        mProgressDialog.show();
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
        mProgressDialog.show();
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

    private void cleanForm() {
        mEditTextNumberA.setText(null);
        mEditTextKeyA.setText(null);
        mEditTextDescriptionA.setText(null);
        mEditTextAmountA.setText(null);
        mEditTextPriceA.setText(null);
        mEditTextTotalA.setText(null);
        mSpinnerA.setSelection(0);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}