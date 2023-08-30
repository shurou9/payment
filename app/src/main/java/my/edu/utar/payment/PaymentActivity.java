package my.edu.utar.payment;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.Arrays;

public class PaymentActivity extends Activity {

    private ImageButton btnExit;
    private EditText editCardNumber, editExpiration, editCVV, editZipCode;
    private Spinner spinnerCountry;
    private Button btnSubmit;
    private CheckBox checkBoxSaveForFutureUse;
    private SharedPreferences sharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnExit = findViewById(R.id.btnExit);
        editCardNumber = findViewById(R.id.editCardNumber);

        editExpiration = findViewById(R.id.editExpiration);
        editCVV = findViewById(R.id.editCVC);
        editZipCode = findViewById(R.id.editZipCode);
        spinnerCountry = findViewById(R.id.spinnerCountry);
        checkBoxSaveForFutureUse = findViewById(R.id.checkBoxSaveForFutureUse);
        btnSubmit = findViewById(R.id.btnSubmit);
        sharedPreferences = getSharedPreferences("PaymentData", MODE_PRIVATE);

        btnExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Finish the current activity and go back
            }
        });


        editExpiration.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Not used
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // Insert '/' after two digits
                if (charSequence.length() == 2 && i2 == 1) {
                    int month = Integer.parseInt(charSequence.toString());
                    if (month > 12) {
                        Toast.makeText(PaymentActivity.this,
                                "Invalid month. Please enter a value between 01 and 12.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }
                    editExpiration.setText(charSequence + "/");
                    editExpiration.setSelection(editExpiration.getText().length());
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // Not used
            }
        });

        // Populate spinner with country options
        String[] countries = {"United States", "Canada", "Malaysia", "Singapore"
                , "HongKong", "Japan", "Thailand", "German", "China", "Australia", "Others"};
        Arrays.sort(countries);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, countries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCountry.setAdapter(adapter);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSubmitClick(v);
            }
        });
    }

    public void onSubmitClick(View view) {
        String cardNumber = editCardNumber.getText().toString().trim();
        String expiration = editExpiration.getText().toString().trim();
        String cvv = editCVV.getText().toString().trim();
        String selectedCountry = spinnerCountry.getSelectedItem().toString();

        if (cardNumber.isEmpty() || expiration.isEmpty() || cvv.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cardNumber.length() < 16) {
            Toast.makeText(this, "Invalid card number.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (expiration.length() != 5 || !expiration.matches("\\d{2}/\\d{2}")) {
            Toast.makeText(this, "Invalid expiration date.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (cvv.length() != 3) {
            Toast.makeText(this, "Invalid CVV (use xxx).", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean paymentSuccess = simulatePaymentProcessing();

        if (paymentSuccess) {
            Toast.makeText(this, "Payment successful!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Payment failed. Please try again.",
                    Toast.LENGTH_SHORT).show();
        }

        String zipCode = editZipCode.getText().toString().trim();
        boolean saveForFutureUse = checkBoxSaveForFutureUse.isChecked();

        if (zipCode.isEmpty()) {
            Toast.makeText(this, "Please enter ZIP Code.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (saveForFutureUse) {
            savePaymentData(cardNumber, expiration, cvv, selectedCountry, zipCode);
        }
    }

    private void savePaymentData(String cardNumber, String expiration, String cvv,
                                 String country, String zipCode) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("CardNumber", cardNumber);
        editor.putString("Expiration", expiration);
        editor.putString("CVV", cvv);
        editor.putString("Country", country);
        editor.putString("ZipCode", zipCode);
        editor.apply();
        Toast.makeText(this, "Payment information saved for future use.",
                Toast.LENGTH_SHORT).show();
    }

    private boolean simulatePaymentProcessing() {
        // Simulate payment processing logic here
        // For demonstration purposes, let's assume payment is successful 80% of the time
        return Math.random() < 0.8;
    }

}