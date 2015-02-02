package de.hansinator.fun.jgp.simulation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.hansinator.fun.jgp.genetics.Genome;
import de.hansinator.fun.jgp.life.ExecutionUnit;
import de.hansinator.fun.jgp.world.World;
import de.hansinator.fun.jgp.world.world2d.World2d;

/**
 * 
 * @author hansinator
 */
public class WorldSimulation
{

	// todo: have world object automatically add themselves to a legend that can
	// be drawn onto the screen (bottom?)
	// todo: in a later simulation creation dialogue, have categories for
	// "simple" stuff (bodiss, sesses) and more custom stuff.. a bit like the
	// clonk menu?!

	// (it is questionable if this must be included in propertiers... it's fine
	// if it's hardcoded for a while or two!)
	private int fpsMax = 70;

	// (it is questionable if this must be included in propertiers... it's fine
	// if it's hardcoded for a while or two!)
	private int roundsMod = 800;

	private volatile boolean running = true;

	private volatile boolean paused = false;

	private volatile boolean slowMode = false;
	
	private volatile int currentRound;

	private final Object runLock = new Object();

	private final ThreadPoolExecutor pool;

	public final World world;

	public static final int ROUNDS_PER_GENERATION = 4000;
	
	private int rps;
	
	private final ConcurrentHashMap<ExecutionUnit<? extends World>, Genome> organismsByGenome = new ConcurrentHashMap<ExecutionUnit<? extends World>, Genome>();
	
	final List<SimulationViewUpdateListener> viewUpdateListeners = new ArrayList<SimulationViewUpdateListener>();

	// XXX distinguish only between generational and continuous simulation, not
	// world and mona lisa; mona lisa needs to be implemented by a scenario only
	public WorldSimulation(World world)
	{
		this.world = world;
		pool = (ThreadPoolExecutor) Executors.newFixedThreadPool((Runtime.getRuntime().availableProcessors() * 2) - 1);
		pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
	}

	public void initialize()
	{
		synchronized (runLock)
		{
			world.resetState();
			running = true;
			paused = false;
			rps = 0;
		}
	}

	/*
	 * TODO re-think generation runtime stat calculation to be better suited for re-entrance
	 */
	@SuppressWarnings({"unchecked" })
	public Genome[] evaluate(Genome[] generation)
	{
		long start = System.currentTimeMillis();
		long lastStatTime = start;
		int lastStatRound = 0;
		ExecutionUnit<? extends World>[] organisms = new ExecutionUnit[generation.length];
		
		// clear organism map for each new round
		organismsByGenome.clear();

		// synthesize organisms
		for (int i = 0; i < generation.length; i++)
		{
			//TODO move these into a Genome.synthesise function so we don't need fitnessevaluator knowledge here
			organisms[i] = generation[i].getRootGene().express((World2d) world);
			generation[i].getFitnessEvaluator().attach(organisms[i]);
			
			// record organism genome relationship
			organismsByGenome.put(organisms[i], generation[i]);
		}

		synchronized (runLock)
		{
			for (currentRound = 0; running && (currentRound < ROUNDS_PER_GENERATION); currentRound++)
			{
				while (paused)
					Thread.yield();

				singleStep(organisms);

				// calc stats and draw stuff
				// TODO: try to decouple this from pure generation running
				if (slowMode || (currentRound % roundsMod) == 0)
				{
					final long time = System.currentTimeMillis() - lastStatTime;
					lastStatTime = System.currentTimeMillis();
					this.rps = time > 0 ? (int) (((currentRound - lastStatRound) * 1000) / time) : 1;
					lastStatRound = currentRound;

					// update views
					updateSimulationViews();

					// slow down things artificially
					if (slowMode && (time < (1000 / fpsMax)))
						try
						{
							Thread.sleep((1000 / fpsMax) - time);
						} catch (InterruptedException ex)
						{
							Logger.getLogger(WorldSimulation.class.getName()).log(Level.SEVERE, null, ex);
						}
				}
			}
		}

		// simulation statistics
		System.out.println("");
		System.out.println("RPS: " + (ROUNDS_PER_GENERATION * 1000) / (System.currentTimeMillis() - start));

		// prepare world for next generation
		world.resetState();

		return generation;
	}

	/**
	 * Stop the current evaluation.
	 */
	public void stop()
	{
		running = false;
	}

	/**
	 * Evaluate a single simulation round.
	 * 
	 * @param organisms
	 */
	@SuppressWarnings("rawtypes")
	private void singleStep(ExecutionUnit[] organisms)
	{
		final CountDownLatch cb = new CountDownLatch(organisms.length);

		/*
		 * Evaluate each organism non-blocking as a thread using an
		 * ExecutorService. The count-down latch will be count down when the
		 * organism is done living one round.
		 * 
		 * Use an anonymous runnable here to decouple threading logic from
		 * the unit under evaluation. It doesn't make me happy but it's not
		 * as ugly as having each organism implement runnable.
		 */
		for (final ExecutionUnit unit : organisms)
		{
			pool.execute(new Runnable() {
				
				@Override
				public void run()
				{
					try
					{
						unit.execute();
					} catch (Exception ex)
					{
						Logger.getLogger(ExecutionUnit.class.getName()).log(Level.SEVERE, null, ex);
					}

					cb.countDown();
				}
			});
		}

		// wait for all organisms to finish
		try
		{
			cb.await();
		} catch (InterruptedException ex)
		{
			Logger.getLogger(WorldSimulation.class.getName()).log(Level.SEVERE, null, ex);
		}

		// run world
		world.animate();
	}

	public boolean isSlowMode()
	{
		return slowMode;
	}

	public void setSlowMode(boolean slowMode)
	{
		this.slowMode = slowMode;
	}

	public int getRoundsMod()
	{
		return roundsMod;
	}

	public void setRoundsMod(int roundsMod)
	{
		this.roundsMod = roundsMod == 0 ? 1 : roundsMod;
	}

	public int getFps()
	{
		return fpsMax;
	}

	public void setFps(int fpsMax)
	{
		this.fpsMax = fpsMax == 0 ? 1 : fpsMax;
	}

	public void setPaused(boolean paused)
	{
		this.paused = paused;
	}

	public boolean isPaused()
	{
		return paused;
	}
	
	public int getCurrentRound()
	{
		return currentRound;
	}
	
	public int getRPS()
	{
		return rps;
	}
	
	
	final synchronized public boolean addViewUpdateListener(SimulationViewUpdateListener listener)
	{
		return viewUpdateListeners.add(listener);
	}

	final synchronized public boolean removeViewUpdateListener(SimulationViewUpdateListener listener)
	{
		return viewUpdateListeners.remove(listener);
	}

	final void updateSimulationViews()
	{
		for(SimulationViewUpdateListener listener : viewUpdateListeners)
			listener.onViewUpdate();
	}

	public static interface SimulationViewUpdateListener
	{
		public void onViewUpdate();
	}
	
	public Map<ExecutionUnit<? extends World>, Genome> getOrganismsByGenomeMap()
	{
		return java.util.Collections.unmodifiableMap(organismsByGenome);
	}
}
