package help;

import com.badlogic.gdx.graphics.Texture;

public class Info {
    public static int altezza = 850;
    public static int larghezza = 800;
    public static int velBall=8;
    public static int dt = 1;
    public static float paddleresize = 0.5f;
    public static float brickresize = 0.8f;
    public static float ballresize = 0.8f;
    public static int brickGapX=10;
    public static int brickGapY=7;

    public static int getBrickWidth () {
        Texture brick=new Texture("normalBrick.jpg");
        int brickWidth=(int)(brick.getWidth()*brickresize);
        return brickWidth;
    }

    public static int getBrickHeight() {
        Texture brick=new Texture("normalBrick.jpg");
        int brickHeight=(int)(brick.getHeight()*brickresize);
        return brickHeight;
    }
}