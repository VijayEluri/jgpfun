package de.hansinator.fun.jgp.life.lgp.operations;

/**
 * 
 * @author dahmen
 */
public class OpDiv implements Operation
{

	@Override
	public int execute(int src1, int src2)
	{
		if (src2 != 0)
			return src1 / src2;
		else return Integer.MAX_VALUE;
	}

}
