package Frame;

import java.util.List;
import Temp.Label;
import Temp.Temp;
import Temp.TempList;

public abstract class Frame {
    public Label name;
    public List<Access> formals;

    public abstract Frame newFrame(Label name, List<Boolean> formals);

    public abstract Access allocLocal(boolean escape);

    public abstract Temp RV();

    public abstract Temp FP();

    public abstract int wordSize();

    public abstract Tree.Exp externalCall(String func, List<Tree.Exp> args);

    public abstract Tree.Stm procEntryExit1(Tree.Stm body);

    public abstract TempList calldefs();

    public abstract Assem.InstrList procEntryExit2(Assem.InstrList body);

}