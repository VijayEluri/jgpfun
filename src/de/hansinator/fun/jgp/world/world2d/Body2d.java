package de.hansinator.fun.jgp.world.world2d;

import java.awt.Graphics;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.common.Vec2;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.BodyDef;
import org.jbox2d.dynamics.BodyType;
import org.jbox2d.dynamics.FixtureDef;

import de.hansinator.fun.jgp.life.ActorOutput;
import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.life.IOUnit;
import de.hansinator.fun.jgp.life.SensorInput;
import de.hansinator.fun.jgp.util.Settings;
import de.hansinator.fun.jgp.world.BodyPart;

public abstract class Body2d extends AnimatableObject implements BodyPart<ExecutionUnit<World2d>>
{
	private static final int bodyCollisionRadius = Settings.getInt("bodyCollisionRadius");

	protected static final Random rnd = Settings.newRandomSource();

	@SuppressWarnings("unchecked")
	protected IOUnit<Body2d>[] parts = BodyPart.emptyBodyPartArray;

	@SuppressWarnings("unchecked")
	protected BodyPart.DrawablePart<Body2d>[] drawableParts = BodyPart.DrawablePart.emptyDrawablePartArray;
	
	private final List<CollisionListener> collisionListeners = new ArrayList<CollisionListener>();

	protected SensorInput[] inputs;

	protected ActorOutput[] outputs;

	public final ExecutionUnit<World2d> parent;
	
	private org.jbox2d.dynamics.Body body;
	
	private Shape shape;

	public org.jbox2d.dynamics.Body getBody()
	{
		return body;
	}

	public Body2d(ExecutionUnit<World2d> parent, double dir, Shape shape)
	{
		super(parent.getExecutionContext(), dir);
		this.parent = parent;
		this.shape = shape;
	}

	@SuppressWarnings("unchecked")
	public void setParts(IOUnit<Body2d>[] parts)
	{
		int i, o, d, x;

		this.parts = parts;

		// count I/O and drawable parts
		for(x = 0, i = 0, o = 0, d = 0; x < parts.length; x++)
		{
			i += parts[x].getInputs().length;
			o += parts[x].getOutputs().length;
			if (parts[x] instanceof BodyPart.DrawablePart)
				d++;
		}

		// create arrays
		inputs = new SensorInput[i];
		outputs = new ActorOutput[o];
		drawableParts = new BodyPart.DrawablePart[d];

		// collect I/O ports and drawable parts
		for(x = 0, i = 0, o = 0, d = 0; x < parts.length; x++)
		{
			// collect inputs
			for (SensorInput in : parts[x].getInputs())
				inputs[i++] = in;

			// collect outputs
			for (ActorOutput out : parts[x].getOutputs())
				outputs[o++] = out;

			// collect drawable parts
			if (parts[x] instanceof BodyPart.DrawablePart)
				drawableParts[d++]= (BodyPart.DrawablePart<Body2d>) parts[x];
		}
	}
	
	public IOUnit<Body2d>[] getParts()
	{
		return parts;
	}

	@Override
	public void attachEvaluationState(ExecutionUnit<World2d> context)
	{
		world = context.getExecutionContext();
		
		int x = rnd.nextInt(world.getWidth());
		int y = rnd.nextInt(world.getHeight());
		dir = rnd.nextDouble() * 2 * Math.PI;
		world.registerObject(this);
		
	    // box2d body
	    {
	      FixtureDef fd = new FixtureDef();
	      fd.shape = shape;
	      fd.density = 1.0f;
	      fd.friction = 0.9f;

	      BodyDef bd = new BodyDef();
	      bd.type = BodyType.DYNAMIC;
	      bd.angularDamping = 12.0f;
	      bd.linearDamping = 4.0f;
	      bd.allowSleep = false;
	      bd.position.set((float)x, (float)y);
	      body = world.getWorld().createBody(bd);
	      body.setUserData(this);
	      body.createFixture(fd);
	    }
	    
	    // attach parts after body initialization is done
		for(IOUnit<Body2d> part : parts)
			part.attachEvaluationState(this);
	}

	@Override
	public SensorInput[] getInputs()
	{
		return inputs;
	}

	@Override
	public ActorOutput[] getOutputs()
	{
		return outputs;
	}

	@Override
	public void sampleInputs()
	{
		for (IOUnit<Body2d> p : parts)
			p.sampleInputs();
	}

	@Override
	public void applyOutputs()
	{
		for (IOUnit<Body2d> p : parts)
			p.applyOutputs();
		
		dir = body.getAngle();
	}

	/**
	 * Only animatable objects can cause collisions
	 * 
	 * @return The desired radius in pixels in which this object wants to experience collisions
	 */
	public int getCollisionRadius()
	{
		return bodyCollisionRadius;
	}
	
	final synchronized public boolean addCollisionListener(CollisionListener listener)
	{
		return collisionListeners.add(listener);
	}

	final synchronized public boolean removeCollisionListener(CollisionListener listener)
	{
		return collisionListeners.remove(listener);
	}

	final void collision(Body object)
	{
		for(CollisionListener listener : collisionListeners)
			listener.onCollision(this, object);
	}
	
	public World2d getWorld()
	{
		return world;
	}
	
	@Override
	public void draw(Graphics g)
	{
		for (BodyPart.DrawablePart<Body2d> part : drawableParts)
			part.draw(g);
	}
	
	public interface CollisionListener
	{
		public void onCollision(AnimatableObject a, Body object);
	}
}
