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
        return new Temp();
    }

    @Override
    public int wordSize() {
        return 4;
    }

    @Override
    public Exp externalCall(String func, List<Exp> args) {
        return new CALL(new NAME(new Label(func)), (ExpList) args);
    }

    @Override
    public Stm procEntryExit1(Stm body) {
        return body;
    }
}