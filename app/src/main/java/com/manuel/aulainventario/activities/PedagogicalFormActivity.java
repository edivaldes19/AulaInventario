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
import com.manuel.aulainventario.models.Pedagogical;
import com.manuel.aulainventario.providers.AuthProvider;
import com.manuel.aulainventario.providers.CollectionsProvider;
import com.manuel.aulainventario.providers.PedagogicalProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static com.manuel.aulainventario.utils.MyTools.getLastPositionOfASpinner;
import static com.manuel.aulainventario.utils.MyTools.validateFieldsAsYouType;

public class PedagogicalFormActivity extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;
    TextInputEditText mEditTextNumberP, mEditTextDescriptionP, mEditTextAmountP;
    MaterialTextView mTextViewConditionSelectedP;
    Spinner mSpinnerP;
    FloatingActionButton mFabClearP, mFabAddP;
    AuthProvider mAuthProvider;
    CollectionsProvider mCollectionsProvider, mCollectionsProviderForNumbers;
    PedagogicalProvider mPedagogicalProvider;
    ProgressDialog mProgressDialog, mProgressDialogGetting;
    String mExtraIdPedagogicalUpdate, mExtraPedagogicalTitle;
    ArrayList<String> mConditionsList;
    ArrayList<Long> mNumbersList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedagogical_form);
        coordinatorLayout = findViewById(R.id.coordinatorPedagogical);
        mEditTextNumberP = findViewById(R.id.textInputNumberP);
        mEditTextDescriptionP = findViewById(R.id.textInputDescriptionP);
        mEditTextAmountP = findViewById(R.id.textInputAmountP);
        mTextViewConditionSelectedP = findViewById(R.id.textViewConditionSelectedP);
        mSpinnerP = findViewById(R.id.spinnerConditionP);
        mFabClearP = findViewById(R.id.fabClearP);
        mFabAddP = findViewById(R.id.fabAddP);
        mAuthProvider = new AuthProvider();
        mCollectionsProvider = new CollectionsProvider(this, "Conditions");
        mCollectionsProviderForNumbers = new CollectionsProvider(this, "Pedagogical");
        mPedagogicalProvider = new PedagogicalProvider();
        mConditionsList = new ArrayList<>();
        mNumbersList = new ArrayList<>();
        mExtraIdPedagogicalUpdate = getIntent().getStringExtra("idPedagogicalUpdate");
        mExtraPedagogicalTitle = getIntent().getStringExtra("pedagogicalTitle");
        if (!TextUtils.isEmpty(mExtraPedagogicalTitle)) {
            setTitle("Editar registro " + mExtraPedagogicalTitle);
        } else {
            setTitle("Nuevo técnico pedagógico");
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
        mCollectionsProvider.getAllTheDocumentsInACollectionAndSetTheAdapter(coordinatorLayout, mConditionsList, "condition", mSpinnerP, "Estado: ", mTextViewConditionSelectedP, "Error al obtener los estados");
        mCollectionsProviderForNumbers.getNumbersByTeacher(mAuthProvider.getUid(), coordinatorLayout, mNumbersList);
        validateFieldsAsYouType(mEditTextNumberP, "El número es obligatorio");
        validateFieldsAsYouType(mEditTextDescriptionP, "La descripción es obligatoria");
        validateFieldsAsYouType(mEditTextAmountP, "La cantidad es obligatoria");
        mFabClearP.setOnClickListener(v -> cleanForm());
        mFabAddP.setOnClickListener(v -> addOrEditPedagogicalTechnician());
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (getIntent().getBooleanExtra("pedagogicalSelect", false)) {
            getPedagogical();
            mFabAddP.setImageResource(R.drawable.ic_edit);
        }
    }

    private void addOrEditPedagogicalTechnician() {
        if (getIntent().getBooleanExtra("pedagogicalSelect", false)) {
            String numberField = Objects.requireNonNull(mEditTextNumberP.getText()).toString().trim();
            String description = Objects.requireNonNull(mEditTextDescriptionP.getText()).toString().trim();
            String amountField = Objects.requireNonNull(mEditTextAmountP.getText()).toString().trim();
            String condition = mSpinnerP.getSelectedItem().toString().trim();
            if (!TextUtils.isEmpty(numberField)) {
                long number = Long.parseLong(numberField);
                if (number != 0) {
                    if (!TextUtils.isEmpty(description)) {
                        if (!TextUtils.isEmpty(amountField)) {
                            long amount = Long.parseLong(amountField);
                            if (amount != 0) {
                                if (!TextUtils.isEmpty(condition)) {
                                    if (mNumbersList != null && !mNumbersList.isEmpty()) {
                                        for (Long aLong : mNumbersList) {
                                            if (aLong == number) {
                                                Snackbar.make(coordinatorLayout, "Ya existe un registro con ese número", Snackbar.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                    }
                                    Pedagogical pedagogical = new Pedagogical();
                                    pedagogical.setId(mExtraIdPedagogicalUpdate);
                                    pedagogical.setNumber(number);
                                    pedagogical.setDescription(description);
                                    pedagogical.setAmount(amount);
                                    pedagogical.setCondition(condition);
                                    pedagogical.setTimestamp(new Date().getTime());
                                    updateInfo(pedagogical);
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
            String numberField = Objects.requireNonNull(mEditTextNumberP.getText()).toString().trim();
            String description = Objects.requireNonNull(mEditTextDescriptionP.getText()).toString().trim();
            String amountField = Objects.requireNonNull(mEditTextAmountP.getText()).toString().trim();
            String condition = mSpinnerP.getSelectedItem().toString().trim();
            if (!TextUtils.isEmpty(numberField)) {
                long number = Long.parseLong(numberField);
                if (number != 0) {
                    if (!TextUtils.isEmpty(description)) {
                        if (!TextUtils.isEmpty(amountField)) {
                            long amount = Long.parseLong(amountField);
                            if (amount != 0) {
                                if (!TextUtils.isEmpty(condition)) {
                                    if (mNumbersList != null && !mNumbersList.isEmpty()) {
                                        for (Long aLong : mNumbersList) {
                                            if (aLong == number) {
                                                Snackbar.make(coordinatorLayout, "Ya existe un registro con ese número", Snackbar.LENGTH_SHORT).show();
                                                return;
                                            }
                                        }
                                    }
                                    Pedagogical pedagogical = new Pedagogical();
                                    pedagogical.setNumber(number);
                                    pedagogical.setDescription(description);
                                    pedagogical.setAmount(amount);
                                    pedagogical.setCondition(condition);
                                    pedagogical.setIdTeacher(mAuthProvider.getUid());
                                    pedagogical.setTimestamp(new Date().getTime());
                                    saveInfo(pedagogical);
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

    private void getPedagogical() {
        mProgressDialogGetting.show();
        mPedagogicalProvider.getPedagogicalById(mExtraIdPedagogicalUpdate).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("number")) {
                    long number = documentSnapshot.getLong("number");
                    mEditTextNumberP.setText(String.valueOf(number));
                    if (mNumbersList != null && !mNumbersList.isEmpty()) {
                        for (int i = 0; i < mNumbersList.size(); i++) {
                            if (mNumbersList.get(i) == number) {
                                mNumbersList.remove(i);
                                break;
                            }
                        }
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
                        mSpinnerP.setSelection(getLastPositionOfASpinner(mSpinnerP, condition));
                    }
                }
                mProgressDialogGetting.dismiss();
            }
        });
    }

    private void saveInfo(Pedagogical pedagogical) {
        mProgressDialog.show();
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
        mProgressDialog.show();
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

    private void cleanForm() {
        mEditTextNumberP.setText(null);
        mEditTextDescriptionP.setText(null);
        mEditTextAmountP.setText(null);
        mSpinnerP.setSelection(0);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }
}