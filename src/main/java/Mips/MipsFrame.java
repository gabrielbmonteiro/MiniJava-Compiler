package Mips;

import java.util.ArrayList;
import java.util.List;
import Frame.Access;
import Frame.Frame;
import Temp.Label;
import Temp.Temp;
import Temp.TempList;
import Temp.TempMap;
import Tree.*;

public class MipsFrame extends Frame implements TempMap {

    private int localOffset = 0;

    // Registradores Fixos Especiais
    private static final Temp FP = new Temp(); // $fp
    private static final Temp RV = new Temp(); // $v0

    // Registradores Caller-Saves do MIPS
    public static final Temp A0 = new Temp();
    public static final Temp A1 = new Temp();
    public static final Temp A2 = new Temp();
    public static final Temp A3 = new Temp();
    private static final Temp V1 = new Temp(); // $v1
    private static final Temp T0 = new Temp(); // $t0
    private static final Temp T1 = new Temp(); // $t1
    private static final Temp T2 = new Temp(); // $t2
    private static final Temp T3 = new Temp(); // $t3
    private static final Temp T4 = new Temp(); // $t4
    private static final Temp T5 = new Temp(); // $t5
    private static final Temp T6 = new Temp(); // $t6
    private static final Temp T7 = new Temp(); // $t7
    private static final Temp T8 = new Temp(); // $t8
    private static final Temp T9 = new Temp(); // $t9
    private static final Temp RA = new Temp(); // $ra

    private static TempList returnSink = null;
    private static TempList CALLDEFS = null;

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
        return RV;
    }

    @Override
    public Temp FP() {
        return FP;
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

    @Override
    public TempList calldefs() {
        if (CALLDEFS == null) {
            CALLDEFS = new TempList(RV,
                    new TempList(V1,
                            new TempList(A0,
                                    new TempList(A1,
                                            new TempList(A2,
                                                    new TempList(A3,
                                                            new TempList(T0,
                                                                    new TempList(T1,
                                                                            new TempList(T2,
                                                                                    new TempList(T3,
                                                                                            new TempList(T4,
                                                                                                    new TempList(T5,
                                                                                                            new TempList(T6,
                                                                                                                    new TempList(T7,
                                                                                                                            new TempList(T8,
                                                                                                                                    new TempList(T9,
                                                                                                                                            new TempList(RA, null)))))))))))))))));
        }
        return CALLDEFS;
    }

    @Override
    public Assem.InstrList procEntryExit2(Assem.InstrList body) {
        if (returnSink == null) {
            returnSink = new TempList(RV, new TempList(FP, new TempList(RA, null)));
        }

        Assem.InstrList p = body;
        if (p == null) {
            return new Assem.InstrList(new Assem.OPER("", null, returnSink), null);
        }
        while (p.tail != null) {
            p = p.tail;
        }
        p.tail = new Assem.InstrList(new Assem.OPER("", null, returnSink), null);

        return body;
    }
    @Override
    public String tempMap(Temp t) {
        if (t == FP) return "$fp";
        if (t == RV) return "$v0";
        if (t == V1) return "$v1";
        if (t == A0) return "$a0";
        if (t == A1) return "$a1";
        if (t == A2) return "$a2";
        if (t == A3) return "$a3";
        if (t == T0) return "$t0";
        if (t == T1) return "$t1";
        if (t == T2) return "$t2";
        if (t == T3) return "$t3";
        if (t == T4) return "$t4";
        if (t == T5) return "$t5";
        if (t == T6) return "$t6";
        if (t == T7) return "$t7";
        if (t == T8) return "$t8";
        if (t == T9) return "$t9";
        if (t == RA) return "$ra";
        return null;
    }

}