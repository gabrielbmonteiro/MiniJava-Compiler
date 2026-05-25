package Translate;

import Frame.Frame;

public class ProcFrag extends Frag {
    public Tree.Stm body;
    public Frame frame;

    public ProcFrag(Tree.Stm body, Frame frame) {
        this.body = body;
        this.frame = frame;
    }
}