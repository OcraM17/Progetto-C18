package com.mygdx.game.Screen;

import com.badlogic.gdx.graphics.Texture;
import sprites.Button;

import java.util.ArrayList;

public class ButtonCollection {
    static ButtonCollection instance;
    private ArrayList<Button> menuButtons;
    private ArrayList<Button> pauseButtons;
    private ArrayList<Button> scoreButtons;

    private ButtonCollection() {
        menuButtons=new ArrayList<Button>();
        pauseButtons=new ArrayList<Button>();
        scoreButtons=new ArrayList<Button>();

        menuButtons.add(new Button("play.png", 530 ));
        menuButtons.add(new Button("exit.png", 50 ));
        menuButtons.add(new Button("multiplayeroffline.png", 290 ));
        menuButtons.add(new Button("multiplayeronline.png", 410 ));
        menuButtons.add(new Button("score.png", 170 ));

        pauseButtons.add(new Button("resume.png", 550));
        pauseButtons.add(new Button("exit.png", 150));
        pauseButtons.add(new Button("menu.png", 350));

        scoreButtons.add(new Button("menu.png", 50));
    }

    public static ButtonCollection getInstance() {
        if (instance == null) {
            instance = new ButtonCollection();
        }
        return instance;
    }

    public ArrayList<Button> getMenuButtons() {
        return menuButtons;
    }

    public ArrayList<Button> getPauseButtons() {
        return pauseButtons;
    }

    public ArrayList<Button> getScoreButtons() {
        return scoreButtons;
    }
}