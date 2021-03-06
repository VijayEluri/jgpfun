package de.hansinator.fun.jgp.genetics.crossover;

import java.util.List;
import java.util.Random;

import de.hansinator.fun.jgp.life.lgp.OpCode;

/**
 * 
 * @author hansinator
 */
public interface CrossoverOperator
{
	// this function shall cross two parent genomes
	// and place the new ones back into the genomes
	void cross(List<OpCode> parent1, List<OpCode> parent2, Random rnd);
}
