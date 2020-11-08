/** 
 *  This game was created by Hirad & Leo Asadi 2020-11-09.
 */
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.Timer;

/**
 *  Try to stay away from the moving objects!
 */
public class Main 
{    
    private static int frameWidth = 700;
    private static int frameHeight = 650;

    public static void main(String[] args) throws InterruptedException 
    {
        JFrame f = new JFrame();
        f.setTitle("Leo's Survival Game");
        f.getContentPane().setBackground(Color.WHITE);
        f.setResizable(false);
        f.setSize(frameWidth, frameHeight);

        LeoCanvas lc = new LeoCanvas();
        f.getContentPane().add(lc);

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
                if (e.getKeyCode() == KeyEvent.VK_UP) 
                {
                    lc.setY(-lc.getSpeed());
                } 
                else if (e.getKeyCode() == KeyEvent.VK_DOWN) 
                {
                    lc.setY(lc.getSpeed());
                } 
                else if (e.getKeyCode() == KeyEvent.VK_RIGHT) 
                {
                    lc.setX(lc.getSpeed());
                } 
                else if (e.getKeyCode() == KeyEvent.VK_LEFT) 
                {
                    lc.setX(-lc.getSpeed());
                } 
                else if (e.getKeyCode() == KeyEvent.VK_A) 
                {
                    int speed = lc.getSpeed();

                    if (speed + 1 <= 10)
                        lc.setSpeed(speed + 1);
                } 
                else if (e.getKeyCode() == KeyEvent.VK_S) 
                {
                    int speed = lc.getSpeed();

                    if (speed - 1 > 0)
                        lc.setSpeed(speed - 1);
                }
            }
        });

        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);

        // Game loop
        while (true) 
        {
            lc.moveRectangleObject();
            lc.moveCircleObjects();
            f.repaint();
            lc.detectCollisionWithRectangle();
            lc.detectCollisionWithCircles();
            Thread.sleep(10);
        }
    }

    public static class LeoCanvas extends JComponent 
    {
        private static final long serialVersionUID = 1L;
        
        private ArrayList<CircleObject> circleObjects = new ArrayList<CircleObject>();
        private double distanceToRectangle;
        private int xPlayer;
        private int yPlayer;
        private int playerWidth;
        private int playerHeight;
        private int xPlayerMiddle;
        private int yPlayerMiddle;
        private int speed;
        private int rectangleX;
        private int rectangleY;
        private int rectangleWidth;
        private int rectangleHeight;
        private int rectangleXMiddle;
        private int rectangleYMiddle;
        private int rectangleDirectionStep;        
        private int survivalTimeSeconds;
        private Random rnd = new Random();

        private Timer timer = new Timer(1000, new ActionListener() 
        {
            @Override
            public void actionPerformed(ActionEvent e) 
            {
                survivalTimeSeconds++;

                // Add a new circle every 10 seconds
                if (survivalTimeSeconds % 10 == 0)
                    circleObjects.add(createCircleObject());
            }            
        });                

        public LeoCanvas()
        {
            setDoubleBuffered(true);
            init();
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

        public void moveCircleObjects()
        {
            for (CircleObject c : circleObjects) 
            {
                if (c.Y + 1 > frameHeight || c.Y - 1 <= 0)            
                    c.YdirectionStep *= -1;

                if (c.X + 1 > frameWidth || c.X - 1 <= 0)
                    c.XdirectionStep *= -1;
            
                c.Y += c.YdirectionStep;
                c.X += c.XdirectionStep;
            }            
        }

        public void detectCollisionWithRectangle()
        {
            xPlayerMiddle = xPlayer + playerWidth/2;
            yPlayerMiddle = yPlayer + playerHeight/2;
            rectangleXMiddle = rectangleX + rectangleWidth/2;
            rectangleYMiddle = rectangleY + rectangleHeight/2;
            
            distanceToRectangle = Math.sqrt(Math.pow(xPlayerMiddle - rectangleXMiddle, 2) + Math.pow(yPlayerMiddle - rectangleYMiddle, 2));

            if (distanceToRectangle <= playerWidth * 1.2)
            {
                Collision();
            }
        }

        public void detectCollisionWithCircles()
        {
            xPlayerMiddle = xPlayer + playerWidth/2;
            yPlayerMiddle = yPlayer + playerHeight/2;

            for (CircleObject c : circleObjects) 
            {
                c.MiddleX = c.X + c.Width/2;
                c.MiddleY = c.Y + c.Height/2;
                c.DistanceToObject = Math.sqrt(Math.pow(xPlayerMiddle-c.MiddleX, 2) + Math.pow(yPlayerMiddle-c.MiddleY, 2));

                if (c.DistanceToObject <= playerWidth * 1.2)
                {
                    Collision();
                }
            }                                
        }                

        public void paint(Graphics g) 
        {
            g.setColor(Color.BLACK);
            g.drawString("Speed (A = faster, S = slower): " + speed, 5, 12);
            g.drawString("Number of balls: " + circleObjects.size(), 300, 12);
            g.drawString("Survival time (s): " + survivalTimeSeconds, 550, 12);
            g.drawLine(0, 15, frameWidth, 15);     

            g.setColor(Color.BLUE);
            g.drawOval(xPlayer, yPlayer, playerWidth, playerHeight);                        
            g.fillOval(xPlayer, yPlayer, playerWidth, playerHeight);
            
            g.setColor(Color.RED);
            g.drawRect(rectangleX, rectangleY, rectangleWidth, rectangleHeight);            
            g.fillRect(rectangleX, rectangleY, rectangleWidth, rectangleHeight);
             
            for (CircleObject c : circleObjects) 
            {
                g.setColor(c.Color); 
                g.drawOval(c.X, c.Y, c.Width, c.Height);
                g.fillOval(c.X, c.Y, c.Width, c.Height);
            }
        }

        private void init()
        {
            circleObjects = new ArrayList<CircleObject>();
            survivalTimeSeconds = 0;
            distanceToRectangle = 0;
            xPlayer = rnd.nextInt(500);
            yPlayer = rnd.nextInt(500) + 20;
            playerWidth = 25;
            playerHeight = 25;
            xPlayerMiddle = xPlayer + playerWidth/2;
            yPlayerMiddle = yPlayer + playerHeight/2;
            speed = 5; 
            rectangleX = 350;
            rectangleY = rnd.nextInt(500);
            rectangleWidth = 20;
            rectangleHeight = 100;
            rectangleDirectionStep = 10;           

            rectangleXMiddle = rectangleX + rectangleWidth/2;
            rectangleYMiddle = rectangleY + rectangleHeight/2;    

            for (int i = 0; i < rnd.nextInt(2) + 1; i++)
            {                
                circleObjects.add(createCircleObject());
            }

            timer.start();
        }

        private CircleObject createCircleObject()
        {
            int extraSize = rnd.nextInt(30);
            CircleObject c = new CircleObject();
            c.X = rnd.nextInt(500);
            c.Y = rnd.nextInt(500);            
            c.YdirectionStep = 5;

            if (rnd.nextBoolean())
                c.YdirectionStep *= -1;

            c.XdirectionStep = 5;

            if (rnd.nextBoolean())
                c.YdirectionStep *= -1;

            c.Width = 25 + extraSize;
            c.Height = 25 + extraSize;
            c.MiddleX = c.X + c.Width/2;
            c.MiddleY = c.Y + c.Height/2;
            c.DistanceToObject = 0;
            c.Color = new Color(rnd.nextFloat(), rnd.nextFloat(), rnd.nextFloat());

            return c;
        }

        private void Collision()
        {            
            JOptionPane.showMessageDialog(this, "YOU LOST! YOU MANAGED TO SURVIVE FOR " + survivalTimeSeconds + " SECONDS.");
            timer.stop();            
            init();            
        }
    }

    public static class CircleObject
    {
        public double DistanceToObject;
        public int YdirectionStep;
        public int XdirectionStep;                        
        public int X;
        public int Y;
        public int Width;
        public int Height;
        public int MiddleX;
        public int MiddleY;
        public Color Color;
    }
}

