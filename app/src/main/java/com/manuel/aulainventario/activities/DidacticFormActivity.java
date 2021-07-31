package com.manuel.aulainventario.activities;

import static com.manuel.aulainventario.utils.MyTools.compareDataNumbers;
import static com.manuel.aulainventario.utils.MyTools.deleteCurrentInformationNumbers;
import static com.manuel.aulainventario.utils.MyTools.setPositionByCondition;
import static com.manuel.aulainventario.utils.MyTools.validateFieldsAsYouType;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.manuel.aulainventario.R;
import com.manuel.aulainventario.models.Didactic;
import com.manuel.aulainventario.providers.AuthProvider;
import com.manuel.aulainventario.providers.CollectionsProvider;
import com.manuel.aulainventario.providers.DidacticProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

public class DidacticFormActivity extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;
    TextInputEditText mEditTextNumberD, mEditTextDescriptionD, mEditTextAmountD;
    MaterialTextView mTextViewConditionSelectedD;
    Spinner mSpinnerD;
    MaterialButton mButtonClearD, mButtonAddD;
    AuthProvider mAuthProvider;
    CollectionsProvider mCollectionsProvider, mCollectionsProviderForNumbers;
    DidacticProvider mDidacticProvider;
    ProgressDialog mProgressDialog, mProgressDialogGetting;
    String mExtraIdDidacticUpdate, mExtraDidacticTitle;
    ArrayList<String> mConditionsList;
    ArrayList<Long> mNumbersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_didactic_form);
        coordinatorLayout = findViewById(R.id.coordinatorDidactic);
        mEditTextNumberD = findViewById(R.id.textInputNumberD);
        mEditTextDescriptionD = findViewById(R.id.textInputDescriptionD);
        mEditTextAmountD = findViewById(R.id.textInputAmountD);
        mTextViewConditionSelectedD = findViewById(R.id.textViewConditionSelectedD);
        mSpinnerD = findViewById(R.id.spinnerConditionD);
        mButtonClearD = findViewById(R.id.btnCleanFormDidactic);
        mButtonAddD = findViewById(R.id.btnAddDidactic);
        mAuthProvider = new AuthProvider();
        mCollectionsProvider = new CollectionsProvider(this, "Conditions");
        mCollectionsProviderForNumbers = new CollectionsProvider(this, "Didactic");
        mDidacticProvider = new DidacticProvider();
        mConditionsList = new ArrayList<>();
        mNumbersList = new ArrayList<>();
        mExtraIdDidacticUpdate = getIntent().getStringExtra("idDidacticUpdate");
        mExtraDidacticTitle = getIntent().getStringExtra("didacticTitle");
        if (!TextUtils.isEmpty(mExtraDidacticTitle)) {
            setTitle("Editar registro " + mExtraDidacticTitle);
        } else {
            setTitle("Nuevo material didáctico");
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
        mCollectionsProvider.getAllTheDocumentsInACollectionAndSetTheAdapter(coordinatorLayout, mConditionsList, "condition", mSpinnerD, "Estado: ", mTextViewConditionSelectedD, "Error al obtener los estados");
        mCollectionsProviderForNumbers.getNumbersByTeacher(mAuthProvider.getUid(), coordinatorLayout, mNumbersList);
        validateFieldsAsYouType(mEditTextNumberD, "El número es obligatorio");
        validateFieldsAsYouType(mEditTextDescriptionD, "La descripción es obligatoria");
        validateFieldsAsYouType(mEditTextAmountD, "La cantidad es obligatoria");
        mButtonClearD.setOnClickListener(v -> cleanForm());
        mButtonAddD.setOnClickListener(v -> addOrEditTeachingMaterials());
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent().getBooleanExtra("didacticSelect", false)) {
            getDidactic();
            mButtonAddD.setIconResource(R.drawable.ic_edit);
            mButtonAddD.setText("Editar");
        }
    }

    private void addOrEditTeachingMaterials() {
        if (getIntent().getBooleanExtra("didacticSelect", false)) {
            String numberField = Objects.requireNonNull(mEditTextNumberD.getText()).toString().trim();
            String description = Objects.requireNonNull(mEditTextDescriptionD.getText()).toString().trim();
            String amountField = Objects.requireNonNull(mEditTextAmountD.getText()).toString().trim();
            String condition = mSpinnerD.getSelectedItem().toString().trim();
            if (!TextUtils.isEmpty(numberField)) {
                long number = Long.parseLong(numberField);
                if (number != 0) {
                    if (!TextUtils.isEmpty(description)) {
                        if (!TextUtils.isEmpty(amountField)) {
                            long amount = Long.parseLong(amountField);
                            if (amount != 0) {
                                if (!TextUtils.isEmpty(condition)) {
                                    compareDataNumbers(mNumbersList, number, coordinatorLayout, "Ya existe un registro con ese número");
                                    Didactic didactic = new Didactic();
                                    didactic.setId(mExtraIdDidacticUpdate);
                                    didactic.setNumber(number);
                                    didactic.setDescription(description);
                                    didactic.setAmount(amount);
                                    didactic.setCondition(condition);
                                    didactic.setTimestamp(new Date().getTime());
                                    updateInfo(didactic);
                                } else {
                                    Snackbar.make(coordinatorLayout, "El estado es obligatorio", Snackbar.LENGTH_SHORT).show();
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
                    Snackbar.make(coordinatorLayout, "El número no puede ser 0", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(coordinatorLayout, "El número es obligatorio", Snackbar.LENGTH_SHORT).show();
            }
        } else {
            String numberField = Objects.requireNonNull(mEditTextNumberD.getText()).toString().trim();
            String description = Objects.requireNonNull(mEditTextDescriptionD.getText()).toString().trim();
            String amountField = Objects.requireNonNull(mEditTextAmountD.getText()).toString().trim();
            String condition = mSpinnerD.getSelectedItem().toString().trim();
            if (!TextUtils.isEmpty(numberField)) {
                long number = Long.parseLong(numberField);
                if (number != 0) {
                    if (!TextUtils.isEmpty(description)) {
                        if (!TextUtils.isEmpty(amountField)) {
                            long amount = Long.parseLong(amountField);
                            if (amount != 0) {
                                if (!TextUtils.isEmpty(condition)) {
                                    compareDataNumbers(mNumbersList, number, coordinatorLayout, "Ya existe un registro con ese número");
                                    Didactic didactic = new Didactic();
                                    didactic.setNumber(number);
                                    didactic.setDescription(description);
                                    didactic.setAmount(amount);
                                    didactic.setCondition(condition);
                                    didactic.setIdTeacher(mAuthProvider.getUid());
                                    didactic.setTimestamp(new Date().getTime());
                                    saveInfo(didactic);
                                } else {
                                    Snackbar.make(coordinatorLayout, "El estado es obligatorio", Snackbar.LENGTH_SHORT).show();
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
                    Snackbar.make(coordinatorLayout, "El número no puede ser 0", Snackbar.LENGTH_SHORT).show();
                }
            } else {
                Snackbar.make(coordinatorLayout, "El número es obligatorio", Snackbar.LENGTH_SHORT).show();
            }
        }
    }

    private void getDidactic() {
        mProgressDialogGetting.show();
        mDidacticProvider.getDidacticById(mExtraIdDidacticUpdate).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("number")) {
                    long number = documentSnapshot.getLong("number");
                    mEditTextNumberD.setText(String.valueOf(number));
                    deleteCurrentInformationNumbers(mNumbersList, number);
                }
                if (documentSnapshot.contains("description")) {
                    String description = documentSnapshot.getString("description");
                    mEditTextDescriptionD.setText(description);
                }
                if (documentSnapshot.contains("amount")) {
                    long amount = documentSnapshot.getLong("amount");
                    mEditTextAmountD.setText(String.valueOf(amount));
                }
                if (documentSnapshot.contains("condition")) {
                    String condition = documentSnapshot.getString("condition");
                    setPositionByCondition(mSpinnerD, condition);
                }
            }
            mProgressDialogGetting.dismiss();
        });
    }

    private void saveInfo(Didactic didactic) {
        mProgressDialog.show();
        mDidacticProvider.save(didactic).addOnCompleteListener(task -> {
            mProgressDialog.dismiss();
            if (task.isSuccessful()) {
                finish();
                Toast.makeText(this, "Registro creado", Toast.LENGTH_SHORT).show();
            } else {
                Snackbar.make(coordinatorLayout, "Error al crear el registro", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void updateInfo(Didactic didactic) {
        mProgressDialog.show();
        mDidacticProvider.update(didactic).addOnCompleteListener(task -> {
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
        mEditTextNumberD.setText(null);
        mEditTextDescriptionD.setText(null);
        mEditTextAmountD.setText(null);
        mSpinnerD.setSelection(0);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}