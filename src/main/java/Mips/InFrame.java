package Mips;

import Frame.Access;
import Tree.BINOP;
import Tree.CONST;
import Tree.Exp;
import Tree.MEM;

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
}