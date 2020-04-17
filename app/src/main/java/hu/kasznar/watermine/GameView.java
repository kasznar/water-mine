package hu.kasznar.watermine;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.view.MotionEventCompat;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.SurfaceHolder;

import java.util.ArrayList;
import java.util.List;

import static hu.kasznar.watermine.Commons.*;


public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private MainThread thread;

    private Submarine submarine;
    // private final int DELAY = 20;
    private List<Block> blocks;
    private Block activeBlock;
    private int boardWorldX = 0;
    private int boardWorldY = 0;

    private Bitmap blockImage;
    private Bitmap blockTreasureImage;

    private Bitmap submarineRightImage;
    private Bitmap submarineLeftImage;

    private Button rightButton;
    private Button leftButton;
    private Button upButton;
    private Button downButton;

    private int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
    private int screenHeight = Resources.getSystem().getDisplayMetrics().heightPixels;

    private int coordinatesToPixel(int coordinates) {

        double conversionRatio =  (double)screenWidth/(double)BOARD_WIDTH;
        return (int)Math.round((double)coordinates * conversionRatio);
    }

    private int pixelToCoordinates(int pixel) {

        double conversionRatio =  (double)SCREEN_WIDTH/(double)BOARD_WIDTH;
        return (int)Math.round((double)pixel / conversionRatio);
    }

    private int worldToBoardCoordinatesX(int x) {
        return x - boardWorldX;
    }

    private int worldToBoardCoordinatesY(int y) {
        return y - boardWorldY;
    }

    private void initImages() {
        //Block images
        int blockWidth = coordinatesToPixel(BLOCK_WIDTH);

        Bitmap bb = BitmapFactory.decodeResource(getResources(),R.drawable.block);
        blockImage = Bitmap.createScaledBitmap(bb, blockWidth, blockWidth, true);

        Bitmap btb = BitmapFactory.decodeResource(getResources(),R.drawable.block_tresure);
        blockTreasureImage = Bitmap.createScaledBitmap(btb, blockWidth, blockWidth, true);

        //Submarine images
        int submarineWidth = coordinatesToPixel(SUBMARINE_WIDTH);
        int submarineHeight = coordinatesToPixel(SUBMARINE_HEIGHT);

        Bitmap srb = BitmapFactory.decodeResource(getResources(),R.drawable.ad_submarine_r);
        submarineRightImage = Bitmap.createScaledBitmap(srb, submarineWidth, submarineHeight, true);

        Bitmap slb = BitmapFactory.decodeResource(getResources(),R.drawable.ad_submarine_l);
        submarineLeftImage = Bitmap.createScaledBitmap(slb, submarineWidth, submarineHeight, true);
    }

    private void initBlocks() {
        blocks = new ArrayList<>();

        for (int j=0; j < 50; j++) {

            for (int i=0; i < 32; i++) {
                //TODO: proper value for the starting depth (2500)
                int price = 0;
                if (Math.random() > 0.8) {
                    price = 666;
                }

                blocks.add(new Block(i*1000,5500 + j*1000, price));
            }
        }
    }

    private void initButtons() {
        upButton = new Button(
                BOARD_WIDTH/2-1000,
                BOARD_HEIGHT+500,
                2000,
                1000);

        downButton = new Button(
                BOARD_WIDTH/2-1000,
                BOARD_HEIGHT+1900,
                2000,
                1000);

        leftButton = new Button(
                BOARD_WIDTH/2-3400,
                BOARD_HEIGHT+1200,
                2000,
                1000);

        rightButton = new Button(
                BOARD_WIDTH/2+1400,
                BOARD_HEIGHT+1200,
                2000,
                1000);

    }

    private void initBoard() {
        initImages();

        submarine = new Submarine((BOARD_WIDTH / 2) - (SUBMARINE_WIDTH / 2), 1000);

        initBlocks();

        initButtons();
    }

    private void drawObjectImage(Canvas c, Bitmap image, int coordX, int coordY) {
        c.drawBitmap(
                image,
                coordinatesToPixel(worldToBoardCoordinatesX(coordX)),
                coordinatesToPixel(worldToBoardCoordinatesY(coordY)),
                null
        );
    }

    private void drawObjectRect(Canvas c, int left, int top, int right, int bottom, int color) {
        Paint myPaint = new Paint();
        myPaint.setColor(color);

        int pLeft = coordinatesToPixel(left);
        int pTop = coordinatesToPixel(top);
        int pRight = coordinatesToPixel(right);
        int pBottom = coordinatesToPixel(bottom);

        c.drawRect(pLeft, pTop, pRight, pBottom, myPaint);
    }

    private void doDrawing(Canvas c) {


        //Fill background color
        drawObjectRect(c, 0,0, BOARD_WIDTH, BOARD_HEIGHT, Color.rgb(0, 192, 192));

        //painting the blocks
        for (Block block : blocks) {
            if (block.isVisible()) {

                if (block.getPrice() > 0) {
                    drawObjectImage(c, blockTreasureImage, block.getX(), block.getY());
                } else {
                    drawObjectImage(c, blockImage, block.getX(), block.getY());
                }

            }
        }

        //painting the submarine
        if(submarine.getIfMovingRight()){
            drawObjectImage(c, submarineRightImage, submarine.getX(), submarine.getY());
        }else {
            drawObjectImage(c, submarineLeftImage, submarine.getX(), submarine.getY());
        }

        //Control panel

        //Background
        drawObjectRect( c, 0, BOARD_HEIGHT, BOARD_WIDTH, BOARD_HEIGHT+5000, Color.BLACK);

        //buttons
        drawObjectRect(c,
                upButton.getX(),
                upButton.getY(),
                upButton.getWidth()+upButton.getX(),
                upButton.getHeight()+upButton.getY(),
                Color.rgb(254, 254, 2));

        drawObjectRect(c,
                downButton.getX(),
                downButton.getY(),
                downButton.getWidth()+downButton.getX(),
                downButton.getHeight()+downButton.getY(),
                Color.rgb(254, 254, 2));

        drawObjectRect(c,
                leftButton.getX(),
                leftButton.getY(),
                leftButton.getWidth()+leftButton.getX(),
                leftButton.getHeight()+leftButton.getY(),
                Color.rgb(254, 254, 2));

        drawObjectRect(c,
                rightButton.getX(),
                rightButton.getY(),
                rightButton.getWidth()+rightButton.getX(),
                rightButton.getHeight()+rightButton.getY(),
                Color.rgb(254, 254, 2));
    }

    private void step() {

        int prevX = submarine.getX();
        int prevY = submarine.getY();

        boolean bottomTouch = false;

        // add Y velocity

        submarine.addToY(submarine.getDy() + GRAVITY);

        // check if the added movement results is collusion
        Rect subRect = submarine.getBounds();

        for (Block block : blocks) {
            if(block.isVisible()){
                Rect blockRect = block.getBounds();

                if (subRect.intersect(blockRect)) {
                    //if touches bottom
                    if (submarine.getY() > prevY){
                        bottomTouch = true;
                    }

                    //if getting moved down
                    if(submarine.getDy() > 0){
                        // start mining downwards
                        startMining(block, "down");
                    } else {
                        submarine.setY(prevY);
                    }
                }
            }
        }


        // add X velocity

        submarine.addToX(submarine.getDx());

        // check if the added movement results is collusion
        subRect = submarine.getBounds();

        for (Block block : blocks) {
            if(block.isVisible()){
                Rect blockRect = block.getBounds();

                if (subRect.intersect(blockRect)) {
                    if (bottomTouch){
                        if (!submarine.getMining()){
                            // start mining with the proper direction, if not started already
                            if (submarine.getDx() > 0){
                                startMining(block, "right");
                            }else {
                                startMining(block, "left");
                            }
                        }
                    }else {
                        submarine.setX(prevX);
                    }
                }
            }
        }


        //keep inside world

        if (submarine.getX() < 0) {
            submarine.setX(0);
        }

        if(submarine.getX() > WORLD_WIDTH - submarine.getWidth()){
            submarine.setX(WORLD_WIDTH - submarine.getWidth());
        }

        if (submarine.getY() < 0) {
            submarine.setY(0);
        }

        if (submarine.getY() > WORLD_HEIGHT  - submarine.getHeight()){
            submarine.setY(WORLD_HEIGHT - submarine.getHeight());
        }

        //move board
        boardWorldX = submarine.getX()-BOARD_WIDTH/2+ SUBMARINE_WIDTH /2;
        boardWorldY = submarine.getY()-BOARD_HEIGHT/2+ SUBMARINE_HEIGHT /2;

        if (boardWorldX < 0) {
            boardWorldX = 0;
        }

        if (boardWorldX > 1000 * 32 - BOARD_WIDTH) {
            boardWorldX = 1000 * 32 - BOARD_WIDTH;
        }


    }

    private void startMining(Block block, String direction) {
        submarine.setMiningStartX(submarine.getX());
        submarine.setMiningStartY(submarine.getY());

        submarine.setMiningDirection(direction);

        submarine.setMining(true);
        activeBlock = block;
    }

    public GameView(Context context) {
        super(context);

        getHolder().addCallback(this);

        thread = new MainThread(getHolder(), this);

        setFocusable(true);

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {


        if (event.getActionMasked() == MotionEvent.ACTION_POINTER_UP ||
                event.getActionMasked() == MotionEvent.ACTION_UP ||
                event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN ||
                event.getActionMasked() == MotionEvent.ACTION_DOWN) {

            // Get the index of the pointer associated with the action.
            int actionIndex = event.getActionIndex();

            String buttonDirection = "";


            int x = (int)event.getX(actionIndex);
            int y = (int)event.getY(actionIndex);

            x = pixelToCoordinates(x);
            y = pixelToCoordinates(y);

            if (downButton.getBounds().contains( x, y)) {
                buttonDirection = "down";
            }

            if (upButton.getBounds().contains( x, y)) {
                buttonDirection = "up";
            }

            if (leftButton.getBounds().contains( x, y)) {
                buttonDirection = "left";
            }

            if (rightButton.getBounds().contains( x, y)) {
                buttonDirection = "right";
            }

            submarine.touchEvent(event, buttonDirection);

        }

        return true;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        initBoard();

        System.out.println("sw:"+screenWidth+" sh:"+screenHeight);

        thread.setRunning(true);
        thread.start();

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = true;
        while (retry) {
            try {
                thread.setRunning(false);
                thread.join();

            } catch(InterruptedException e){
                e.printStackTrace();
            }
            retry = false;
        }
    }

    public void update() {

        // the submarine stuck in the mining state until that completes
        if(submarine.getMining()){
            submarine.mine(activeBlock);
        }else {
            //move and check collision
            step();
        }
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);

        if(canvas!=null) {
            doDrawing(canvas);
        }
    }
}