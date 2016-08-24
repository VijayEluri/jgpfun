package de.hansinator.fun.jgp.world.world2d.senses;

import java.util.List;

import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;

import de.hansinator.fun.jgp.genetics.Mutation;
import de.hansinator.fun.jgp.genetics.ValueGene.DoubleGene;
import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.simulation.EvolutionaryProcess;
import de.hansinator.fun.jgp.world.BodyPart;
import de.hansinator.fun.jgp.world.world2d.Body2d;

/**
 * 
 * @author hansinator
 */
public class WallSense implements SensorInput, BodyPart<Body2d>
{
	private final double wallSenseScaleFactor;
	
	private float worldWidth, worldHeight, angle = 0.0f;

	private Body body;
	
	private Vec2 position;

	SensorInput[] inputs = { this };

	private double lastSenseVal = 0;
	
	public WallSense(double wallSenseScaleFactor)
	{
		this.wallSenseScaleFactor = wallSenseScaleFactor;
	}

	@Override
	public double get()
	{
		return lastSenseVal;
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
		position = body.getPosition();
		angle = body.getAngle();
	}

	@Override
	public void applyOutputs()
	{
		// pickup wallsense in applyOutputs before coordinates are clipped
		float temp = 0.0f;

		if ((position.x <= 0) || (position.x >= worldWidth))
		{
			// TODO: fix abs stuff
			temp = (float) Math.min(Math.abs(2 * Math.PI - angle), Math.abs(Math.PI - angle));
			if ((position.y <= 0) || (position.y >= worldHeight))
				temp = (float) Math.min(temp, Math.min(Math.abs(0.5 * Math.PI - angle), Math.abs(1.5 * Math.PI - angle)));
		} else if ((position.y <= 0) || (position.y >= worldHeight))
			temp = (float) Math.min(Math.abs(0.5 * Math.PI - angle), Math.abs(1.5 * Math.PI - angle));

		lastSenseVal = temp * wallSenseScaleFactor;
	}

	@Override
	public void attachEvaluationState(Body2d context)
	{
		this.worldWidth = context.getWorld().getWidth();
		this.worldHeight = context.getWorld().getHeight();
		this.body = context.getBody();
	}
	
	public static class Gene extends IOUnit.Gene<Body2d>
	{
		private DoubleGene wallSenseScaleFactor = new DoubleGene(1.0, 500);
		
		Mutation[] mutations = { wallSenseScaleFactor };
		
		@Override
		public List<de.hansinator.fun.jgp.genetics.Gene> getChildren()
		{
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public de.hansinator.fun.jgp.life.IOUnit.Gene<Body2d> replicate()
		{
			WallSense.Gene gene = new WallSense.Gene();
			gene.wallSenseScaleFactor.setValue(wallSenseScaleFactor.getValue());
			return gene;
		}

		@Override
		public IOUnit<Body2d> express(Body2d context)
		{
			return new WallSense(wallSenseScaleFactor.getValue() * EvolutionaryProcess.intScaleFactor);
		}

		@Override
		public int getInputCount()
		{
			return 1;
		}

		@Override
		public int getOutputCount()
		{
			return 0;
		}

		@Override
		public Mutation[] getMutations()
		{
			return mutations;
		}
	}
}
