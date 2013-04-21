package de.hansinator.fun.jgp.world.world2d;

import java.awt.Graphics;
import java.awt.Point;

import de.hansinator.fun.jgp.world.World;

public abstract class World2dObject extends Point.Double
{
	public double dir;

	protected final World world;
	
	public World2dObject(World world, double x, double y, double dir)
	{
		super(x, y);
		this.dir = dir;
		this.world = world;
	}

	public static double distance(Point.Double p, double x, double y)
	{
		return Math.sqrt(((x - p.x) * (x - p.x)) + ((y - p.y) * (y - p.y)));
	}

	public static double distance(Point.Double p1, Point.Double p2)
	{
		return Math.sqrt(((p2.x - p1.x) * (p2.x - p1.x)) + ((p2.y - p1.y) * (p2.y - p1.y)));
	}

	public abstract void draw(Graphics g);

}
