package fr.dantindurand.cryptocrush;

import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.view.Window;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);

        Button playBtn = (Button)findViewById(R.id.play_btn);
        TextView textView = new TextView(this);

        playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setContentView(R.layout.game_main);
                TextView textPage = (TextView)findViewById(R.id.textView);
                TableLayout table = (TableLayout)findViewById(R.id.tableLayout);
                Game game = new Game();
                game.start();

                if(game.gameStatus() == true) {
                    textPage.setText("started !");
                    textView.setText("test 1");

                    table.addView(textView);
                }
            }
        });
    }
}