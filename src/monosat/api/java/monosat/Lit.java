/***************************************************************************************[Solver.cc]
 The MIT License (MIT)

 Copyright (c) 2018, Sam Bayless

 Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 associated documentation files (the "Software"), to deal in the Software without restriction,
 including without limitation the rights to use, copy, modify, merge, publish, distribute,
 sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all copies or
 substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,
 DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT
 OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 **************************************************************************************************/

package monosat;
import monosat.Logic;
import monosat.Solver;
/**
 * Literals are integers in the rance 0..nVars()*2
 * Literals come in pairs; positive literals are even, negative literals are odd.
 * Finally, there are two reserved literals:
 * lit_Undef and lit_Error, with values -2 and -1, respectively.
 * <p>
 * In an ideal world, this would be _just_ an integer, or a type-checked value class of size 32 bits.
 * However, to deal with
 */
final public class Lit {
    public final static Lit Undef = new Lit(-2, true);
    public final static Lit Error = new Lit(-1, true);
    protected final Solver solver;

    //private final Lit neg; //every literal also has a pointer to its negation.
    //The value of this literal
    protected int l = -2;

/*
    *//**
     * Typically, users will create literals using Solver.newLit(), rather than constructing them directly.
     *//*
    protected Lit(Solver solver) {
        this.l = -2;
        this.solver=solver;
        //this.neg = Error;
    }*/

    private Lit(int lit, boolean define_literal) {
        //used to define static lits
        assert(lit<=0);
        this.solver=null;
        this.l = lit;
        //neg = null;
    }

    protected Lit(Solver solver,int literal) {
        assert (literal >= 0);
        this.l =literal;
        this.solver=solver;
        //this.neg = new Lit(l + 1, this);
    }

    public Solver getSolver(){
        if(solver==null){
            throw new RuntimeException("Cannot access solver for " + toString());
        }
        return solver;
    }

    /*private Lit(int l, Lit neg) {
        this.l = l;
        this.neg = neg;
    }*/


    /**
     * Check whether the literal has a negation sign.
     *
     * @return True if the literal is negative, false if it is positive.
     */
    public boolean sign() {
        return (l & 1) == 1;
    }

    public Lit negate() {
        return solver.not(this);
    }

    public Lit abs() {
        if (sign()) {
            return solver.not(this);
        } else {
            return this;
        }
    }

    /**
     * Convert the literal into dimacs format
     *
     * @return
     */
    public int toDimacs() {
        return ((l / 2) + 1) * (this.sign() ? -1 : 1);
    }

    @Override
    public String toString() {
        if(l==-2){
            return "Lit_Error";
        }else if(l==-2){
            return "Lit_Undef";
        }
        return "Lit" + l;
    }

    /**
     * Convert the literal into a variable.
     *
     * @return
     */
    public int toVar() {
        return l / 2;
    }


    /**
     * Convert the literal into an integer.
     *
     * @return
     */
    public int toInt() {
        return l;
    }


    /**
     * Query the model in the solver, throwing an exception if the literal is unassigned in the model.
     * This can happen only if the literal is not a decision literal.
     */
    public boolean value() throws RuntimeException {
        return getSolver().getValue(this);
    }
    /**
     * Query the model in the solver.
     * If defaultVal is LBool.Undef, this will throw an exception if the literal is unassigned.
     * Else, if the literal is unassigned, defaultVal will be returned.
     */
    public boolean value(LBool defaultVal) throws RuntimeException {
        return getSolver().getValue(this, defaultVal);
    }
    /**
     * After a solve call, non-decision literals may or may not be assigned to a value.
     * Unassigned literals will have the value LBool.Undef;
     */
    public LBool possibleValue(){
        return getSolver().getPossibleValue(this);
    }


}