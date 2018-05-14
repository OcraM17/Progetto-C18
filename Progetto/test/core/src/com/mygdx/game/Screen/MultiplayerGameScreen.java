package com.mygdx.game.Screen;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.mygdx.game.BreakGame;
import com.mygdx.game.ClientServer.Client;
import help.Info;
import sprites.Ball;
import sprites.Brick.AbstractBrick;
import sprites.Brick.Brick;
import sprites.Brick.HardBrick;
import sprites.Paddle;
import sprites.powerup.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import static help.Info.paddleresizex;


public class MultiplayerGameScreen implements Screen {

    private Client client;
    private BreakGame game;
    private Ball palla;
    private ArrayList<Paddle> paddles;
    private ArrayList<AbstractBrick> bricks;
    private ArrayList<PowerUp> powerUps;


    public MultiplayerGameScreen(BreakGame game) {
        client=new Client();
        palla=new Ball();
        paddles=new ArrayList<Paddle>();
        bricks=new ArrayList<AbstractBrick>();
        powerUps=new ArrayList<PowerUp>();
        paddles.add(new Paddle(1,1));
        paddleresizex.add(0.5f);

        this.game=game;
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        game.getBatch().begin();
        game.getBatch().draw(new Texture("bg.jpg"), 0, 0);
        client.ricevi();
        String m=client.getMessage();
        System.out.println(m);
        if(!m.equals("")) {
            parseMessage(m);
        }
        for (AbstractBrick brick : bricks) {
            game.getBatch().draw(brick, brick.getPositionBrick().x, brick.getPositionBrick().y, brick.getWidth() * Info.brickresize, brick.getHeight() * Info.brickresize);
        }

        for(PowerUp p:powerUps) {
            game.getBatch().draw(p, p.getBounds().x, p.getBounds().y, p.getWidth()*Info.powerUpResize, p.getHeight()*Info.powerUpResize);
        }
        game.getBatch().draw(paddles.get(0), paddles.get(0).getPosition().x, paddles.get(0).getPosition().y, paddles.get(0).getWidth() * Info.paddleresizex.get(0), paddles.get(0).getHeight() * Info.paddleresize);
        game.getBatch().draw(palla, palla.getPositionBall().x, palla.getPositionBall().y, palla.getWidth() * Info.ballresize, palla.getHeight() * Info.ballresize);
        game.getBatch().end();

        if(Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            client.keyPressed(Input.Keys.LEFT);
        }
        else {
            if(Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                client.keyPressed(Input.Keys.RIGHT);
            }
            else {
                client.keyPressed(0);
            }
        }
    }

    public void parseMessage(String message) {
        int i;
        String[] lines=message.split("\n"); //Separa le righe
        palla.getPositionBall().x=Float.parseFloat(lines[0].split(" ")[0]);
        palla.getPositionBall().y=Float.parseFloat(lines[0].split(" ")[1]);

        paddles.get(0).getPosition().x=Float.parseFloat(lines[1].split(" ")[0]);
        Info.paddleresizex.set(0,Float.parseFloat(lines[2].split(" ")[0]));

        bricks.removeAll(bricks);
        for(i=3; i<lines.length; i++) {
            if(lines[i].split(" ")[2].equals("Brick")) {
                bricks.add(new Brick((int)Float.parseFloat(lines[i].split(" ")[0]),(int)Float.parseFloat(lines[i].split(" ")[1])));
            }
            else {
                if (lines[i].split(" ")[2].equals("HardBrick")) {
                    bricks.add(new HardBrick((int)Float.parseFloat(lines[i].split(" ")[0]), (int)Float.parseFloat(lines[i].split(" ")[1])));
                }
                else
                    break;
            }

        }

        powerUps.removeAll(powerUps);
        while(i<lines.length) {
            if(lines[i].split(" ")[2].equals("ExtraLife")){
                powerUps.add(new ExtraLife((int)Float.parseFloat(lines[i].split(" ")[0]), (int)Float.parseFloat(lines[i].split(" ")[1])));
            }
            if(lines[i].split(" ")[2].equals("LossLife")) {
                powerUps.add(new LossLife((int)Float.parseFloat(lines[i].split(" ")[0]), (int)Float.parseFloat(lines[i].split(" ")[1])));

            }
            if(lines[i].split(" ")[2].equals("LongPaddle")) {
                powerUps.add(new LongPaddle((int)Float.parseFloat(lines[i].split(" ")[0]), (int)Float.parseFloat(lines[i].split(" ")[1])));

            }
            if(lines[i].split(" ")[2].equals("ShortPaddle")) {
                powerUps.add(new ShortPaddle((int)Float.parseFloat(lines[i].split(" ")[0]), (int)Float.parseFloat(lines[i].split(" ")[1])));

            }
            i++;
        }

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
