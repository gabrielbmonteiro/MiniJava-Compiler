package Mips;

import java.util.ArrayList;
import java.util.List;
import Frame.Access;
import Frame.Frame;
import Temp.Label;
import Temp.Temp;
import Tree.*;

public class MipsFrame extends Frame {

    private int localOffset = 0;

    private static final Temp FP = new Temp();
    private static final Temp RV = new Temp();

    public MipsFrame() {}

    private MipsFrame(Label name, List<Boolean> formals) {
        this.name = name;
        this.formals = new ArrayList<>();
        for (Boolean escape : formals) {
            this.formals.add(allocLocal(escape));
        }
    }

    @Override
    public Frame newFrame(Label name, List<Boolean> formals) {
        return new MipsFrame(name, formals);
    }

    @Override
    public Access allocLocal(boolean escape) {
        if (escape) {
            localOffset -= wordSize();
            return new InFrame(localOffset);
        } else {
            return new InReg(new Temp());
        }
    }

    @Override
    public Temp RV() {
        return RV; // Retorna o registrador fixo, não um novo
    }

    @Override
    public Temp FP() {
        return FP; // Retorna o registrador fixo
    }

    @Override
    public int wordSize() {
        return 4;
    }

    @Override
    public Exp externalCall(String func, List<Exp> args) {
        ExpList irArgs = null;
        for (int i = args.size() - 1; i >= 0; i--) {
            irArgs = new ExpList(args.get(i), irArgs);
        }
        return new CALL(new NAME(new Label(func)), irArgs);
    }

    @Override
    public Stm procEntryExit1(Stm body) {
        return body;
    }
}