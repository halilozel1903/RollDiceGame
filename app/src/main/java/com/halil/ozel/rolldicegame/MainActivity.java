package com.halil.ozel.rolldicegame;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    public static final Random RANDOM = new Random(); // Random nesnesi olusturduk.

    Button playGame; // button

    ImageView imgView1, imgView2; // image

    TextView textView2, textView3; // textview


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // id değerleri getirildi.
        playGame = findViewById(R.id.playGame);
        imgView1 = findViewById(R.id.imgView1);
        imgView2 = findViewById(R.id.imgView2);
        textView2 = findViewById(R.id.textView2);
        textView3 = findViewById(R.id.textView3);

    }


    // oyunu oynatma metodu
    public void playGame(View view) {

        // value değerleri
        int value1 = randomDiceValues();
        int value2 = randomDiceValues();

        // resim değerlerini alma.
        int picture1 = getResources().getIdentifier("dice_" + value1, "drawable", getPackageName());
        int picture2 = getResources().getIdentifier("dice_" + value2, "drawable", getPackageName());


        // resimleri yükleme
        imgView1.setImageResource(picture1);
        imgView2.setImageResource(picture2);


        // sonucları gösterme
        textView2.setText(String.valueOf(value1));
        textView3.setText(String.valueOf(value2));

    }

    // Random değer üreten fonksiyon
    public static int randomDiceValues() {


        return RANDOM.nextInt(6) + 1; // 1-6 arası


    }


}
