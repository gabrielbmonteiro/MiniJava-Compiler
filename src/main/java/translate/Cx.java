package translate;

import temp.Label;
import temp.Temp;

public abstract class Cx extends Exp {

    @Override
    public tree.Exp unEx() {
        Temp r = new Temp();
        Label t = new Label();
        Label f = new Label();

        return new tree.ESEQ(
                new tree.SEQ(new tree.MOVE(new tree.TEMP(r), new tree.CONST(1)),
                        new tree.SEQ(unCx(t, f),
                                new tree.SEQ(new tree.LABEL(f),
                                        new tree.SEQ(new tree.MOVE(new tree.TEMP(r), new tree.CONST(0)),
                                                new tree.LABEL(t))))),
                new tree.TEMP(r));
    }

    @Override
    public tree.Stm unNx() {
        Label join = new Label();
        return new tree.SEQ(unCx(join, join), new tree.LABEL(join));
    }

    @Override
    public abstract tree.Stm unCx(Label t, Label f);
}