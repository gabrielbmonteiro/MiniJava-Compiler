package translate;

public abstract class Exp {
    public abstract tree.Exp unEx();

    public abstract tree.Stm unNx();

    public abstract tree.Stm unCx(temp.Label t, temp.Label f);
}