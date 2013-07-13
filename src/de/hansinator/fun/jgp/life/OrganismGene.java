package de.hansinator.fun.jgp.life;

import de.hansinator.fun.jgp.genetics.Gene;
import de.hansinator.fun.jgp.world.World;
import de.hansinator.fun.jgp.world.world2d.Body2d;

/*
 * XXX refactor this into a Gene (OrganismGene) somehow
 */
public class OrganismGene<E extends World> implements Gene<Organism<E>, E>
{

	private final ExecutionUnit.Gene brainGene;

	private final IOUnit.Gene<E> bodyGene;

	private final FitnessEvaluator fitnessEvaluator;

	private int fitness;

	private int exonSize;

	public OrganismGene(IOUnit.Gene<E> bodyGene, ExecutionUnit.Gene brainGene, FitnessEvaluator evaluator)
	{
		this.brainGene = brainGene;
		this.bodyGene = bodyGene;
		this.fitnessEvaluator = evaluator;
	}


	@Override
	public OrganismGene<E> replicate()
	{
		return new OrganismGene<E>(bodyGene, brainGene.replicate(), fitnessEvaluator.replicate());
	}


	// make random changes to random locations in the genome
	public void mutate(int mutCount)
	{
		// determine amount of mutations, minimum 1
		// int mutCount = maxMutations;
		// int mutCount = randomR.Next(maxMutations) + 1;

		for (int i = 0; i < mutCount; i++)
			mutate();
	}


	public int size()
	{
		return brainGene.size();
	}


	public int getFitness()
	{
		return fitness;
	}


	//XXX make thism something like "addEvaluation" to add an evaluation with world parameter reference and datetime and stuff so a genome is multi-evaluatable
	public void setFitness(int fitness)
	{
		this.fitness = fitness;
	}


	public int getExonSize()
	{
		return exonSize;
	}


	public void setExonSize(int exonSize)
	{
		this.exonSize = exonSize;
	}


	@Override
	public void mutate()
	{
		brainGene.mutate();
	}


	@SuppressWarnings("unchecked")
	@Override
	public Organism<E> express(E context)
	{
		int i, o, x;

		// create organism
		Organism<E> org = new Organism<E>(this, fitnessEvaluator);

		// create and attach body
		IOUnit<E>[] bodies = new IOUnit[] { bodyGene.express(org) };
		org.setIOUnits(bodies);

		//XXX move this somewhere else
		//listen for collsions
		((Body2d)bodies[0]).addCollisionListener(fitnessEvaluator);

		// count I/O ports
		for(x = 0, i = 0, o = 0; x < bodies.length; x++)
		{
			i += bodies[x].getInputs().length;
			o += bodies[x].getOutputs().length;
		}

		// create I/O arrays
		SensorInput[] inputs = (i==0)?SensorInput.emptySensorInputArray:new SensorInput[i];
		ActorOutput[] outputs = (o==0)?ActorOutput.emptyActorOutputArray:new ActorOutput[o];

		// collect I/O
		for(x = 0, i = 0, o = 0; x < bodies.length; x++)
		{
			// collect inputs
			for (SensorInput in : bodies[x].getInputs())
				inputs[i++] = in;

			// collect outputs
			for (ActorOutput out : bodies[x].getOutputs())
				outputs[o++] = out;
		}

		// create brain
		ExecutionUnit brain = brainGene.express(org);
		// BaseMachine brain = EvoCompiler.compile(registerCount, numInputs,
		// program.toArray(new OpCode[program.size()]));

		// attach inputs and outputs to brain
		brain.setInputs(inputs);
		brain.setOutputs(outputs);

		// attach brain
		org.setExecutionUnit(brain);

		//attach to evaluation state
		org.addToWorld(context);

		// return assembled organism
		return org;
	}
}