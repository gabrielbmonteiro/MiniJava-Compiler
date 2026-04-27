package frame;

import java.util.List;
import temp.Label;
import temp.Temp;
import temp.TempList;

public abstract class Frame {
    public Label name;
    public List<Access> formals;

    public abstract Frame newFrame(Label name, List<Boolean> formals);

    public abstract Access allocLocal(boolean escape);

    public abstract Temp RV();

    public abstract Temp FP();

    public abstract int wordSize();

    public abstract tree.Exp externalCall(String func, List<tree.Exp> args);

    public abstract tree.Stm procEntryExit1(tree.Stm body);

    public abstract TempList calldefs();

    public abstract assem.InstrList procEntryExit2(assem.InstrList body);

    public abstract TempList registers();

    public abstract assem.InstrList procEntryExit3(assem.InstrList body);

}