package game;

import java.awt.Color;
import java.awt.Graphics;
import javax.swing.JComponent;
import javax.swing.JOptionPane;

public class LeoCanvas extends JComponent
{
    private static final long serialVersionUID = 1L;

    private final LeoEngine engine;

    public LeoCanvas(LeoEngine engine)
    {
        this.engine = engine;
        engine.registerCollisionCallback(() ->
        {
            JOptionPane.showMessageDialog(this, "You lost! You managed to survive for "
                    + engine.survivalTimeSeconds() + " seconds. \n\nSCORE: " + engine.getScore());
        });

        setDoubleBuffered(true);
        engine.init();
    }

    @Override
    public void paint(Graphics g)
    {
        int nrOfCircles;

        nrOfCircles = engine.getCircles().size();

        g.setColor(Color.BLACK);
        g.drawString("Speed (A = faster, S = slower): " + engine.getSpeed(), 5, 12);
        g.drawString("Number of balls: " + nrOfCircles, 230, 12);
        g.drawString("Survival time (s): " + engine.survivalTimeSeconds(), 370, 12);
        g.drawString("SCORE: " + engine.getScore(), 525, 12);
        g.drawLine(0, 15, engine.getframeWidth(), 15);

        g.setColor(Color.BLUE);
        g.drawOval(engine.getXplayer(), engine.getYplayer(), engine.getPlayerWidth(),
                engine.getPlayerHeight());
        g.fillOval(engine.getXplayer(), engine.getYplayer(), engine.getPlayerWidth(),
                engine.getPlayerHeight());

        g.setColor(Color.RED);
        g.drawRect(engine.getRectangleX(), engine.getRectangleY(), engine.getRectangleWidth(),
                engine.getRectangleHeight());
        g.fillRect(engine.getRectangleX(), engine.getRectangleY(), engine.getRectangleWidth(),
                engine.getRectangleHeight());

        engine.getCircles().forEach((c) ->
        {
            g.setColor(c.Color);
            g.drawOval(c.X, c.Y, c.Width, c.Height);
            g.fillOval(c.X, c.Y, c.Width, c.Height);
        });
    }
}