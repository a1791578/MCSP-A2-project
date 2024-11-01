package com.example.uiappliction.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.uiappliction.R;

public class ModelSelectionActivity extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model_selection);

        String itemName = getIntent().getStringExtra("ITEM_NAME");

        if (itemName != null) {
            switch (itemName) {
                case "Coffee":
                    startHelloArActivity("coffee");
                    break;
                case "Pizza":
                    startHelloArActivity("pizza");
                    break;
                case "Chicken Thigh":
                    startHelloArActivity("chickenThigh");
                    break;
                case "Chicken Burger":
                    startHelloArActivity("chickenBurger");
                    break;
                case "Beef Burger":
                    startHelloArActivity("beefBurger");
                    break;
                case "Cola":
                    startHelloArActivity("cola");
                    break;
                default:
                    Toast.makeText(this, "AR model not found for " + itemName, Toast.LENGTH_SHORT).show();
                    break;
            }
        }

        /*

        // Set up buttons for each model
        Button coffeeModelButton = findViewById(R.id.button_coffee);
        coffeeModelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start HelloArActivity with the selected model identifier
                startHelloArActivity("coffee");
            }
        });

        Button pizzaModelButton = findViewById(R.id.button_pizza);
        pizzaModelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start HelloArActivity with the selected model identifier
                startHelloArActivity("pizza");
            }
        });
        Button chickenModelButton = findViewById(R.id.button_chicken);
        chickenModelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start HelloArActivity with the selected model identifier
                startHelloArActivity("chickenThigh");
            }
        });
        Button chickenBurgerModelButton = findViewById(R.id.button_chickenBurger);
        chickenBurgerModelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start HelloArActivity with the selected model identifier
                startHelloArActivity("chickenBurger");
            }
        });

        Button beefBurgerModelButton = findViewById(R.id.button_beefBurger);
        beefBurgerModelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start HelloArActivity with the selected model identifier
                startHelloArActivity("beefBurger");
            }
        });

        Button colaModelButton = findViewById(R.id.button_cola);
        colaModelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start HelloArActivity with the selected model identifier
                startHelloArActivity("cola");
            }
        });
         */

        // Add more buttons and model identifiers as needed
    }

    private void startHelloArActivity(String modelIdentifier) {
        Intent intent = new Intent(this, HelloArActivity.class);
        intent.putExtra("MODEL_IDENTIFIER", modelIdentifier);
        startActivity(intent);
    }
}
