package de.hansinator.fun.jgp.world.world2d;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Polygon;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.World;
import de.hansinator.fun.jgp.world.world2d.actors.ActorOutput;
import de.hansinator.fun.jgp.world.world2d.senses.SensorInput;

public abstract class Body2d extends World2dObject
{
	protected static final Random rnd = Settings.newRandomSource();

	protected final List<Part> parts;

	protected final List<DrawablePart> drawableParts;

	protected final SensorInput[] inputs;

	protected final ActorOutput[] outputs;

	protected final Organism2d organism;

	private int inputNum = 0, outputNum = 0;

	public double lastSpeed = 0.0;

	public volatile boolean tagged = false;

	public Body2d(Organism2d organism, double x, double y, double dir, SensorInput[] inputs, ActorOutput[] outputs)
	{
		// TODO: fix null pointer
		super(null, x, y, dir);
		this.organism = organism;
		this.inputs = inputs;
		this.outputs = outputs;
		this.parts = new ArrayList<Part>();
		this.drawableParts = new ArrayList<DrawablePart>();
	}

	public void addBodyPart(Part part)
	{
		for (SensorInput i : part.getInputs())
			inputs[inputNum++] = i;
		for (ActorOutput o : part.getOutputs())
			outputs[outputNum++] = o;
		parts.add(part);
		if (part instanceof DrawablePart)
			drawableParts.add((DrawablePart) part);
	}
	
	public void addToWorld(World world)
	{
		for(Part part : parts)
			part.addToWorld(world);
		x = rnd.nextInt(world.getWidth());
		y = rnd.nextInt(world.getHeight());
		dir = rnd.nextDouble() * 2 * Math.PI;
	}

	public SensorInput[] getInputs()
	{
		return inputs;
	}

	public ActorOutput[] getOutputs()
	{
		return outputs;
	}

	public void sampleInputs()
	{
		for (Part p : parts)
			p.sampleInputs();
	}

	public void applyOutputs()
	{
		for (Part p : parts)
			p.applyOutputs();
	}

	public abstract void postRoundTrigger();

	public abstract void collision(World2dObject object);

	@Override
	public void draw(Graphics g)
	{
		final double sindir = Math.sin(dir);
		final double cosdir = Math.cos(dir);
		final double x_len_displace = 6.0 * sindir;
		final double y_len_displace = 6.0 * cosdir;
		final double x_width_displace = 4.0 * sindir;
		final double y_width_displace = 4.0 * cosdir;
		final double x_bottom = x - x_len_displace;
		final double y_bottom = y + y_len_displace;

		for (DrawablePart part : drawableParts)
			part.draw(g);

		Polygon p = new Polygon();
		p.addPoint(Math.round((float) (x + x_len_displace)), Math.round((float) (y - y_len_displace))); // top
																										// of
																										// triangle
		p.addPoint(Math.round((float) (x_bottom + y_width_displace)), Math.round((float) (y_bottom + x_width_displace))); // right
																															// wing
		p.addPoint(Math.round((float) (x_bottom - y_width_displace)), Math.round((float) (y_bottom - x_width_displace))); // left
																															// wing

		g.setColor(tagged ? Color.magenta : Color.red);
		g.drawPolygon(p);
		g.fillPolygon(p);

		g.setColor(Color.green);
		g.drawString("" + organism.getFitness(), Math.round((float) x) + 8, Math.round((float) y) + 8);
	}

	public class OrientationSense implements SensorInput, Part
	{

		SensorInput[] inputs = { this };

		@Override
		public int get()
		{
			// could also be sin
			return (int) (Math.cos(dir) * Organism2d.intScaleFactor);
		}

		@Override
		public SensorInput[] getInputs()
		{
			return inputs;
		}

		@Override
		public ActorOutput[] getOutputs()
		{
			return ActorOutput.emptyActorOutputArray;
		}

		@Override
		public void sampleInputs()
		{
		}

		@Override
		public void applyOutputs()
		{
		}
		
		@Override
		public void addToWorld(World world)
		{
		}
	}

	public class SpeedSense implements SensorInput, Part
	{

		SensorInput[] inputs = { this };

		@Override
		public int get()
		{
			return (int) (lastSpeed * Organism2d.intScaleFactor);
		}

		@Override
		public SensorInput[] getInputs()
		{
			return inputs;
		}

		@Override
		public ActorOutput[] getOutputs()
		{
			return ActorOutput.emptyActorOutputArray;
		}

		@Override
		public void sampleInputs()
		{
		}

		@Override
		public void applyOutputs()
		{
		}

		@Override
		public void addToWorld(World world)
		{
		}
	}

	public interface Part
	{
		public SensorInput[] getInputs();

		public ActorOutput[] getOutputs();

		public void sampleInputs();

		public void applyOutputs();
		
		public void addToWorld(World world);
	}

	public interface DrawablePart extends Part
	{
		public void draw(Graphics g);
	}
}
