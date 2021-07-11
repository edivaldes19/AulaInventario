package com.manuel.aulainventario.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.manuel.aulainventario.R;
import com.manuel.aulainventario.providers.AuthProvider;
import com.manuel.aulainventario.providers.TeachersProvider;

import java.util.Objects;
import java.util.Properties;

import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import static com.manuel.aulainventario.utils.Validations.isEmailValid;
import static com.manuel.aulainventario.utils.Validations.validateFieldsAsYouType;

public class ContactMeActivity extends AppCompatActivity {
    public static String affair;
    CoordinatorLayout coordinatorLayout;
    ShapeableImageView mImageViewBack;
    TextInputEditText textInputUsernameForm, textInputEmailForm, textInputMessageForm;
    MaterialTextView mTextViewReasonSelected;
    RadioGroup radioGroup;
    MaterialRadioButton materialRadioButton, materialRadioButtonComplain, materialRadioButtonSuggestion;
    final String emailProject = "appsmanuel1219@gmail.com", passwordProject = "e12171922M/";
    String name, email, message;
    Session session;
    TeachersProvider mTeachersProvider;
    AuthProvider mAuthProvider;

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactme);
        coordinatorLayout = findViewById(R.id.coordinatorContactanos);
        mImageViewBack = findViewById(R.id.imageViewBack);
        textInputUsernameForm = findViewById(R.id.textInputUsernameForm);
        textInputEmailForm = findViewById(R.id.textInputEmailForm);
        textInputMessageForm = findViewById(R.id.textInputMessageForm);
        mTextViewReasonSelected = findViewById(R.id.textViewReasonSelected);
        materialRadioButtonComplain = findViewById(R.id.radioButtonComplain);
        materialRadioButtonSuggestion = findViewById(R.id.radioButtonSuggestion);
        radioGroup = findViewById(R.id.radioGroup);
        mTeachersProvider = new TeachersProvider();
        mAuthProvider = new AuthProvider();
        validateFieldsAsYouType(textInputMessageForm, "El mensaje es obligatorio");
        materialRadioButtonComplain.setOnClickListener(v -> mTextViewReasonSelected.setText(materialRadioButtonComplain.getText().toString().trim()));
        materialRadioButtonSuggestion.setOnClickListener(v -> mTextViewReasonSelected.setText(materialRadioButtonSuggestion.getText().toString().trim()));
        MaterialButton buttonSend = findViewById(R.id.btnSendForm);
        buttonSend.setOnClickListener(view -> {
            int radioID = radioGroup.getCheckedRadioButtonId();
            materialRadioButton = findViewById(radioID);
            name = Objects.requireNonNull(textInputUsernameForm.getText()).toString().trim();
            email = Objects.requireNonNull(textInputEmailForm.getText()).toString().trim();
            message = Objects.requireNonNull(textInputMessageForm.getText()).toString().trim();
            if (name.isEmpty() && !isEmailValid(email) && message.isEmpty() && (!materialRadioButtonComplain.isChecked() || !materialRadioButtonSuggestion.isChecked())) {
                Snackbar.make(view, "Complete los campos", Snackbar.LENGTH_SHORT).show();
            } else {
                if (!name.isEmpty()) {
                    if (isEmailValid(email)) {
                        if (!message.isEmpty()) {
                            if (materialRadioButtonComplain.isChecked() || materialRadioButtonSuggestion.isChecked()) {
                                affair = materialRadioButton.getText().toString().trim();
                                StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                                StrictMode.setThreadPolicy(policy);
                                Properties properties = new Properties();
                                properties.put("mail.smtp.host", "smtp.gmail.com");
                                properties.put("mail.smtp.socketFactory.port", "465");
                                properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
                                properties.put("mail.smtp.auth", "true");
                                properties.put("mail.smtp.port", "465");
                                session = Session.getDefaultInstance(properties, new Authenticator() {
                                    @Override
                                    protected PasswordAuthentication getPasswordAuthentication() {
                                        return new PasswordAuthentication(emailProject, passwordProject);
                                    }
                                });
                                try {
                                    Message message = new MimeMessage(session);
                                    message.setFrom(new InternetAddress(email, name + " (" + email + ")"));
                                    message.setSubject(affair + " de Aula Inventario");
                                    message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(emailProject));
                                    message.setContent(this.message, "text/html; charset=utf-8");
                                    new SendMail().execute(message);
                                } catch (Exception e) {
                                    Snackbar.make(view, "Error al enviar correo electrónico", Snackbar.LENGTH_SHORT).show();
                                }
                            } else {
                                Snackbar.make(view, "Debe seleccionar queja o sugerencia", Snackbar.LENGTH_SHORT).show();
                            }
                        } else {
                            Snackbar.make(view, "El mensaje es obligatorio", Snackbar.LENGTH_SHORT).show();
                        }
                    } else {
                        Snackbar.make(view, "Formato de correo electrónico inválido", Snackbar.LENGTH_SHORT).show();
                    }
                } else {
                    Snackbar.make(view, "El nombre y apellido es obligatorio", Snackbar.LENGTH_SHORT).show();
                }
            }
        });
        mImageViewBack.setOnClickListener(v -> finish());
        getTeacher();
    }

    private void getTeacher() {
        mTeachersProvider.getTeacher(mAuthProvider.getUid()).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                if (documentSnapshot.contains("teachername")) {
                    String teachername = documentSnapshot.getString("teachername");
                    textInputUsernameForm.setText(teachername);
                }
                if (documentSnapshot.contains("email")) {
                    String email = documentSnapshot.getString("email");
                    textInputEmailForm.setText(email);
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class SendMail extends AsyncTask<Message, String, String> {
        private ProgressDialog progressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = ProgressDialog.show(ContactMeActivity.this, "Enviando correo electrónico...", "Por favor, espere un momento", true, false);
        }

        @Override
        protected String doInBackground(Message... messages) {
            try {
                Transport.send(messages[0]);
                return "Éxito";
            } catch (MessagingException e) {
                e.printStackTrace();
                return "Error";
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressDialog.dismiss();
            if (s.equals("Éxito")) {
                if (ContactMeActivity.affair.equals("Queja")) {
                    Toast.makeText(ContactMeActivity.this, "Gracias por su queja", Toast.LENGTH_LONG).show();
                } else if (ContactMeActivity.affair.equals("Sugerencia")) {
                    Toast.makeText(ContactMeActivity.this, "Gracias por su sugerencia", Toast.LENGTH_LONG).show();
                }
                finish();
            } else {
                Snackbar.make(coordinatorLayout, "Error", Snackbar.LENGTH_SHORT).show();
            }
        }
    }
}