package Mips;

import Frame.Access;
import Temp.Temp;
import Tree.Exp;
import Tree.TEMP;

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