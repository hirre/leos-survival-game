
/**
 * This game was created by Hirad & Leo Asadi 2020-11-09.
 */
import game.LeoCanvas;
import game.LeoEngine;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.JFrame;
import javax.swing.Timer;

/**
 * Try to stay away from the moving objects!
 */
public class Main
{
    private static final int FRAME_WIDTH = 700;
    private static final int FRAME_HEIGHT = 650;
    private static final int DIRECTION_CHANGE_DELAY_MS = 100;

    public static void main(String[] args) throws InterruptedException
    {
        LeoEngine le = new LeoEngine(FRAME_WIDTH, FRAME_HEIGHT, DIRECTION_CHANGE_DELAY_MS);
        LeoCanvas lc = new LeoCanvas(le);

        JFrame f = new JFrame();
        f.setTitle("Leo's Survival Game");
        f.getContentPane().setBackground(Color.WHITE);
        f.setResizable(false);
        f.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        f.getContentPane().add(lc);

        bindControls(f, le);

        // Game loop
        Timer gameLoopTimer = new Timer(10, (ActionEvent e) ->
        {
            le.moveRectangleObject();
            le.moveCircles();
            f.repaint();
            le.detectCollisionWithRectangle();
            le.detectCollisionWithCircles();
            le.calculateScore();
        });

        gameLoopTimer.start();
    }

    private static void bindControls(JFrame f, LeoEngine lc)
    {
        f.addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                switch (e.getKeyCode())
                {
                case KeyEvent.VK_UP -> lc.setY(-lc.getSpeed());
                case KeyEvent.VK_DOWN -> lc.setY(lc.getSpeed());
                case KeyEvent.VK_RIGHT -> lc.setX(lc.getSpeed());
                case KeyEvent.VK_LEFT -> lc.setX(-lc.getSpeed());
                case KeyEvent.VK_A -> {
                    int speed = lc.getSpeed();
                    if (speed + 1 <= 20)
                        lc.setSpeed(speed + 1);
                }
                case KeyEvent.VK_S -> {
                    int speed = lc.getSpeed();
                    if (speed - 1 > 0)
                        lc.setSpeed(speed - 1);
                }
                default -> {
                }
                }
            }
        });

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
    }
}
