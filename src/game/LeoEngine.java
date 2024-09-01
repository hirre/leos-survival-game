package game;

import game.models.Circle;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;
import javax.swing.Timer;

public class LeoEngine
{
    private final List<Circle> Circles = Collections.synchronizedList(new ArrayList<>());
    private final Random rnd = new Random();
    private final int frameWidth;
    private final int frameHeight;
    private final int directionChangeDelayMs;
    private Runnable callback;
    private double distanceToRectangle;
    private int xPlayer;
    private int yPlayer;
    private int playerWidth;
    private int rectangleX;
    private int rectangleY;
    private int rectangleWidth;
    private int rectangleHeight;
    private int rectangleXMiddle;
    private int rectangleYMiddle;
    private int rectangleDirectionStep;
    private int playerHeight;
    private int xPlayerMiddle;
    private int yPlayerMiddle;
    private int survivalTimeSeconds;
    private int score;
    private int speed;

    private final Timer timer;

    public LeoEngine(int frameWidth, int frameHeight, int directionChangeDelayMs)
    {
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.directionChangeDelayMs = directionChangeDelayMs;

        this.timer = new Timer(1000, (ActionEvent e) ->
        {
            survivalTimeSeconds++;

            // Add a new circle every 20 seconds
            if (survivalTimeSeconds % 20 == 0)
            {
                Circles.add(createCircle());
            }
        });
    }

    public void registerCollisionCallback(Runnable callback)
    {
        this.callback = callback;
    }

    public void init()
    {
        Circles.clear();

        survivalTimeSeconds = 0;
        distanceToRectangle = 0;
        xPlayer = rnd.nextInt(500);
        yPlayer = rnd.nextInt(500) + 20;
        playerWidth = 25;
        playerHeight = 25;
        xPlayerMiddle = xPlayer + playerWidth / 2;
        yPlayerMiddle = yPlayer + playerHeight / 2;
        speed = 5;
        rectangleX = 350;
        rectangleY = rnd.nextInt(500);
        rectangleWidth = 20;
        rectangleHeight = 100;
        rectangleDirectionStep = 10;
        score = 0;

        rectangleXMiddle = rectangleX + rectangleWidth / 2;
        rectangleYMiddle = rectangleY + rectangleHeight / 2;

        for (int i = 0; i < rnd.nextInt(2) + 1; i++)
        {
            Circles.add(createCircle());
        }

        timer.start();
    }

    public List<Circle> getCircles()
    {
        return Circles;
    }

    public int survivalTimeSeconds()
    {
        return survivalTimeSeconds;
    }

    public int getScore()
    {
        return score;
    }

    public int getframeWidth()
    {
        return frameWidth;
    }

    public int getXplayer()
    {
        return xPlayer;
    }

    public int getYplayer()
    {
        return yPlayer;
    }

    public int getPlayerWidth()
    {
        return playerWidth;
    }

    public int getPlayerHeight()
    {
        return playerHeight;
    }

    public int getRectangleX()
    {
        return rectangleX;
    }

    public int getRectangleY()
    {
        return rectangleY;
    }

    public int getRectangleWidth()
    {
        return rectangleWidth;
    }

    public int getRectangleHeight()
    {
        return rectangleHeight;
    }

    public void calculateScore()
    {
        score += Math.max(Circles.size() * survivalTimeSeconds / speed, 1);
    }

    public void setX(int i)
    {
        if (i >= 0 && (xPlayer + playerWidth + i) < frameWidth - 15)
            xPlayer += i;
        else if (i < 0 && (xPlayer + i) > 0)
            xPlayer += i;
    }

    public void setY(int i)
    {
        if (i >= 0 && (yPlayer + playerHeight * 2.5 + i) < frameHeight)
            yPlayer += i;
        else if (i < 0 && (yPlayer + i) > 12)
            yPlayer += i;
    }

    public void setSpeed(int i)
    {
        speed = i;
    }

    public int getSpeed()
    {
        return speed;
    }

    public void moveRectangleObject()
    {
        if (rectangleY + 1 > frameHeight || rectangleY - 1 <= 0)
            rectangleDirectionStep *= -1;

        rectangleY += rectangleDirectionStep;
    }

