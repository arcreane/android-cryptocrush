package fr.dantindurand.cryptocrush;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.TranslateAnimation;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    int interval = 300;
    TextView scoreResult;
    int score = 0;
    boolean isWin = false;
    //int scoreToWin = (int)Math.floor(Math.random()*(200-100+1)+100);
    int scoreToWin = 50;

    int displacement;
    Vibrator vibrator;
    MediaPlayer beepSoundMP;
    MediaPlayer switchSoundMP;

    @Override
    public void onBackPressed() {
        Toast.makeText(GameActivity.this, "Impossible de revenir en arrière", Toast.LENGTH_SHORT).show();
    }

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
                    moveCoinAnimation("left");
                    setTimeout(() -> coinInterchange(), 300);

                }

                @Override
                void onSwipeRight() {
                    super.onSwipeRight();
                    switchSoundMP.start();
                    coinToBeDragged = imageView.getId();
                    coinToBeReplaced = coinToBeDragged + 1;
                    moveCoinAnimation("right");
                    setTimeout(() -> coinInterchange(), 300);
                }

                @Override
                void onSwipeTop() {
                    super.onSwipeTop();
                    switchSoundMP.start();
                    coinToBeDragged = imageView.getId();
                    coinToBeReplaced = coinToBeDragged - noOfBlocks;
                    moveCoinAnimation("top");
                    setTimeout(() -> coinInterchange(), 300);
                }

                @Override
                void onSwipeBottom() {
                    super.onSwipeBottom();
                    switchSoundMP.start();
                    coinToBeDragged = imageView.getId();
                    coinToBeReplaced = coinToBeDragged + noOfBlocks;
                    moveCoinAnimation("bottom");
                    setTimeout(() -> coinInterchange(), 300);
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
                TranslateAnimation animationDown = new TranslateAnimation(0, 0, 0, noOfBlocks);
                animationDown.setDuration(300);
                animationDown.restrictDuration(300);
                animationDown.setFillAfter(true);
                coin.get(i + noOfBlocks).startAnimation(animationDown);

                coin.get(i + noOfBlocks).setImageResource((int) coin.get(i).getTag());
                coin.get(i + noOfBlocks).setTag(coin.get(i).getTag());
                coin.get(i).setImageResource(notCoin);
                coin.get(i).setTag(notCoin);
               /* if(list.contains(i) && (int) coin.get(i).getTag() == notCoin) {
                    int randomColor = (int) Math.floor(Math.random() * coins.length);
                    coin.get(i).setImageResource(coins[randomColor]);
                    coin.get(i).setTag(coins[randomColor]);
                    vibrator.vibrate(5);
                }*/
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


        System.out.println("first " + coin.get(coinToBeDragged).getTag());
        System.out.println("first2 " + coin.get(coinToBeReplaced).getTag());

        int background = (int) coin.get(coinToBeReplaced).getTag();
        int background1 = (int) coin.get(coinToBeDragged).getTag();

        coin.get(coinToBeDragged).setImageResource(background);
        coin.get(coinToBeReplaced).setImageResource(background1);
        coin.get(coinToBeDragged).setTag(background);
        coin.get(coinToBeReplaced).setTag(background1);
        displacement = displacement + 1;
        vibrator.vibrate(5);
        /*
        System.out.println("second " + coin.get(coinToBeDragged).getTag());
        System.out.println("second " + coin.get(coinToBeReplaced).getTag());

        System.out.println("left " + coin.get(coinToBeReplaced - 1).getTag());
        //System.out.println(coin.get(coinToBeReplaced - 1).getTag().equals(coin.get(coinToBeDragged).getTag()));
        System.out.println("right " + coin.get(coinToBeReplaced + 1).getTag());
        //System.out.println(coin.get( coinToBeReplaced + 1 ).getTag().equals(coin.get(coinToBeDragged).getTag()));
        System.out.println("bottom " + coin.get(coinToBeReplaced - noOfBlocks).getTag());
        //System.out.println(coin.get( coinToBeReplaced - noOfBlocks ).getTag().equals(coin.get(coinToBeDragged).getTag()));
        System.out.println("top " + coin.get(coinToBeReplaced + noOfBlocks).getTag());
        //System.out.println(coin.get(coinToBeReplaced + noOfBlocks).getTag().equals(coin.get(coinToBeDragged).getTag()));
*/

    }

    @SuppressLint("SetTextI18n")
    private void changeScore() {
        if(displacement > 0)  score = score + 3;
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

    private void moveCoinAnimation(String to) {
        TranslateAnimation animationToBeDragged = null;
        TranslateAnimation animationToBeReplaced = null;

        switch (to) {
            case "left":
                animationToBeDragged = new TranslateAnimation(0, -widthOfBlock, 0, 0);
                animationToBeReplaced = new TranslateAnimation(0, widthOfBlock, 0, 0);
                break;
            case "right":
                animationToBeDragged = new TranslateAnimation(0, widthOfBlock, 0, 0);
                animationToBeReplaced = new TranslateAnimation(0, -widthOfBlock, 0, 0);
                break;
            case "top":
                animationToBeDragged = new TranslateAnimation(0, 0, 0, -widthOfBlock);
                animationToBeReplaced = new TranslateAnimation(0, 0, 0, widthOfBlock);
                break;
            case "bottom":
                animationToBeDragged = new TranslateAnimation(0, 0, 0, widthOfBlock);
                animationToBeReplaced = new TranslateAnimation(0, 0, 0, -widthOfBlock);
                break;
        }

        animationToBeDragged.setDuration(300);
        animationToBeDragged.restrictDuration(300);
        animationToBeDragged.setFillAfter(true);
        coin.get(coinToBeDragged).startAnimation(animationToBeDragged);


        animationToBeReplaced.setDuration(300);
        animationToBeReplaced.restrictDuration(300);

        animationToBeReplaced.setFillAfter(true);
        coin.get(coinToBeReplaced).startAnimation(animationToBeReplaced);

        setTimeout(() -> {
            coin.get(coinToBeDragged).clearAnimation();
            coin.get(coinToBeReplaced).clearAnimation();
        }, 300);

    }

    public static void setTimeout(Runnable runnable, int delay){
        new Thread(() -> {
            try {
                Thread.sleep(delay);
                runnable.run();
            }
            catch (Exception e){
                System.err.println(e);
            }
        }).start();
    }

}
