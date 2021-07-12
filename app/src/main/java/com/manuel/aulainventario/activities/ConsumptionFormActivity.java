package com.manuel.aulainventario.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.manuel.aulainventario.R;
import com.manuel.aulainventario.models.Consumption;
import com.manuel.aulainventario.providers.AuthProvider;
import com.manuel.aulainventario.providers.ConsumptionProvider;

import java.util.Date;
import java.util.Objects;

import static com.manuel.aulainventario.utils.Validations.validateFieldsAsYouType;

public class ConsumptionFormActivity extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;
    TextInputEditText mEditTextNumberC, mEditTextDescriptionC, mEditTextAmountC;
    MaterialButton mButtonClearC, mButtonAddC;
    AuthProvider mAuthProvider;
    ConsumptionProvider mConsumptionProvider;
    ProgressDialog mProgressDialog;
    String mExtraIdConsumptionUpdate, mExtraConsumptionTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_consumption_form);
        coordinatorLayout = findViewById(R.id.coordinatorConsumption);
        mEditTextNumberC = findViewById(R.id.textInputNumberC);
        mEditTextDescriptionC = findViewById(R.id.textInputDescriptionC);
        mEditTextAmountC = findViewById(R.id.textInputAmountC);
        mButtonClearC = findViewById(R.id.btnClearC);
        mButtonAddC = findViewById(R.id.btnAddC);
        mAuthProvider = new AuthProvider();
        mConsumptionProvider = new ConsumptionProvider();
        mExtraIdConsumptionUpdate = getIntent().getStringExtra("idConsumptionUpdate");
        mExtraConsumptionTitle = getIntent().getStringExtra("consumptionTitle");
        if (mExtraConsumptionTitle != null && !mExtraConsumptionTitle.isEmpty()) {
            setTitle("Editar registro " + mExtraConsumptionTitle);
        } else {
            setTitle("Nuevo material de consumo");
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Guardando registro...");
        mProgressDialog.setMessage("Por favor, espere un momento");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        validateFieldsAsYouType(mEditTextNumberC, "El número es obligatorio");
        validateFieldsAsYouType(mEditTextDescriptionC, "La descripción es obligatoria");
        validateFieldsAsYouType(mEditTextAmountC, "La cantidad es obligatoria");
        getDataFromAdapter();
        mButtonClearC.setOnClickListener(v -> cleanForm());
        mButtonAddC.setOnClickListener(v -> {
            if (getIntent().getBooleanExtra("consumptionSelect", false)) {
                //EDITAR REGISTRO
                String number = Objects.requireNonNull(mEditTextNumberC.getText()).toString().trim();
                String description = Objects.requireNonNull(mEditTextDescriptionC.getText()).toString().trim();
                String amount = Objects.requireNonNull(mEditTextAmountC.getText()).toString().trim();
                if (!number.isEmpty()) {
                    if (!description.isEmpty()) {
                        if (!amount.isEmpty()) {
                            Consumption consumption = new Consumption();
                            consumption.setId(mExtraIdConsumptionUpdate);
                            consumption.setNumber(Long.parseLong(number));
                            consumption.setDescription(description);
                            consumption.setAmount(Long.parseLong(amount));
                            consumption.setTimestamp(new Date().getTime());
                            updateInfo(consumption);
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
                String number = Objects.requireNonNull(mEditTextNumberC.getText()).toString().trim();
                String description = Objects.requireNonNull(mEditTextDescriptionC.getText()).toString().trim();
                String amount = Objects.requireNonNull(mEditTextAmountC.getText()).toString().trim();
                if (!number.isEmpty()) {
                    if (!description.isEmpty()) {
                        if (!amount.isEmpty()) {
                            Consumption consumption = new Consumption();
                            consumption.setNumber(Long.parseLong(number));
                            consumption.setDescription(description);
                            consumption.setAmount(Long.parseLong(amount));
                            consumption.setIdTeacher(mAuthProvider.getUid());
                            consumption.setTimestamp(new Date().getTime());
                            saveInfo(consumption);
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
        if (getIntent().getBooleanExtra("consumptionSelect", false)) {
            getConsumption();
            mButtonAddC.setIconResource(R.drawable.ic_edit);
            mButtonAddC.setText("Editar");
        }
    }

    private void getConsumption() {
        mConsumptionProvider.getConsumptionById(mExtraIdConsumptionUpdate).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("number")) {
                    long number = documentSnapshot.getLong("number");
                    mEditTextNumberC.setText(String.valueOf(number));
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
        });
    }

    private void saveInfo(Consumption consumption) {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
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
        if (mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
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

    @SuppressLint("SetTextI18n")
    private void cleanForm() {
        mEditTextNumberC.setText(null);
        mEditTextDescriptionC.setText(null);
        mEditTextAmountC.setText(null);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}