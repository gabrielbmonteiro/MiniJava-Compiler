package mips;

import frame.Access;
import temp.Temp;
import tree.Exp;
import tree.TEMP;

public class InReg extends Access {
    Temp temp;

    public InReg(Temp t) {
        this.temp = t;
    }

    @Override
    public Exp exp(Exp framePtr) {
        return new TEMP(temp);
    }
}