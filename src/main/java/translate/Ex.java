package Translate;

import Tree.EXPR;

public class Ex extends Exp {
    Tree.Exp exp;

    public Ex(Tree.Exp e) {
        this.exp = e;
    }

    @Override
    public Tree.Exp unEx() {
        return exp;
    }

    @Override
    public Tree.Stm unNx() {
        return new EXPR(exp);
    }

    @Override
    public Tree.Stm unCx(Temp.Label t, Temp.Label f) {
        // Se a expressão for diferente de zero, salta para t (true), senão para f (false)
        return new Tree.CJUMP(Tree.CJUMP.NE, exp, new Tree.CONST(0), t, f);
    }
}