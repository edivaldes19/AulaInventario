package com.manuel.aulainventario.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Spinner;
import android.widget.Toast;

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

import static com.manuel.aulainventario.utils.Validations.validateFieldsAsYouType;

public class DidacticFormActivity extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;
    TextInputEditText mEditTextNumberD, mEditTextDescriptionD, mEditTextAmountD;
    MaterialTextView mTextViewConditionSelectedD;
    Spinner mSpinnerD;
    MaterialButton mButtonClearD, mButtonAddD;
    AuthProvider mAuthProvider;
    CollectionsProvider mCollectionsProvider;
    DidacticProvider mDidacticProvider;
    ProgressDialog mProgressDialog;
    String mExtraIdDidacticUpdate, mExtraDidacticTitle;
    ArrayList<String> mConditionsList;

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
        mButtonClearD = findViewById(R.id.btnClearD);
        mButtonAddD = findViewById(R.id.btnAddD);
        mAuthProvider = new AuthProvider();
        mDidacticProvider = new DidacticProvider();
        mCollectionsProvider = new CollectionsProvider(this, "Conditions");
        mConditionsList = new ArrayList<>();
        mExtraIdDidacticUpdate = getIntent().getStringExtra("idDidacticUpdate");
        mExtraDidacticTitle = getIntent().getStringExtra("didacticTitle");
        if (mExtraDidacticTitle != null && !mExtraDidacticTitle.isEmpty()) {
            setTitle("Editar registro " + mExtraDidacticTitle);
        } else {
            setTitle("Nuevo material didáctico");
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Guardando registro...");
        mProgressDialog.setMessage("Por favor, espere un momento");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        validateFieldsAsYouType(mEditTextNumberD, "El número es obligatorio");
        validateFieldsAsYouType(mEditTextDescriptionD, "La descripción es obligatoria");
        validateFieldsAsYouType(mEditTextAmountD, "La cantidad es obligatoria");
        mCollectionsProvider.getAllTheDocumentsInACollectionAndSetTheAdapter(coordinatorLayout, mConditionsList, "condition", mSpinnerD, "Estado: ", mTextViewConditionSelectedD, "Error al obtener los estados");
        getDataFromAdapter();
        mButtonClearD.setOnClickListener(v -> cleanForm());
        mButtonAddD.setOnClickListener(v -> {
            if (getIntent().getBooleanExtra("didacticSelect", false)) {
                //EDITAR REGISTRO
                String number = Objects.requireNonNull(mEditTextNumberD.getText()).toString().trim();
                String description = Objects.requireNonNull(mEditTextDescriptionD.getText()).toString().trim();
                String amount = Objects.requireNonNull(mEditTextAmountD.getText()).toString().trim();
                String condition = mSpinnerD.getSelectedItem().toString().trim();
                if (!number.isEmpty()) {
                    if (!description.isEmpty()) {
                        if (!amount.isEmpty()) {
                            if (!condition.isEmpty()) {
                                Didactic didactic = new Didactic();
                                didactic.setId(mExtraIdDidacticUpdate);
                                didactic.setNumber(Long.parseLong(number));
                                didactic.setDescription(description);
                                didactic.setAmount(Long.parseLong(amount));
                                didactic.setCondition(condition);
                                didactic.setTimestamp(new Date().getTime());
                                updateInfo(didactic);
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
                String number = Objects.requireNonNull(mEditTextNumberD.getText()).toString().trim();
                String description = Objects.requireNonNull(mEditTextDescriptionD.getText()).toString().trim();
                String amount = Objects.requireNonNull(mEditTextAmountD.getText()).toString().trim();
                String condition = mSpinnerD.getSelectedItem().toString().trim();
                if (!number.isEmpty()) {
                    if (!description.isEmpty()) {
                        if (!amount.isEmpty()) {
                            if (!condition.isEmpty()) {
                                Didactic didactic = new Didactic();
                                didactic.setNumber(Long.parseLong(number));
                                didactic.setDescription(description);
                                didactic.setAmount(Long.parseLong(amount));
                                didactic.setCondition(condition);
                                didactic.setIdTeacher(mAuthProvider.getUid());
                                didactic.setTimestamp(new Date().getTime());
                                saveInfo(didactic);
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
        if (getIntent().getBooleanExtra("didacticSelect", false)) {
            getDidactic();
            mButtonAddD.setIconResource(R.drawable.ic_edit);
            mButtonAddD.setText("Editar");
        }
    }

    private void getDidactic() {
        mDidacticProvider.getDidacticById(mExtraIdDidacticUpdate).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("number")) {
                    long number = documentSnapshot.getLong("number");
                    mEditTextNumberD.setText(String.valueOf(number));
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
                    mTextViewConditionSelectedD.setText(condition);
                    for (int i = 0; i < mSpinnerD.getCount(); i++) {
                        if (mSpinnerD.getItemAtPosition(i).toString().equalsIgnoreCase(condition)) {
                            mSpinnerD.setSelection(i);
                            break;
                        }
                    }
                }
            }
        });
    }

    private void saveInfo(Didactic didactic) {
        if (mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
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
        if (mProgressDialog.isShowing()) {
            mProgressDialog.show();
        }
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

    @SuppressLint("SetTextI18n")
    private void cleanForm() {
        mEditTextNumberD.setText(null);
        mEditTextDescriptionD.setText(null);
        mEditTextAmountD.setText(null);
        mTextViewConditionSelectedD.setText("Estado: ");
        mSpinnerD.setSelection(0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}