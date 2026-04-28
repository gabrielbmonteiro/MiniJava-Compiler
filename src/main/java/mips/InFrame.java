package mips;

import frame.Access;
import tree.BINOP;
import tree.CONST;
import tree.Exp;
import tree.MEM;

public class InFrame extends Access {
    int offset;

    public InFrame(int offset) {
        this.offset = offset;
    }

    @Override
    public Exp exp(Exp framePtr) {
        return new MEM(
                new BINOP(BINOP.PLUS, framePtr, new CONST(offset))
        );
    }

    public int getOffset() {
        return offset;
    }

}