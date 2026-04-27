package mips;

public class Codegen {
    mips.MipsFrame frame;
    private assem.InstrList ilist = null, last = null;

    public Codegen(mips.MipsFrame f) {
        frame = f;
    }

    private void emit(assem.Instr inst) {
        if (last != null)
            last = last.tail = new assem.InstrList(inst, null);
        else
            last = ilist = new assem.InstrList(inst, null);
    }

    private temp.TempList L(temp.Temp h, temp.TempList t) {
        return new temp.TempList(h, t);
    }

    public assem.InstrList getInstrList() {
        return ilist;
    }

    /* --- Munch Statements --- */
    public void munchStm(tree.Stm s) {
        if (s instanceof tree.MOVE) {
            munchMove((tree.MOVE) s);
        } else if (s instanceof tree.EXPR) {
            munchExp(((tree.EXPR) s).exp);
        } else if (s instanceof tree.JUMP j) {
            emit(new assem.OPER("j `j0", null, null, j.targets));
        } else if (s instanceof tree.CJUMP) {
            munchCjump((tree.CJUMP) s);
        } else if (s instanceof tree.LABEL l) {
            emit(new assem.LABEL(l.label.toString() + ":", l.label));
        }
    }

    private void munchMove(tree.MOVE s) {
        if (s.dst instanceof tree.MEM mem) {
            if (mem.exp instanceof tree.BINOP && ((tree.BINOP) mem.exp).binop == tree.BINOP.PLUS
                    && ((tree.BINOP) mem.exp).right instanceof tree.CONST) {

                int offset = ((tree.CONST) ((tree.BINOP) mem.exp).right).value;
                emit(new assem.OPER("sw `s0, " + offset + "(`s1)",
                        null, L(munchExp(s.src), L(munchExp(((tree.BINOP) mem.exp).left), null))));
            } else if (mem.exp instanceof tree.CONST) {
                int offset = ((tree.CONST) mem.exp).value;
                emit(new assem.OPER("sw `s0, " + offset + "($zero)",
                        null, L(munchExp(s.src), null)));
            } else {
                emit(new assem.OPER("sw `s0, 0(`s1)",
                        null, L(munchExp(s.src), L(munchExp(mem.exp), null))));
            }
        }
        else if (s.dst instanceof tree.TEMP) {
            emit(new assem.MOVE("move `d0, `s0", ((tree.TEMP) s.dst).temp, munchExp(s.src)));
        }
    }

    private void munchCjump(tree.CJUMP s) {
        String op = switch (s.relop) {
            case tree.CJUMP.EQ -> "beq";
            case tree.CJUMP.NE -> "bne";
            case tree.CJUMP.LT -> "blt";
            case tree.CJUMP.GT -> "bgt";
            case tree.CJUMP.LE -> "ble";
            case tree.CJUMP.GE -> "bge";
            default -> "beq";
        };
        // beq rs, rt, label
        emit(new assem.OPER(op + " `s0, `s1, `j0",
                null, L(munchExp(s.left), L(munchExp(s.right), null)),
                new temp.LabelList(s.iftrue, null)));
    }

    /* --- Munch Expressions --- */
    public temp.Temp munchExp(tree.Exp e) {
        if (e instanceof tree.MEM mem) {
            temp.Temp r = new temp.Temp();
            if (mem.exp instanceof tree.BINOP && ((tree.BINOP) mem.exp).binop == tree.BINOP.PLUS
                    && ((tree.BINOP) mem.exp).right instanceof tree.CONST) {

                int offset = ((tree.CONST) ((tree.BINOP) mem.exp).right).value;
                emit(new assem.OPER("lw `d0, " + offset + "(`s0)",
                        L(r, null), L(munchExp(((tree.BINOP) mem.exp).left), null)));
            } else {
                emit(new assem.OPER("lw `d0, 0(`s0)", L(r, null), L(munchExp(mem.exp), null)));
            }
            return r;
        }
        else if (e instanceof tree.BINOP) {
            return munchBinop((tree.BINOP) e);
        }
        else if (e instanceof tree.CONST) {
            temp.Temp r = new temp.Temp();
            emit(new assem.OPER("li `d0, " + ((tree.CONST) e).value, L(r, null), null));
            return r;
        }
        else if (e instanceof tree.TEMP) {
            return ((tree.TEMP) e).temp;
        }
        else if (e instanceof tree.CALL) {
            return munchCall((tree.CALL) e);
        }
        else if (e instanceof tree.NAME) {
            temp.Temp r = new temp.Temp();
            emit(new assem.OPER("la `d0, " + ((tree.NAME) e).label.toString(), L(r, null), null));
            return r;
        }

        return null;
    }

    private temp.Temp munchBinop(tree.BINOP b) {
        temp.Temp r = new temp.Temp();
        if (b.binop == tree.BINOP.PLUS && b.right instanceof tree.CONST) {
            emit(new assem.OPER("addi `d0, `s0, " + ((tree.CONST) b.right).value,
                    L(r, null), L(munchExp(b.left), null)));
            return r;
        }

        String op = switch (b.binop) {
            case tree.BINOP.PLUS -> "add";
            case tree.BINOP.MINUS -> "sub";
            case tree.BINOP.MUL -> "mul";
            case tree.BINOP.AND -> "and";
            case tree.BINOP.OR -> "or";
            default -> "add";
        };
        emit(new assem.OPER(op + " `d0, `s0, `s1",
                L(r, null), L(munchExp(b.left), L(munchExp(b.right), null))));
        return r;
    }

    private temp.Temp munchCall(tree.CALL c) {
        temp.TempList argRegs = munchArgs(0, c.args);

        if (c.func instanceof tree.NAME) {
            emit(new assem.OPER("jal " + ((tree.NAME) c.func).label.toString(),
                    frame.calldefs(), argRegs));
        } else {
            temp.Temp funcReg = munchExp(c.func);
            emit(new assem.OPER("jalr `s0",
                    frame.calldefs(), L(funcReg, argRegs)));
        }
        return frame.RV();
    }

    private temp.TempList munchArgs(int i, tree.ExpList args) {
        if (args == null) return null;

        temp.Temp src = munchExp(args.head);
        temp.Temp dest = null;

        // Mapeia os 4 primeiros argumentos para os registadores corretos do MIPS
        switch (i) {
            case 0: dest = mips.MipsFrame.A0; break;
            case 1: dest = mips.MipsFrame.A1; break;
            case 2: dest = mips.MipsFrame.A2; break;
            case 3: dest = mips.MipsFrame.A3; break;
        }

        if (dest != null) {
            emit(new assem.MOVE("move `d0, `s0", dest, src));
            return L(dest, munchArgs(i + 1, args.tail));
        } else {
            // Se tem mais de 4 argumentos, vão para a pilha
            int offset = i * frame.wordSize();
            emit(new assem.OPER("sw `s0, " + offset + "($sp)", null, L(src, null)));
            return munchArgs(i + 1, args.tail);
        }
    }

}