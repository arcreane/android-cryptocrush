package fr.dantindurand.cryptocrush;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Game extends AppCompatActivity {

    int[] candies = {
            R.drawable.egld,
            R.drawable.binance,
            R.drawable.bitcoin,
            R.drawable.dogecoin,
            R.drawable.ethereum,
            R.drawable.shibacoin
    };

    int widthOfBlock, noOfBlocks = 8, widthOfScreen;
    ArrayList<ImageView> candy = new ArrayList<>();
    int candyToBeDragged, candyToBeReplaced;
    int notCandy = R.drawable.ic_launcher_background;
    Handler mHandler;
    int interval = 100;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        widthOfScreen = displayMetrics.widthPixels;
        int heightOfScreen = displayMetrics.heightPixels;
        widthOfBlock = widthOfScreen / noOfBlocks;
        createBoard();
        for (ImageView imageView : candy) {
            imageView.setOnTouchListener(new OnSwipeListener(this) {
                @Override
                void onSwipeLeft() {
                    super.onSwipeLeft();
                    candyToBeDragged = imageView.getId();
                    candyToBeReplaced = candyToBeDragged - 1;
                    candyInterchange();

                }

                @Override
                void onSwipeRight() {
                    super.onSwipeRight();
                    candyToBeDragged = imageView.getId();
                    candyToBeReplaced = candyToBeDragged + 1;
                    candyInterchange();
                }

                @Override
                void onSwipeTop() {
                    super.onSwipeTop();
                    candyToBeDragged = imageView.getId();
                    candyToBeReplaced = candyToBeDragged - noOfBlocks;
                    candyInterchange();
                }

                @Override
                void onSwipeBottom() {
                    super.onSwipeBottom();
                    candyToBeDragged = imageView.getId();
                    candyToBeReplaced = candyToBeDragged + noOfBlocks;
                    candyInterchange();
                }
            });
        }
        mHandler = new Handler();
        startRepeat();
    }

    private void checkRowForThree() {
        for (int i = 0; i < 62; i++) {
            int chosedCandy = (int) candy.get(i).getTag();
            boolean isBlank = (int) candy.get(i).getTag() == notCandy;
            Integer[] notValid = {6,7,14,15,22,23,30,31,38,39,46,47,54,55};
            List<Integer> list = Arrays.asList(notValid);
            if(!list.contains(i)) {
                int x = i;
                if((int) candy.get(x++).getTag() == chosedCandy && ! isBlank &&
                        (int) candy.get(x++).getTag() == chosedCandy &&
                        (int) candy.get(x).getTag() == chosedCandy)
                {
                    candy.get(x).setImageResource(notCandy);
                    candy.get(x).setTag(notCandy);
                    x--;
                    candy.get(x).setImageResource(notCandy);
                    candy.get(x).setTag(notCandy);
                    x--;
                    candy.get(x).setImageResource(notCandy);
                    candy.get(x).setTag(notCandy);
                }
            }
        }
    }

    Runnable repeatChecker = new Runnable() {
        @Override
        public void run() {
            try {
                checkRowForThree();
            }finally {
                mHandler.postDelayed(repeatChecker, interval);
            }
        }
    };

    void startRepeat() {
        repeatChecker.run();
    }

    private void candyInterchange() {
        int background = (int) candy.get(candyToBeReplaced).getTag();
        int background1 = (int) candy.get(candyToBeDragged).getTag();
        candy.get(candyToBeDragged).setImageResource(background);
        candy.get(candyToBeReplaced).setImageResource(background1);
        candy.get(candyToBeDragged).setTag(background);
        candy.get(candyToBeReplaced).setTag(background1);
    }

    private void createBoard() {
        GridLayout gridLayout = findViewById(R.id.board);
        gridLayout.setRowCount(noOfBlocks);
        gridLayout.setColumnCount(noOfBlocks);
        gridLayout.getLayoutParams().width = widthOfScreen;
        gridLayout.getLayoutParams().height = widthOfScreen;

        for (int i = 0; i < noOfBlocks * noOfBlocks; i++) {
            ImageView imageView = new ImageView(this);
            imageView.setId(i);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(widthOfBlock, widthOfBlock));
            imageView.setMaxHeight(widthOfBlock);
            imageView.setMaxWidth(widthOfBlock);
            int randomCandy = (int) Math.floor(Math.random() * candies.length);
            imageView.setImageResource(candies[randomCandy]);
            imageView.setTag(candies[randomCandy]);
            candy.add(imageView);
            gridLayout.addView(imageView);
        }


    }
}