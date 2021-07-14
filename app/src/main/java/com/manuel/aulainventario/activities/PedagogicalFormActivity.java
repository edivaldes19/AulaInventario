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
import com.manuel.aulainventario.models.Pedagogical;
import com.manuel.aulainventario.providers.AuthProvider;
import com.manuel.aulainventario.providers.CollectionsProvider;
import com.manuel.aulainventario.providers.PedagogicalProvider;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import static com.manuel.aulainventario.utils.Validations.validateFieldsAsYouType;

public class PedagogicalFormActivity extends AppCompatActivity {
    CoordinatorLayout coordinatorLayout;
    TextInputEditText mEditTextNumberP, mEditTextDescriptionP, mEditTextAmountP;
    MaterialTextView mTextViewConditionSelectedP;
    Spinner mSpinnerP;
    MaterialButton mButtonClearP, mButtonAddP;
    AuthProvider mAuthProvider;
    CollectionsProvider mCollectionsProvider;
    PedagogicalProvider mPedagogicalProvider;
    ProgressDialog mProgressDialog;
    String mExtraIdPedagogicalUpdate, mExtraPedagogicalTitle;
    ArrayList<String> mConditionsList;

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
        mButtonClearP = findViewById(R.id.btnClearP);
        mButtonAddP = findViewById(R.id.btnAddP);
        mAuthProvider = new AuthProvider();
        mCollectionsProvider = new CollectionsProvider(this, "Conditions");
        mPedagogicalProvider = new PedagogicalProvider();
        mConditionsList = new ArrayList<>();
        mExtraIdPedagogicalUpdate = getIntent().getStringExtra("idPedagogicalUpdate");
        mExtraPedagogicalTitle = getIntent().getStringExtra("pedagogicalTitle");
        if (mExtraPedagogicalTitle != null && !mExtraPedagogicalTitle.isEmpty()) {
            setTitle("Editar registro " + mExtraPedagogicalTitle);
        } else {
            setTitle("Nuevo técnico pedagógico");
        }
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Guardando registro...");
        mProgressDialog.setMessage("Por favor, espere un momento");
        mProgressDialog.setCancelable(false);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        validateFieldsAsYouType(mEditTextNumberP, "El número es obligatorio");
        validateFieldsAsYouType(mEditTextDescriptionP, "La descripción es obligatoria");
        validateFieldsAsYouType(mEditTextAmountP, "La cantidad es obligatoria");
        mCollectionsProvider.getAllTheDocumentsInACollectionAndSetTheAdapter(coordinatorLayout, mConditionsList, "condition", mSpinnerP, "Estado: ", mTextViewConditionSelectedP, "Error al obtener los estados");
        getDataFromAdapter();
        mButtonClearP.setOnClickListener(v -> cleanForm());
        mButtonAddP.setOnClickListener(v -> {
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
            mButtonAddP.setIconResource(R.drawable.ic_edit);
            mButtonAddP.setText("Editar");
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
                    for (int i = 0; i < mSpinnerP.getCount(); i++) {
                        if (mSpinnerP.getItemAtPosition(i).toString().equalsIgnoreCase(condition)) {
                            mSpinnerP.setSelection(i);
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