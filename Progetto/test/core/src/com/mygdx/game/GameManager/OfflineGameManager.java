package com.mygdx.game.GameManager;

import DatabaseManagement.Database;
import DatabaseManagement.Enum.DropType;
import DatabaseManagement.Enum.TableType;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.mygdx.game.BreakGame;
import com.mygdx.game.CommandPlayer;
import com.mygdx.game.Levels.GestoreLivelli;
import com.mygdx.game.Player.HumanPlayer;
import com.mygdx.game.Player.Player;
import com.mygdx.game.Player.RobotPlayer;
import com.mygdx.game.Screen.*;
import com.mygdx.game.hud.Hud;
import help.GameState;
import help.Info;
import help.Timer;
import sprites.Ball;
import sprites.Brick.Brick;
import sprites.Paddle;
import sprites.powerup.PowerUp;

import java.util.ArrayList;
import java.util.Date;

public class OfflineGameManager extends GameManager {

    private static String playerName;
    private boolean loop;
    private Music musicGame;
    private Music musicGameOver;
    private Hud hud;
    private boolean isPaused;
    private Database db = new Database();


    private OfflineGameScreen screen;

   public OfflineGameManager(BreakGame game, OfflineGameScreen screen, int numeroPlayer) {
        this.numeroPlayer = numeroPlayer;
        this.game=game;
        this.screen=screen;
        players =new ArrayList<Player>();
        paddles = new ArrayList<Paddle>();
        commandPlayers = new ArrayList<CommandPlayer>();
        bricks=new ArrayList<Brick>();
        players.add(new HumanPlayer(playerName));
        date = new ArrayList<Date>();
        palla = new Ball();
        timer = new Timer();

        musicGame = Gdx.audio.newMusic(Gdx.files.internal("music.mp3"));
        musicGameOver = Gdx.audio.newMusic(Gdx.files.internal("Untitled.mp3"));
        musicGame.setVolume(1);
        isFinished = false;
        livelloCorrente = 1;
        nextLevel = false;
        isPaused = false;

        Info.getInstance().setDt(1);
        tmpDT = Info.getInstance().getDt();
        date.add(new Date());
        paddles.add(new Paddle(numeroPlayer, 1));
        Info.getInstance().getPaddleresizex().add(0.5f);

        if (numeroPlayer > 1) {
            for (int i = 1; i < numeroPlayer; i++) {
                paddles.add(new Paddle(numeroPlayer, i + 1));
                players.add(new RobotPlayer("Robot " + i, palla, paddles.get(i)));
                Info.getInstance().getPaddleresizex().add(0.5f);
                date.add(i,new Date());
            }
        }
        gestoreLivelli=new GestoreLivelli("fileLivelli.txt");
        updateScene();
        updateLevel();
        hud = new Hud(players, game.getBatch());
        gameHolder = players.get(0);
        bg = gestoreLivelli.getLivello(livelloCorrente - 1).getBackground();
    }

    @Override
    public void render() {
        game.getBatch().begin();

        if (nextLevel) {//deve stare dentro render perchè deve essere controllato sempre
            bricks = gestoreLivelli.getLivello(livelloCorrente - 1).getBricks();//ritorno l'array adatto al nuovo livello
            bg = gestoreLivelli.getLivello(livelloCorrente - 1).getBackground();
        }
        if(gameState!= GameState.GAME_OVER && gameState!=GameState.YOU_WON) {
            musicGame.play();
        }

        palla.getPositionBall().add(palla.getSpeedBall().x * Info.getInstance().getDt(), palla.getSpeedBall().y * Info.getInstance().getDt());
        palla.getBoundsBall().setPosition(palla.getPositionBall());
        ArrayList<PowerUp> tmpPUps=new ArrayList<PowerUp>();
        for(PowerUp p:powerUps) {
            if(p.getPosition().y+Info.getInstance().getPowerUpHeight()<0) {
                tmpPUps.add(p);
            }
            p.getPosition().add(p.getSpeed().x*Info.getInstance().getDt(), p.getSpeed().y*Info.getInstance().getDt());
            p.getBounds().setPosition(p.getPosition());
        }
        for(PowerUp p:tmpPUps) {
            powerUps.remove(p);
        }
        Drawer.drawMultiplayerOffline( game,  bg, bricks, players, powerUps,  paddles,  palla);
        if (numeroPlayer > 1) {
            for (int i = 1; i < numeroPlayer; i++) {
                game.getBatch().draw(paddles.get(i), paddles.get(i).getPosition().x, paddles.get(i).getPosition().y, paddles.get(i).getWidth() * Info.getInstance().getPaddleresizex().get(i), paddles.get(i).getHeight() * Info.getInstance().getPaddleresize());
                if(!isPaused) {
                    commandPlayers.get(i).move();
                }
            }
        }

        if(!isPaused) {
            commandPlayers.get(0).move();
        }
        if (commandPlayers.get(0).checkpause()) {
            musicGame.pause();
            game.setScreen(new PauseScreen(game, screen));
        }
        gestisciCollisioni();
        checkTimerPowerUp(); // controlla il tempo
        if (matEliminati == gestoreLivelli.getLivello(livelloCorrente - 1).getnMatMorbidi()) {
            gameState = GameState.YOU_WON;

            livelloCorrente++;
            if (livelloCorrente > gestoreLivelli.getNumeroLivelli()) {
                isFinished = true;
            }
        }
        if (palla.getPositionBall().y <= 0) {
            lostLife();
            updateScene();
        }
        if (gameState == GameState.YOU_WON) {
            if (isFinished) {
                livelloCorrente = 1;
                isFinished = false;
                db.modify(""+(int)Math.random()*1000, playerName, players.get(0).getScore(), DropType.INSERT, TableType.OFFLINE);
                updateScene();
                updateLevel();
                game.setScreen(new FinishScreen(game));
            } else {
                nextLevel = true;
                game.setScreen(new WinGameScreen(game,screen,gameState));
                updateScene();
                updateLevel();
            }
            musicGame.stop();
        }
        if (gameState == GameState.GAME_OVER) {
            musicGame.stop();
            if(!loop)
                musicGameOver.play();
            loop = true;
            game.setScreen(new LoseGameScreen(game));
        }
        if(gameState== GameState.WAIT) {
            Info.getInstance().setDt(0);
            if(Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                Info.getInstance().setDt(tmpDT);
                gameState=GameState.ACTION;
            }
        }
        game.getBatch().end();
    }

    protected void deletePlayer(Player loser) {
        if(players.get(0).equals(loser)) {
            db.modify(""+(int)Math.random()*1000, playerName, players.get(0).getScore(), DropType.INSERT, TableType.OFFLINE);
            gameState=GameState.GAME_OVER;
        }
        else {
            int index=players.indexOf(loser);
            players.remove(loser);
            paddles.remove(index);
            numeroPlayer--;
        }
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }

    public static void setPlayerName(String playerName) {
        OfflineGameManager.playerName = playerName;
    }

    public static String getPlayerName() {
        return playerName;
    }
}
