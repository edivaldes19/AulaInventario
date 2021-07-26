package com.manuel.aulainventario.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.manuel.aulainventario.R;
import com.manuel.aulainventario.models.Consumption;
import com.manuel.aulainventario.providers.AuthProvider;
import com.manuel.aulainventario.providers.CollectionsProvider;
import com.manuel.aulainventario.providers.ConsumptionProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static com.manuel.aulainventario.utils.MyTools.validateFieldsAsYouType;

public class ConsumptionFormActivity extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;
    TextInputEditText mEditTextNumberC, mEditTextDescriptionC, mEditTextAmountC;
    FloatingActionButton mFabClearC, mFabAddC;
    AuthProvider mAuthProvider;
    ConsumptionProvider mConsumptionProvider;
    CollectionsProvider mCollectionsProviderForNumbers;
    ProgressDialog mProgressDialog, mProgressDialogGetting;
    String mExtraIdConsumptionUpdate, mExtraConsumptionTitle;
    ArrayList<Long> mNumbersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumption_form);
        coordinatorLayout = findViewById(R.id.coordinatorConsumption);
        mEditTextNumberC = findViewById(R.id.textInputNumberC);
        mEditTextDescriptionC = findViewById(R.id.textInputDescriptionC);
        mEditTextAmountC = findViewById(R.id.textInputAmountC);
        mFabClearC = findViewById(R.id.fabClearC);
        mFabAddC = findViewById(R.id.fabAddC);
        mAuthProvider = new AuthProvider();
        mConsumptionProvider = new ConsumptionProvider();
        mCollectionsProviderForNumbers = new CollectionsProvider(this, "Consumption");
        mNumbersList = new ArrayList<>();
        mExtraIdConsumptionUpdate = getIntent().getStringExtra("idConsumptionUpdate");
        mExtraConsumptionTitle = getIntent().getStringExtra("consumptionTitle");
        if (!TextUtils.isEmpty(mExtraConsumptionTitle)) {
            setTitle("Editar registro " + mExtraConsumptionTitle);
        } else {
            setTitle("Nuevo material de consumo");
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
        mCollectionsProviderForNumbers.getNumbersByTeacher(mAuthProvider.getUid(), coordinatorLayout, mNumbersList);
        validateFieldsAsYouType(mEditTextNumberC, "El número es obligatorio");
        validateFieldsAsYouType(mEditTextDescriptionC, "La descripción es obligatoria");
        validateFieldsAsYouType(mEditTextAmountC, "La cantidad es obligatoria");
        mFabClearC.setOnClickListener(v -> cleanForm());
        mFabAddC.setOnClickListener(v -> addOrEditConsumables());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent().getBooleanExtra("consumptionSelect", false)) {
            getConsumption();
            mFabAddC.setImageResource(R.drawable.ic_edit);
        }
    }

    private void addOrEditConsumables() {
        if (getIntent().getBooleanExtra("consumptionSelect", false)) {
            String numberField = Objects.requireNonNull(mEditTextNumberC.getText()).toString().trim();
            String description = Objects.requireNonNull(mEditTextDescriptionC.getText()).toString().trim();
            String amountField = Objects.requireNonNull(mEditTextAmountC.getText()).toString().trim();
            if (!TextUtils.isEmpty(numberField)) {
                long number = Long.parseLong(numberField);
                if (number != 0) {
                    if (!TextUtils.isEmpty(description)) {
                        if (!TextUtils.isEmpty(amountField)) {
                            long amount = Long.parseLong(amountField);
                            if (amount != 0) {
                                if (mNumbersList != null && !mNumbersList.isEmpty()) {
                                    for (Long aLong : mNumbersList) {
                                        if (aLong == number) {
                                            Snackbar.make(coordinatorLayout, "Ya existe un registro con ese número", Snackbar.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                }
                                Consumption consumption = new Consumption();
                                consumption.setId(mExtraIdConsumptionUpdate);
                                consumption.setNumber(number);
                                consumption.setDescription(description);
                                consumption.setAmount(amount);
                                consumption.setTimestamp(new Date().getTime());
                                updateInfo(consumption);
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
                    Snackbar.make(coordinatorLayout, "El número no puede ser 0", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(coordinatorLayout, "El número es obligatorio", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            String numberField = Objects.requireNonNull(mEditTextNumberC.getText()).toString().trim();
            String description = Objects.requireNonNull(mEditTextDescriptionC.getText()).toString().trim();
            String amountField = Objects.requireNonNull(mEditTextAmountC.getText()).toString().trim();
            if (!TextUtils.isEmpty(numberField)) {
                long number = Long.parseLong(numberField);
                if (number != 0) {
                    if (!TextUtils.isEmpty(description)) {
                        if (!TextUtils.isEmpty(amountField)) {
                            long amount = Long.parseLong(amountField);
                            if (amount != 0) {
                                if (mNumbersList != null && !mNumbersList.isEmpty()) {
                                    for (Long aLong : mNumbersList) {
                                        if (aLong == number) {
                                            Snackbar.make(coordinatorLayout, "Ya existe un registro con ese número", Snackbar.LENGTH_SHORT).show();
                                            return;
                                        }
                                    }
                                }
                                Consumption consumption = new Consumption();
                                consumption.setNumber(number);
                                consumption.setDescription(description);
                                consumption.setAmount(amount);
                                consumption.setIdTeacher(mAuthProvider.getUid());
                                consumption.setTimestamp(new Date().getTime());
                                saveInfo(consumption);
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
                    Snackbar.make(coordinatorLayout, "El número no puede ser 0", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(coordinatorLayout, "El número es obligatorio", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void getConsumption() {
        mProgressDialogGetting.show();
        mConsumptionProvider.getConsumptionById(mExtraIdConsumptionUpdate).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("number")) {
                    long number = documentSnapshot.getLong("number");
                    mEditTextNumberC.setText(String.valueOf(number));
                    if (mNumbersList != null && !mNumbersList.isEmpty()) {
                        for (int i = 0; i < mNumbersList.size(); i++) {
                            if (mNumbersList.get(i) == number) {
                                mNumbersList.remove(i);
                                break;
                            }
                        }
                    }
                }
                if (documentSnapshot.contains("description")) {
                    String description = documentSnapshot.getString("description");
                    mEditTextDescriptionC.setText(description);
                }
                if (documentSnapshot.contains("amount")) {
                    long amount = documentSnapshot.getLong("amount");
                    mEditTextAmountC.setText(String.valueOf(amount));
                }
            }
            mProgressDialogGetting.dismiss();
        });
    }

    private void saveInfo(Consumption consumption) {
        mProgressDialog.show();
        mConsumptionProvider.save(consumption).addOnCompleteListener(task -> {
            mProgressDialog.dismiss();
            if (task.isSuccessful()) {
                finish();
                Toast.makeText(this, "Registro creado", Toast.LENGTH_SHORT).show();
            } else {
                Snackbar.make(coordinatorLayout, "Error al crear el registro", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void updateInfo(Consumption consumption) {
        mProgressDialog.show();
        mConsumptionProvider.update(consumption).addOnCompleteListener(task -> {
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
        mEditTextNumberC.setText(null);
        mEditTextDescriptionC.setText(null);
        mEditTextAmountC.setText(null);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}