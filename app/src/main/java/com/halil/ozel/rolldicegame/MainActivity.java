package com.halil.ozel.rolldicegame;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends Activity {

    // Creating an Random object.
    public static final Random RANDOM = new Random();
    Button playGame;
    ImageView imgView1, imgView2;
    TextView textView2, textView3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Id values are returned.
        playGame = findViewById(R.id.playGame);
        imgView1 = findViewById(R.id.imgView1);
        imgView2 = findViewById(R.id.imgView2);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);
    }


    // Game play method
    public void playGame(View view) {
        // Variable values
        int value1 = randomDiceValues();
        int value2 = randomDiceValues();

        // Get image values.
        @SuppressLint("DiscouragedApi") int picture1 = getResources().getIdentifier("dice_" + value1, "drawable", getPackageName());
        @SuppressLint("DiscouragedApi") int picture2 = getResources().getIdentifier("dice_" + value2, "drawable", getPackageName());

        // Upload pictures
        imgView1.setImageResource(picture1);
        imgView2.setImageResource(picture2);

        // Show results
        textView2.setText(String.valueOf(value1));
        textView3.setText(String.valueOf(value2));
    }

    // Random value generating function
    public static int randomDiceValues() {
        return RANDOM.nextInt(6) + 1; // 1-6
    }
}
