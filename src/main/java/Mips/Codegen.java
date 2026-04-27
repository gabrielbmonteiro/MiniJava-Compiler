package Mips;

public class Codegen {
    Mips.MipsFrame frame;
    private Assem.InstrList ilist = null, last = null;

    public Codegen(Mips.MipsFrame f) {
        frame = f;
    }

    private void emit(Assem.Instr inst) {
        if (last != null)
            last = last.tail = new Assem.InstrList(inst, null);
        else
            last = ilist = new Assem.InstrList(inst, null);
    }

    private Temp.TempList L(Temp.Temp h, Temp.TempList t) {
        return new Temp.TempList(h, t);
    }

    public Assem.InstrList getInstrList() {
        return ilist;
    }

    /* --- Munch Statements --- */
    public void munchStm(Tree.Stm s) {
        if (s instanceof Tree.MOVE) {
            munchMove((Tree.MOVE) s);
        } else if (s instanceof Tree.EXPR) {
            munchExp(((Tree.EXPR) s).exp);
        } else if (s instanceof Tree.JUMP) {
            Tree.JUMP j = (Tree.JUMP) s;
            emit(new Assem.OPER("j `j0", null, null, j.targets));
        } else if (s instanceof Tree.CJUMP) {
            munchCjump((Tree.CJUMP) s);
        } else if (s instanceof Tree.LABEL) {
            Tree.LABEL l = (Tree.LABEL) s;
            emit(new Assem.LABEL(l.label.toString() + ":", l.label));
        }
    }

    private void munchMove(Tree.MOVE s) {
        if (s.dst instanceof Tree.MEM) {
            Tree.MEM mem = (Tree.MEM) s.dst;
            if (mem.exp instanceof Tree.BINOP && ((Tree.BINOP) mem.exp).binop == Tree.BINOP.PLUS
                    && ((Tree.BINOP) mem.exp).right instanceof Tree.CONST) {

                int offset = ((Tree.CONST) ((Tree.BINOP) mem.exp).right).value;
                emit(new Assem.OPER("sw `s0, " + offset + "(`s1)",
                        null, L(munchExp(s.src), L(munchExp(((Tree.BINOP) mem.exp).left), null))));
            } else if (mem.exp instanceof Tree.CONST) {
                int offset = ((Tree.CONST) mem.exp).value;
                emit(new Assem.OPER("sw `s0, " + offset + "($zero)",
                        null, L(munchExp(s.src), null)));
            } else {
                emit(new Assem.OPER("sw `s0, 0(`s1)",
                        null, L(munchExp(s.src), L(munchExp(mem.exp), null))));
            }
        }
        else if (s.dst instanceof Tree.TEMP) {
            emit(new Assem.MOVE("move `d0, `s0", ((Tree.TEMP) s.dst).temp, munchExp(s.src)));
        }
    }

    private void munchCjump(Tree.CJUMP s) {
        String op = switch (s.relop) {
            case Tree.CJUMP.EQ -> "beq";
            case Tree.CJUMP.NE -> "bne";
            case Tree.CJUMP.LT -> "blt";
            case Tree.CJUMP.GT -> "bgt";
            case Tree.CJUMP.LE -> "ble";
            case Tree.CJUMP.GE -> "bge";
            default -> "beq";
        };
        // beq rs, rt, label
        emit(new Assem.OPER(op + " `s0, `s1, `j0",
                null, L(munchExp(s.left), L(munchExp(s.right), null)),
                new Temp.LabelList(s.iftrue, null)));
    }

    /* --- Munch Expressions --- */
    public Temp.Temp munchExp(Tree.Exp e) {
        if (e instanceof Tree.MEM) {
            Tree.MEM mem = (Tree.MEM) e;
            Temp.Temp r = new Temp.Temp();
            if (mem.exp instanceof Tree.BINOP && ((Tree.BINOP) mem.exp).binop == Tree.BINOP.PLUS
                    && ((Tree.BINOP) mem.exp).right instanceof Tree.CONST) {

                int offset = ((Tree.CONST) ((Tree.BINOP) mem.exp).right).value;
                emit(new Assem.OPER("lw `d0, " + offset + "(`s0)",
                        L(r, null), L(munchExp(((Tree.BINOP) mem.exp).left), null)));
            } else {
                emit(new Assem.OPER("lw `d0, 0(`s0)", L(r, null), L(munchExp(mem.exp), null)));
            }
            return r;
        }
        else if (e instanceof Tree.BINOP) {
            return munchBinop((Tree.BINOP) e);
        }
        else if (e instanceof Tree.CONST) {
            Temp.Temp r = new Temp.Temp();
            emit(new Assem.OPER("li `d0, " + ((Tree.CONST) e).value, L(r, null), null));
            return r;
        }
        else if (e instanceof Tree.TEMP) {
            return ((Tree.TEMP) e).temp;
        }
        else if (e instanceof Tree.CALL) {
            return munchCall((Tree.CALL) e);
        }
        else if (e instanceof Tree.NAME) {
            Temp.Temp r = new Temp.Temp();
            emit(new Assem.OPER("la `d0, " + ((Tree.NAME) e).label.toString(), L(r, null), null));
            return r;
        }

        return null;
    }

    private Temp.Temp munchBinop(Tree.BINOP b) {
        Temp.Temp r = new Temp.Temp();
        if (b.binop == Tree.BINOP.PLUS && b.right instanceof Tree.CONST) {
            emit(new Assem.OPER("addi `d0, `s0, " + ((Tree.CONST) b.right).value,
                    L(r, null), L(munchExp(b.left), null)));
            return r;
        }

        String op = switch (b.binop) {
            case Tree.BINOP.PLUS -> "add";
            case Tree.BINOP.MINUS -> "sub";
            case Tree.BINOP.MUL -> "mul";
            case Tree.BINOP.AND -> "and";
            case Tree.BINOP.OR -> "or";
            default -> "add";
        };
        emit(new Assem.OPER(op + " `d0, `s0, `s1",
                L(r, null), L(munchExp(b.left), L(munchExp(b.right), null))));
        return r;
    }

    private Temp.Temp munchCall(Tree.CALL c) {
        Temp.TempList argRegs = munchArgs(0, c.args);

        if (c.func instanceof Tree.NAME) {
            emit(new Assem.OPER("jal " + ((Tree.NAME) c.func).label.toString(),
                    frame.calldefs(), argRegs));
        } else {
            Temp.Temp funcReg = munchExp(c.func);
            emit(new Assem.OPER("jalr `s0",
                    frame.calldefs(), L(funcReg, argRegs)));
        }
        return frame.RV();
    }

    private Temp.TempList munchArgs(int i, Tree.ExpList args) {
        if (args == null) return null;

        Temp.Temp src = munchExp(args.head);
        Temp.Temp dest = null;

        // Mapeia os 4 primeiros argumentos para os registadores corretos do MIPS
        switch (i) {
            case 0: dest = Mips.MipsFrame.A0; break;
            case 1: dest = Mips.MipsFrame.A1; break;
            case 2: dest = Mips.MipsFrame.A2; break;
            case 3: dest = Mips.MipsFrame.A3; break;
        }

        if (dest != null) {
            emit(new Assem.MOVE("move `d0, `s0", dest, src));
            return L(dest, munchArgs(i + 1, args.tail));
        } else {
            // Se tem mais de 4 argumentos, vão para a pilha
            int offset = i * frame.wordSize();
            emit(new Assem.OPER("sw `s0, " + offset + "($sp)", null, L(src, null)));
            return munchArgs(i + 1, args.tail);
        }
    }

}