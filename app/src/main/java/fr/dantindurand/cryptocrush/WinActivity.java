package fr.dantindurand.cryptocrush;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

public class WinActivity extends AppCompatActivity {
    int score, displacement;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide();
        setContentView(R.layout.activity_win);

        Intent intent = getIntent();
        score = intent.getIntExtra(GameActivity.EXTRA_SCORE, 0);
        displacement = intent.getIntExtra(GameActivity.EXTRA_DISPLACEMENT, 0);

        genEvaluation(score, displacement);

        Button replayBtn = (Button)findViewById(R.id.replay);
        Button leaveBtn = (Button)findViewById(R.id.leave);


        replayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WinActivity.this, GameActivity.class));
            }
        });

        leaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WinActivity.this, MainActivity.class));
            }
        });
    }

    @SuppressLint("SetTextI18n")
    public void genEvaluation(int score, int displacement) {
        TextView textTitle = (TextView)findViewById(R.id.winTextView);
        TextView textDescScore = (TextView)findViewById(R.id.winDescTextView);
        TextView textScore = (TextView)findViewById(R.id.score);
        TextView textDisplacement = (TextView)findViewById(R.id.displacement);

        GridLayout gridLayout = findViewById(R.id.starsGrid);

        int minDisplacement = Math.round(score / 3);

        ImageView star1 = (ImageView) findViewById(R.id.star1);
        ImageView star2 = (ImageView) findViewById(R.id.star2);
        ImageView star3 = (ImageView) findViewById(R.id.star3);

        textScore.setText("score: " + String.valueOf(score));
        textDisplacement.setText("déplacements: " + String.valueOf(displacement));


        if(displacement <= minDisplacement) {
            textTitle.setText("SUPER !");
            textDescScore.setText("Elon musk serait fier de toi");
            star1.setImageResource(R.drawable.star);
            star2.setImageResource(R.drawable.star);
            star3.setImageResource(R.drawable.star);

        } else if(displacement < minDisplacement * 2) {
            textTitle.setText("BRAVO");
            textDescScore.setText("Continues comme ça");
            star1.setImageResource(R.drawable.star);
            star2.setImageResource(R.drawable.star);
            star3.setImageResource(R.drawable.empty_star);
        } else {
            textTitle.setText("...");
            textDescScore.setText("Et dire que tu as aussi le droit de vote..");
            star1.setImageResource(R.drawable.star);
            star2.setImageResource(R.drawable.empty_star);
            star3.setImageResource(R.drawable.empty_star);
        }
    }
}