    public void moveCircles()
    {
        Circles.forEach((c) ->
        {
            Date now = Calendar.getInstance().getTime();

            long diff = now.getTime() - c.LastEdited.getTime();
            Date lastEdited = Calendar.getInstance().getTime();
            boolean[] changed = new boolean[1];
            changed[0] = false;

            if (diff > directionChangeDelayMs)
                changed[0] = true;

            // Detect hit with frame floor and ceiling and flip direction
            if (changed[0] && (c.Y + 1 > (frameHeight - c.Height) || c.Y - 1 <= 0))
            {
                c.YdirectionStep *= -1;
                c.LastEdited = lastEdited;
            }

            // Detect hit with frame walls and flip direction
            if (changed[0] && (c.X + 1 > (frameHeight - c.Width) || c.X - 1 <= 0))
            {
                c.XdirectionStep *= -1;
                c.LastEdited = lastEdited;
            }

            // Detect hit with rectangle walls and flip direction
            if (changed[0] && (c.X + 1 > (rectangleX - c.Width)
                    && c.Y <= (rectangleY + rectangleHeight) && c.Y >= rectangleY))
            {
                c.XdirectionStep *= -1;
                c.LastEdited = lastEdited;
            }

            // Detect hit with rectangle floor and ceiling and flip direction
            if (changed[0] && (c.Y + 1 > (rectangleY - c.Height)
                    && c.X <= (rectangleX + rectangleWidth) && c.X >= rectangleX))
            {
                c.YdirectionStep *= -1;
                c.LastEdited = lastEdited;
            }

            // Detect hit with other circles and flip direction
            Circles.forEach((v) ->
            {
                if (c != v)
                {
                    double dist = Math.sqrt(Math.pow(c.MiddleX - v.MiddleX, 2)
                            + Math.pow(c.MiddleY - v.MiddleY, 2));
                    long diff2 = now.getTime() - v.LastEdited.getTime();
                    boolean otherChanged = (diff2 > directionChangeDelayMs);

                    if (otherChanged && changed[0] && (dist <= c.Height))
                    {
                        c.YdirectionStep *= -1;
                        c.LastEdited = lastEdited;
                    }

                    if (otherChanged && changed[0] && (dist <= c.Width))
                    {
                        c.XdirectionStep *= -1;
                        c.LastEdited = lastEdited;
                    }
                }
            });

            c.Y += c.YdirectionStep;
            c.X += c.XdirectionStep;
        });
    }

    public void detectCollisionWithRectangle()
    {
        xPlayerMiddle = xPlayer + playerWidth / 2;
        yPlayerMiddle = yPlayer + playerHeight / 2;
        rectangleXMiddle = rectangleX + rectangleWidth / 2;
        rectangleYMiddle = rectangleY + rectangleHeight / 2;

        distanceToRectangle = Math.sqrt(Math.pow(xPlayerMiddle - rectangleXMiddle, 2)
                + Math.pow(yPlayerMiddle - rectangleYMiddle, 2));

        if (distanceToRectangle <= playerWidth * 1.2)
        {
            Collision();
        }
    }

    public void detectCollisionWithCircles()
    {
        xPlayerMiddle = xPlayer + playerWidth / 2;
        yPlayerMiddle = yPlayer + playerHeight / 2;
        final boolean[] collision = new boolean[1];

        Circles.forEach((c) ->
        {
            c.MiddleX = c.X + c.Width / 2;
            c.MiddleY = c.Y + c.Height / 2;
            c.DistanceToObject = Math.sqrt(Math.pow(xPlayerMiddle - c.MiddleX, 2)
                    + Math.pow(yPlayerMiddle - c.MiddleY, 2));

            if (c.DistanceToObject <= playerWidth * 1.2)
            {
                collision[0] = true;
            }
        });

        if (collision[0])
            Collision();
    }

    private Circle createCircle()
    {
        int extraSize = rnd.nextInt(10);
        Circle c = new Circle();
        c.X = rnd.nextInt(500);
        c.Y = rnd.nextInt(500);
        c.YdirectionStep = 5;

        if (rnd.nextBoolean())
            c.YdirectionStep *= -1;

        c.XdirectionStep = 5;

        if (rnd.nextBoolean())
            c.YdirectionStep *= -1;

        c.Width = 10 + extraSize;
        c.Height = 10 + extraSize;
        c.MiddleX = c.X + c.Width / 2;
        c.MiddleY = c.Y + c.Height / 2;
        c.DistanceToObject = 0;
        c.Color = new Color(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat());

        return c;
    }

    private void Collision()
    {
        if (callback != null)
            callback.run();

        timer.stop();
        init();
    }
}
