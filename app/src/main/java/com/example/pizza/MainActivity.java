
package com.example.pizza;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private SeekBar sizeSeekBar;
    private TextView sizeText;
    private RadioGroup doughGroup;
    private CheckBox cheeseBox, hamBox, mushroomBox, pineappleBox;
    private Switch deliverySwitch;
    private EditText nameEditText;
    private Button orderButton, clearButton;

    private int pizzaSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sizeSeekBar = findViewById(R.id.sizeSeekBar);
        sizeText = findViewById(R.id.sizeText);
        doughGroup = findViewById(R.id.doughGroup);
        cheeseBox = findViewById(R.id.cheeseBox);
        hamBox = findViewById(R.id.hamBox);
        mushroomBox = findViewById(R.id.mushroomBox);
        pineappleBox = findViewById(R.id.pineappleBox);
        deliverySwitch = findViewById(R.id.deliverySwitch);
        nameEditText = findViewById(R.id.nameEditText);
        orderButton = findViewById(R.id.orderButton);
        clearButton = findViewById(R.id.clearButton);

        pizzaSize = 20 + sizeSeekBar.getProgress() * 5;
        updateSizeText();

        sizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                pizzaSize = 20 + progress * 5;
                updateSizeText();
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        orderButton.setOnClickListener(v -> showOrderSummary());
        clearButton.setOnClickListener(v -> resetForm());

        SharedPreferences prefs = getSharedPreferences("pizza_prefs", MODE_PRIVATE);
        nameEditText.setText(prefs.getString("name", ""));
        sizeSeekBar.setProgress(prefs.getInt("size", 2));
        doughGroup.check(prefs.getInt("dough", R.id.thin));
        cheeseBox.setChecked(prefs.getBoolean("cheese", false));
        hamBox.setChecked(prefs.getBoolean("ham", false));
        mushroomBox.setChecked(prefs.getBoolean("mushroom", false));
        pineappleBox.setChecked(prefs.getBoolean("pineapple", false));
        deliverySwitch.setChecked(prefs.getBoolean("delivery", false));

    }

    private void updateSizeText() {
        sizeText.setText("Rozmiar pizzy: " + pizzaSize + " cm");
    }

    private void showOrderSummary() {
        String name = nameEditText.getText().toString();
        if (name.isEmpty()) {
            Toast.makeText(this, "Podaj swoje imię!", Toast.LENGTH_SHORT).show();
            return;
        }

        String dough = "";
        int doughId = doughGroup.getCheckedRadioButtonId();
        if (doughId == -1) dough = "(brak wyboru)";
        else dough = ((RadioButton) findViewById(doughId)).getText().toString();

        StringBuilder ingredients = new StringBuilder();
        int price = pizzaSize - 10;

        if (cheeseBox.isChecked()) { ingredients.append("Ser, "); price += 2; }
        if (hamBox.isChecked()) { ingredients.append("Szynka, "); price += 2; }
        if (mushroomBox.isChecked()) { ingredients.append("Pieczarki, "); price += 2; }
        if (pineappleBox.isChecked()) { ingredients.append("Ananas, "); price += 2; }

        if (dough.equals("Serowe (+5 zł)")) price += 5;

        if (ingredients.length() == 0) ingredients.append("Brak dodatków");
        else ingredients.setLength(ingredients.length() - 2);

        boolean delivery = deliverySwitch.isChecked();
        if (delivery) price += 5;

        String summary = "Zamówienie dla: " + name +
                "\nRozmiar: " + pizzaSize + " cm" +
                "\nCiasto: " + dough +
                "\nSkładniki: " + ingredients.toString() +
                "\nDostawa: " + (delivery ? "z dostawą (+5 zł)" : "odbiór") +
                "\n\nCena: " + price + " zł";

        new AlertDialog.Builder(this)
                .setTitle("Podsumowanie zamówienia")
                .setMessage(summary)
                .setPositiveButton("OK", null)
                .show();
    }

    private void resetForm() {
        nameEditText.setText("");
        sizeSeekBar.setProgress(2);
        doughGroup.check(R.id.thin);
        cheeseBox.setChecked(false);
        hamBox.setChecked(false);
        mushroomBox.setChecked(false);
        pineappleBox.setChecked(false);
        deliverySwitch.setChecked(false);
    }
    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences prefs = getSharedPreferences("pizza_prefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        editor.putString("name", nameEditText.getText().toString());
        editor.putInt("size", sizeSeekBar.getProgress());
        editor.putInt("dough", doughGroup.getCheckedRadioButtonId());
        editor.putBoolean("cheese", cheeseBox.isChecked());
        editor.putBoolean("ham", hamBox.isChecked());
        editor.putBoolean("mushroom", mushroomBox.isChecked());
        editor.putBoolean("pineapple", pineappleBox.isChecked());
        editor.putBoolean("delivery", deliverySwitch.isChecked());

        editor.apply();
    }

}
