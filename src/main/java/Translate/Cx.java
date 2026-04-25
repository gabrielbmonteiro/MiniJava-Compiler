package Translate;

import Temp.Label;
import Temp.Temp;

public abstract class Cx extends Exp {

    @Override
    public Tree.Exp unEx() {
        Temp r = new Temp();
        Label t = new Label();
        Label f = new Label();

        return new Tree.ESEQ(
                new Tree.SEQ(new Tree.MOVE(new Tree.TEMP(r), new Tree.CONST(1)),
                        new Tree.SEQ(unCx(t, f),
                                new Tree.SEQ(new Tree.LABEL(f),
                                        new Tree.SEQ(new Tree.MOVE(new Tree.TEMP(r), new Tree.CONST(0)),
                                                new Tree.LABEL(t))))),
                new Tree.TEMP(r));
    }

    @Override
    public Tree.Stm unNx() {
        Label join = new Label();
        return new Tree.SEQ(unCx(join, join), new Tree.LABEL(join));
    }

    @Override
    public abstract Tree.Stm unCx(Label t, Label f);
}