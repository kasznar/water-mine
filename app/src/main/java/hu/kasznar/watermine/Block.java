package hu.kasznar.watermine;

import android.graphics.Rect;
import android.media.Image;


public class Block extends Sprite implements Commons {

    // resistance towards mining, state during destruction
    protected int hardness = 200;
    protected int value;

    //price of the contained mineral
    protected int price;

    public Block(int x, int y, int price) {
        super(x, y);

        value = hardness;
        this.price = price;
        initBlock();
    }

    private void initBlock() {

        setImageDimensions(BLOCK_WIDTH,BLOCK_HEIGHT);
    }

    public int getPrice(){
        return price;
    }

    public void mineBlock(int minimgForce) {
        value -= minimgForce;
    }

    public double getMiningStatus() {
        return (double)value/(double)hardness;
    }

    public Rect getBounds() {
        // return new Rect(x, y, width, height);
        return new Rect(x, y, x+width, y+height);
    }
}
