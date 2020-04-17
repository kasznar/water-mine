package hu.kasznar.watermine;

import android.graphics.Rect;
import android.media.Image;
import android.view.MotionEvent;


public class Submarine extends Sprite implements Commons{
    private int dx;
    private int dy;
    private int miningStartX;
    private int miningStartY;
    private int miningStartDx;
    private int miningStartDy;
    private String miningDirection;
    private int miningForce = 5;
    private boolean isMining = false;
    private int money = 0;
    // this variable is necessary because the direction has to be kept
    private boolean movingRight = true;
    private Image LeftImage;
    private Image RightImage;


    public Submarine(int x, int y) {
        super(x, y);
        setImageDimensions(SUBMARINE_WIDTH, SUBMARINE_HEIGHT);
    }

    public void mine(Block activeBlock){

        if (activeBlock.getMiningStatus() > 0){
            //TODO reasonable solution - should be calculated from the end of the block
            if (miningDirection == "down"){
                x = activeBlock.getX();
            }

            activeBlock.mineBlock(miningForce);
        }else{

            addMoney(activeBlock.getPrice());
            activeBlock.setVisible(false);
            isMining = false;
        }
    }

    public int getDx() {
        return dx;
    }

    public int getDy() {
        return dy;
    }

    public Rect getBounds() {
        //TODO: smaller width than appears
        return new Rect(x+250, y, x+500, y+height);
    }

    public Boolean getIfMovingRight() {
        return movingRight;
    }

    public void addMoney(int price){
        money += price;
        System.out.println("current money: " + money);
    }

    public int getMiningForce(){
        return miningForce;
    }

    public boolean getMining(){
        return isMining;
    }

    public void setMining(boolean mining) {
        isMining = mining;
    }

    public void setMiningStartX(int miningStartX) {
        this.miningStartX = miningStartX;
    }

    public void setMiningStartY(int miningStartY) {
        this.miningStartY = miningStartY;
    }

    public void setMiningDirection(String direction){
        miningDirection = direction;
    }


    public void touchEvent(MotionEvent event, String direction) {

        if (event.getActionMasked() == MotionEvent.ACTION_UP
                || event.getActionMasked() == MotionEvent.ACTION_POINTER_UP) {

            if (direction == "down"){
                dy = 0;
            }
            if (direction == "up") {
                dy = 0;
            }
            if (direction == "left") {
                dx = 0;
            }
            if (direction == "right") {
                dx = 0;
            }
        }

        if (event.getActionMasked() == MotionEvent.ACTION_DOWN
                || event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN) {

            if (direction == "down"){
                dy = 80;
            }
            if (direction == "up") {
                dy = -80;
            }
            if (direction == "left") {
                if (!isMining){
                    movingRight = false;
                }
                dx = -80;
            }
            if (direction == "right") {
                if (!isMining){
                    movingRight = true;
                }
                dx = 80;
            }
        }

    }

    public int getMoney(){
        return money;
    }
}