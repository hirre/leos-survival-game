package game.models;

import java.awt.Color;
import java.util.Calendar;
import java.util.Date;

public class Circle
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
        public Date LastEdited = Calendar.getInstance().getTime();
}
