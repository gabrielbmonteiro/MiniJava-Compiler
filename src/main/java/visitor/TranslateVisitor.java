package visitor;

import Translate.Exp;
import Translate.Ex;
import Translate.Nx;
import Translate.Cx;
import Translate.Frag;
import Translate.ProcFrag;
import Frame.Frame;
import Frame.Access;
import Temp.Label;
import Temp.Temp;
import Tree.BINOP;
import Tree.CJUMP;
import Tree.CONST;
import Tree.MOVE;
import Tree.SEQ;
import Tree.TEMP;
import syntaxtree.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class TranslateVisitor implements Visitor {

    private Frame frameFactory;
    private Frame currentFrame;
    private Frag frags = null;

    private Map<String, Access> varEnv;

    private Exp expResult;

    public TranslateVisitor(Frame frameFactory) {
        this.frameFactory = frameFactory;
        this.varEnv = new HashMap<String, Access>();
    }

    public Frag getResult() {
        return frags;
    }

    private void addFrag(Frag f) {
        f.next = frags;
        frags = f;
    }

    // --------------------------------------------------------
    // 1. Visitando Declarações
    // --------------------------------------------------------
    public void visit(MethodDecl n) {
        varEnv.clear();

        Label methodLabel = new Label(n.i.s);
        List<Boolean> formals = new ArrayList<Boolean>();
        formals.add(false); // 'this'

        for (int i = 0; i < n.fl.size(); i++) {
            formals.add(false);
        }

        Frame previousFrame = currentFrame;
        currentFrame = frameFactory.newFrame(methodLabel, formals);

        for (int i = 0; i < n.fl.size(); i++) {
            String paramName = n.fl.elementAt(i).i.s;
            Access paramAccess = currentFrame.formals.get(i + 1);
            varEnv.put(paramName, paramAccess);
        }

        for (int i = 0; i < n.vl.size(); i++) {
            n.vl.elementAt(i).accept(this);
        }

        Tree.Stm bodyStm = null;
        for (int i = 0; i < n.sl.size(); i++) {
            n.sl.elementAt(i).accept(this);
            Tree.Stm s = this.expResult.unNx(); // Recupera o resultado salvo

            if (s != null) {
                if (bodyStm == null) bodyStm = s;
                else bodyStm = new SEQ(bodyStm, s);
            }
        }

        n.e.accept(this);
        Tree.Exp returnExp = this.expResult.unEx(); // Recupera o resultado salvo

        Tree.Stm returnStm = new MOVE(new TEMP(currentFrame.RV()), returnExp);
        if (bodyStm == null) bodyStm = returnStm;
        else bodyStm = new SEQ(bodyStm, returnStm);

        bodyStm = currentFrame.procEntryExit1(bodyStm);

        addFrag(new ProcFrag(bodyStm, currentFrame));
        currentFrame = previousFrame;

        this.expResult = new Nx(bodyStm); // Salva o resultado final do método
    }

    public void visit(VarDecl n) {
        Access a = currentFrame.allocLocal(false);
        varEnv.put(n.i.s, a);
        this.expResult = new Nx(null);
    }

    // --------------------------------------------------------
    // 2. Acesso a Variáveis
    // --------------------------------------------------------

    public void visit(IdentifierExp n) {
        Access a = varEnv.get(n.s);

        if (a != null) {
            Temp fp = new Temp();
            this.expResult = new Ex(a.exp(new TEMP(fp)));
        } else {
            this.expResult = new Ex(new CONST(0)); // Placeholder para atributos de classe
        }
    }

    // --------------------------------------------------------
    // 3. Comandos e Expressões Base
    // --------------------------------------------------------

    public void visit(Assign n) {
        n.i.accept(this);
        Tree.Exp left = this.expResult.unEx();

        n.e.accept(this);
        Tree.Exp right = this.expResult.unEx();

        this.expResult = new Nx(new MOVE(left, right));
    }

    public void visit(Plus n) {
        n.e1.accept(this);
        Tree.Exp left = this.expResult.unEx();

        n.e2.accept(this);
        Tree.Exp right = this.expResult.unEx();

        this.expResult = new Ex(new BINOP(BINOP.PLUS, left, right));
    }

    public void visit(IntegerLiteral n) {
        this.expResult = new Ex(new CONST(n.i));
    }

    public void visit(LessThan n) {
        n.e1.accept(this);
        Tree.Exp left = this.expResult.unEx();

        n.e2.accept(this);
        Tree.Exp right = this.expResult.unEx();

        this.expResult = new Cx() {
            @Override
            public Tree.Stm unCx(Label t, Label f) {
                return new CJUMP(CJUMP.LT, left, right, t, f);
            }
        };
    }

    // --------------------------------------------------------
    // 4. Métodos não implementados da interface Visitor
    // --------------------------------------------------------

    public void visit(Program n) {
        n.m.accept(this);
        for (int i = 0; i < n.cl.size(); i++) {
            n.cl.elementAt(i).accept(this);
        }
        this.expResult = new Nx(null);
    }

    public void visit(MainClass n) { /* Implementação futura */ }
    public void visit(ClassDeclSimple n) { /* Implementação futura */ }
    public void visit(ClassDeclExtends n) { /* Implementação futura */ }
    public void visit(Formal n) { /* Implementação futura */ }
    public void visit(IntArrayType n) { /* Tipos não geram código IR */ }
    public void visit(BooleanType n) { /* Tipos não geram código IR */ }
    public void visit(IntegerType n) { /* Tipos não geram código IR */ }
    public void visit(IdentifierType n) { /* Tipos não geram código IR */ }
    public void visit(Block n) { /* Implementação futura */ }

    public void visit(If n) {
        Label t = new Label();
        Label f = new Label();
        Label join = new Label();

        n.e.accept(this);
        Translate.Exp cond = this.expResult;

        n.s1.accept(this);
        Tree.Stm stmTrue = this.expResult.unNx();

        n.s2.accept(this);
        Tree.Stm stmFalse = this.expResult.unNx();

        Tree.Stm ifStm = new Tree.SEQ(cond.unCx(t, f),
                new Tree.SEQ(new Tree.LABEL(t),
                        new Tree.SEQ(stmTrue,
                                new Tree.SEQ(new Tree.JUMP(join),
                                        new Tree.SEQ(new Tree.LABEL(f),
                                                new Tree.SEQ(stmFalse, new Tree.LABEL(join)))))));

        this.expResult = new Nx(ifStm);
    }

    public void visit(While n) {
        Label test = new Label();
        Label body = new Label();
        Label done = new Label();

        n.e.accept(this);
        Translate.Exp cond = this.expResult;

        n.s.accept(this);
        Tree.Stm stmBody = this.expResult.unNx();

        Tree.Stm whileStm = new Tree.SEQ(new Tree.LABEL(test),
                new Tree.SEQ(cond.unCx(body, done),
                        new Tree.SEQ(new Tree.LABEL(body),
                                new Tree.SEQ(stmBody,
                                        new Tree.SEQ(new Tree.JUMP(test), new Tree.LABEL(done))))));

        this.expResult = new Nx(whileStm);
    }

    public void visit(Print n) {}
    public void visit(ArrayAssign n) {}
    public void visit(And n) {}
    public void visit(Minus n) {}
    public void visit(Times n) {}
    public void visit(ArrayLookup n) {}
    public void visit(ArrayLength n) {}
    public void visit(Call n) {}
    public void visit(True n) {}
    public void visit(False n) {}
    public void visit(This n) {}
    public void visit(NewArray n) {}
    public void visit(NewObject n) {}
    public void visit(Not n) {}
    public void visit(Identifier n) {}
}