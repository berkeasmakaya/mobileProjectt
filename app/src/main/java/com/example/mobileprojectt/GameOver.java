package com.example.mobileprojectt;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
public class GameOver extends AppCompatActivity {
    TextView tvPoints = findViewById(R.id.tvPoints);

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.game_over);
        int points = getIntent().getExtras().getInt("points");
        tvPoints.setText("" + points);
    }

    public void restart(View view) {
        Intent intent = new Intent(GameOver.this, Start.class);
        startActivity(intent);
        finish();
    }

    public void exit(View view) {
        finish();
    }
}
