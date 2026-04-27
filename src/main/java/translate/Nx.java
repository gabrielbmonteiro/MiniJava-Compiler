package translate;

public class Nx extends Exp {
    tree.Stm stm;

    public Nx(tree.Stm s) {
        this.stm = s;
    }

    @Override
    public tree.Exp unEx() {
        return null;
    }

    @Override
    public tree.Stm unNx() {
        return stm;
    }

    @Override
    public tree.Stm unCx(temp.Label t, temp.Label f) {
        return null;
    }
}