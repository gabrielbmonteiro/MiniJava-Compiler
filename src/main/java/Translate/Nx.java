package Translate;

public class Nx extends Exp {
    Tree.Stm stm;

    public Nx(Tree.Stm s) {
        this.stm = s;
    }

    @Override
    public Tree.Exp unEx() {
        return null;
    }

    @Override
    public Tree.Stm unNx() {
        return stm;
    }

    @Override
    public Tree.Stm unCx(Temp.Label t, Temp.Label f) {
        return null;
    }
}