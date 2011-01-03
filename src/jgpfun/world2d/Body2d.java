package jgpfun.world2d;

import jgpfun.Food;

public class Body2d {

    public final WallSense wallSense;

    public final Motor2d motor;

    public final FoodFinder foodFinder;

    public Food food;

    public double dir;

    public double x;

    public double y;


    public Body2d(double x, double y, double dir, FoodFinder foodFinder, WallSense wallSense) {
        this.x = x;
        this.y = y;
        this.dir = dir;

        this.motor = new TankMotor(this);
        this.foodFinder = foodFinder;
        this.wallSense = wallSense;
        wallSense.setBody(this);
    }

}
