package translate;

import frame.Frame;

public class ProcFrag extends Frag {
    public tree.Stm body;
    public Frame frame;

    public ProcFrag(tree.Stm body, Frame frame) {
        this.body = body;
        this.frame = frame;
    }
}