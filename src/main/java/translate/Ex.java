package translate;

import tree.EXPR;

public class Ex extends Exp {
    tree.Exp exp;

    public Ex(tree.Exp e) {
        this.exp = e;
    }

    @Override
    public tree.Exp unEx() {
        return exp;
    }

    @Override
    public tree.Stm unNx() {
        return new EXPR(exp);
    }

    @Override
    public tree.Stm unCx(temp.Label t, temp.Label f) {
        // Se a expressão for diferente de zero, salta para t (true), senão para f (false)
        return new tree.CJUMP(tree.CJUMP.NE, exp, new tree.CONST(0), t, f);
    }
}