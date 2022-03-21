package fr.dantindurand.cryptocrush;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    public static final String EXTRA_SCORE = "fr.dantindurand.cryptocrush.game.EXTRA_NUMBER";
    public static final String EXTRA_DISPLACEMENT = "fr.dantindurand.cryptocrush.game.EXTRA_DISPLACEMENT";

    int[] coins = {
            R.drawable.augur,
            R.drawable.cosmos,
            R.drawable.bitcoin,
            R.drawable.maker,
            R.drawable.ethereum,
            R.drawable.sushiswap,
            R.drawable.uma
    };

    int widthOfBlock, noOfBlocks = 8, widthOfScreen;
    ArrayList<ImageView> coin = new ArrayList<>();
    int coinToBeDragged, coinToBeReplaced;
    int notCoin = R.drawable.transparent;
    Handler mHandler;
    int interval = 100;
    TextView scoreResult;
    int score = 0;
    boolean isWin = false;
    int scoreToWin = (int)Math.floor(Math.random()*(200-100+1)+100);
    int displacement;
    Vibrator vibrator;
    MediaPlayer beepSoundMP;
    MediaPlayer switchSoundMP;


    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);//will hide the title
        getSupportActionBar().hide();
        setContentView(R.layout.activity_game);

        scoreResult = findViewById(R.id.score);
        scoreResult.setText("0 / " + String.valueOf(scoreToWin));
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        widthOfScreen = displayMetrics.widthPixels;
        int heightOfScreen = displayMetrics.heightPixels;


        beepSoundMP = MediaPlayer.create(this, R.raw.beep_sound_effect);
        switchSoundMP = MediaPlayer.create(this, R.raw.whoosh_sound_effect);
        widthOfBlock = widthOfScreen / noOfBlocks;
        createBoard();
        for (ImageView imageView : coin) {
            imageView.setOnTouchListener(new OnSwipeListener(this) {
                @Override
                void onSwipeLeft() {
                    super.onSwipeLeft();
                    switchSoundMP.start();
                    coinToBeDragged = imageView.getId();
                    coinToBeReplaced = coinToBeDragged - 1;
                    coinInterchange();

                }

                @Override
                void onSwipeRight() {
                    super.onSwipeRight();
                    switchSoundMP.start();
                    coinToBeDragged = imageView.getId();
                    coinToBeReplaced = coinToBeDragged + 1;
                    coinInterchange();
                }

                @Override
                void onSwipeTop() {
                    super.onSwipeTop();
                    switchSoundMP.start();
                    coinToBeDragged = imageView.getId();
                    coinToBeReplaced = coinToBeDragged - noOfBlocks;
                    coinInterchange();
                }

                @Override
                void onSwipeBottom() {
                    super.onSwipeBottom();
                    switchSoundMP.start();
                    coinToBeDragged = imageView.getId();
                    coinToBeReplaced = coinToBeDragged + noOfBlocks;
                    coinInterchange();
                }
            });
        }
        mHandler = new Handler();
        startRepeat();
    }

    private void checkRowForThree() {
        for (int i = 0; i < 62; i++) {
            int chosedCandy = (int) coin.get(i).getTag();
            boolean isBlank = (int) coin.get(i).getTag() == notCoin;
            Integer[] notValid = {6,7,14,15,22,23,30,31,38,39,46,47,54,55};
            List<Integer> list = Arrays.asList(notValid);
            if(!list.contains(i)) {
                int x = i;
                if((int) coin.get(x++).getTag() == chosedCandy && ! isBlank &&
                        (int) coin.get(x++).getTag() == chosedCandy &&
                        (int) coin.get(x).getTag() == chosedCandy)
                {
                    changeScore();

                    coin.get(x).setImageResource(notCoin);
                    coin.get(x).setTag(notCoin);
                    x--;
                    coin.get(x).setImageResource(notCoin);
                    coin.get(x).setTag(notCoin);
                    x--;
                    coin.get(x).setImageResource(notCoin);
                    coin.get(x).setTag(notCoin);
                }
            }
        }
        moveDownCoins();
    }

    private void checkColumnForThree() {
        for (int i = 0; i < 47; i++) {
            int chosedCandy = (int) coin.get(i).getTag();
            boolean isBlank = (int) coin.get(i).getTag() == notCoin;

            int x = i;
            if((int) coin.get(x).getTag() == chosedCandy && ! isBlank &&
                    (int) coin.get(x+noOfBlocks).getTag() == chosedCandy &&
                    (int) coin.get(x+2*noOfBlocks).getTag() == chosedCandy)
            {
                changeScore();

                coin.get(x).setImageResource(notCoin);
                coin.get(x).setTag(notCoin);
                x = x + noOfBlocks;
                coin.get(x).setImageResource(notCoin);
                coin.get(x).setTag(notCoin);
                x = x + noOfBlocks;
                coin.get(x).setImageResource(notCoin);
                coin.get(x).setTag(notCoin);

            }
        }
        moveDownCoins();
    }

    private void moveDownCoins() {
        Integer[] firstRow = {0, 1, 2, 3, 4, 5, 6, 7};
        List<Integer> list = Arrays.asList(firstRow);
        for (int i = 55; i >= 0; i--) {
            if( (int) coin.get(i + noOfBlocks).getTag() == notCoin) {
                coin.get(i + noOfBlocks).setImageResource((int) coin.get(i).getTag());
                coin.get(i + noOfBlocks).setTag(coin.get(i).getTag());
                coin.get(i).setImageResource(notCoin);
                coin.get(i).setTag(notCoin);
                if(list.contains(i) && (int) coin.get(i).getTag() == notCoin) {
                    int randomColor = (int) Math.floor(Math.random() * coins.length);
                    coin.get(i).setImageResource(coins[randomColor]);
                    coin.get(i).setTag(coins[randomColor]);
                    vibrator.vibrate(5);
                }

            }
        }
        for (int i = 0; i < 8; i++) {
            if ((int) coin.get(i).getTag() == notCoin) {
                int randomColor = (int) Math.floor(Math.random() * coins.length);
                coin.get(i).setImageResource(coins[randomColor]);
                coin.get(i).setTag(coins[randomColor]);
                vibrator.vibrate(5);

            }
        }
    }

    Runnable repeatChecker = new Runnable() {
        @Override
        public void run() {
            try {
                checkRowForThree();
                checkColumnForThree();
                moveDownCoins();
            }finally {
                mHandler.postDelayed(repeatChecker, interval);
            }
        }
    };

    void startRepeat() {
        repeatChecker.run();
    }

    private void coinInterchange() {
        int background = (int) coin.get(coinToBeReplaced).getTag();
        int background1 = (int) coin.get(coinToBeDragged).getTag();
        coin.get(coinToBeDragged).setImageResource(background);
        coin.get(coinToBeReplaced).setImageResource(background1);
        coin.get(coinToBeDragged).setTag(background);
        coin.get(coinToBeReplaced).setTag(background1);
        displacement = displacement + 1;
        vibrator.vibrate(5);
    }

    @SuppressLint("SetTextI18n")
    private void changeScore() {
        score = score + 3;
        vibrator.vibrate(10);
        beepSoundMP.start();
        scoreResult.setText(String.valueOf(score) + " / " + String.valueOf(scoreToWin));
        if(score >= scoreToWin && !isWin) {
            isWin = true;
            Intent intent = new Intent(this, WinActivity.class);
            intent.putExtra(EXTRA_SCORE, score);
            intent.putExtra(EXTRA_DISPLACEMENT, displacement);
            startActivity(intent);
        }

    }

    private void createBoard() {
        GridLayout gridLayout = findViewById(R.id.board);
        gridLayout.setRowCount(noOfBlocks);
        gridLayout.setColumnCount(noOfBlocks);
        gridLayout.getLayoutParams().width = widthOfScreen;
        gridLayout.getLayoutParams().height = widthOfScreen;
        gridLayout.setBackgroundResource(R.drawable.solid_border);

        for (int i = 0; i < noOfBlocks * noOfBlocks; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setId(i);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(widthOfBlock, widthOfBlock));
            imageView.setMaxHeight(widthOfBlock);
            imageView.setMaxWidth(widthOfBlock);
            int randomCandy = (int) Math.floor(Math.random() * coins.length);
            imageView.setImageResource(coins[randomCandy]);
            imageView.setTag(coins[randomCandy]);
            coin.add(imageView);
            gridLayout.addView(imageView);
        }


    }

}
