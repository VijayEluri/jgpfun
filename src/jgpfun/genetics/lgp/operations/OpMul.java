/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jgpfun.genetics.lgp.operations;

/**
 *
 * @author dahmen
 */
public class OpMul implements Operation {

    @Override
    public int execute(int src1, int src2) {
        return src1 * src2;
    }

}