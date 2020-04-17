package hu.kasznar.watermine;

import android.graphics.Rect;

public class Button {
    protected int x;
    protected int y;

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    protected int width;
    protected int height;

    public Button(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public Rect getBounds(){
        return new Rect(x, y, x+width, y+height);
    }
}
