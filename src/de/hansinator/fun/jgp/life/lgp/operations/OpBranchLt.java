package de.hansinator.fun.jgp.life.lgp.operations;

/**
 * 
 * @author dahmen
 */
public class OpBranchLt implements Operation, BranchOperation
{

	@Override
	public int execute(int src1, int src2)
	{
		if (src1 < src2)
			return 1;

		return 0;
	}

}
