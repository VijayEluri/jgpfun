package jgpfun;

import jgpfun.operations.OpAbs;
import jgpfun.operations.OpAdd;
import jgpfun.operations.OpBranchGt;
import jgpfun.operations.OpBranchLt;
import jgpfun.operations.OpDiv;
import jgpfun.operations.OpMax;
import jgpfun.operations.OpMin;
import jgpfun.operations.OpMod;
import jgpfun.operations.OpMov;
import jgpfun.operations.OpMul;
import jgpfun.operations.OpNeg;
import jgpfun.operations.OpSin;
import jgpfun.operations.OpSqrt;
import jgpfun.operations.OpSub;
import jgpfun.operations.Operation;

/**
 *
 * @author hansinator
 */
public class EvoVM2 {

    static Operation[] ops;

    static {
        //compatible instruction set
        //ops = new Operation[]{new OpAdd(), new OpSub(), new OpMul(), new OpDiv(), new OpMod()};

        //extended instruction set
        ops = new Operation[]{
                    new OpAdd(),
                    new OpSub(),
                    new OpMul(),
                    new OpDiv(),
                    new OpMod(),
                    new OpSqrt(),
                    new OpNeg(),
                    new OpMin(),
                    new OpMax(),
                    new OpAbs(),
                    new OpSin(),
                    new OpMov()
                    //new OpBranchLt(),
                    //new OpBranchGt()
                };
    }
    OpCode[] program;
    int[] regs;

    public EvoVM2(int numregs, OpCode[] program) {
        this.program = program;
        regs = new int[numregs];

        for (int pc = 0; pc < program.length; pc++) {
            OpCode curop = program[pc];

            curop.src1 = Math.abs(curop.src1) % numregs;
            if (!curop.immediate) {
                curop.src2 = Math.abs(curop.src2) % numregs;
            }
            curop.trg = Math.abs(curop.trg) % numregs;
            curop.op = Math.abs(curop.op) % ops.length;
        }
    }

    public void run() throws Exception {
        for (int pc = 0; pc < program.length; pc++) {
            try {
                execute(pc);
            } catch (NoOp ex) {
            } catch (Branch ex) {
                pc++;
            }
        }
    }

    public void execute(int pc) throws Exception {
        OpCode curop = program[pc];

        //execute the operation
        regs[curop.trg] = ops[curop.op].execute(regs[curop.src1], (curop.immediate ? curop.src2 : regs[curop.src2]));
    }
}