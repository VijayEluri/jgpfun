package jgpfun;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import jgpfun.world2d.World2d;

/**
 *
 * @author hansinator
 */
public abstract class AbstractPopulationManager {

    public static final int foodTolerance = 10;

    public static final int maxMutations = 3;

    protected final Random rnd;

    protected final ThreadPoolExecutor pool;

    protected final int progSize;

    protected List<Organism> ants;

    protected final World2d world;

    protected int gen = 0;

    protected boolean slowMode;

    public volatile int roundsMod = 800;


    public AbstractPopulationManager(World2d world, int popSize, int progSize) {
        this.world = world;
        this.progSize = progSize;

        pool = (ThreadPoolExecutor) Executors.newFixedThreadPool((Runtime.getRuntime().availableProcessors() * 2) - 1);
        pool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        ants = new ArrayList<Organism>(popSize);
        rnd = new SecureRandom();

        for (int i = 0; i < popSize; i++) {
            ants.add(Organism.randomOrganism(world.worldWidth, world.worldHeight, progSize, world.foodFinder));
        }
    }


    public abstract void step();


    public abstract void printStats(long rps);


    public abstract int newGeneration();


    public boolean isSlowMode() {
        return slowMode;
    }


    public void setSlowMode(boolean slowMode) {
        this.slowMode = slowMode;
    }

}
