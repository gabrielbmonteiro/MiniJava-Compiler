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
import symbol.Symbol;
import syntaxtree.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class TranslateVisitor implements Visitor {

    private final Frame frameFactory;
    private Frame currentFrame;
    private Frag frags = null;

    private final symbol.Table symbolTable;
    private String currentClassName;

    private final Map<String, Access> varEnv;
    private Exp expResult;

    public TranslateVisitor(Frame frameFactory, symbol.Table symbolTable) {
        this.frameFactory = frameFactory;
        this.symbolTable = symbolTable;
        this.varEnv = new HashMap<>();
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
        List<Boolean> formals = new ArrayList<>();
        formals.add(false);

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
            Tree.Stm s = this.expResult.unNx();

            if (s != null) {
                if (bodyStm == null) bodyStm = s;
                else bodyStm = new SEQ(bodyStm, s);
            }
        }

        n.e.accept(this);
        Tree.Exp returnExp = this.expResult.unEx();

        Tree.Stm returnStm = new MOVE(new TEMP(currentFrame.RV()), returnExp);
        if (bodyStm == null) bodyStm = returnStm;
        else bodyStm = new SEQ(bodyStm, returnStm);

        bodyStm = currentFrame.procEntryExit1(bodyStm);

        addFrag(new ProcFrag(bodyStm, currentFrame));
        currentFrame = previousFrame;

        this.expResult = new Nx(bodyStm);
    }

    public void visit(VarDecl n) {
        if (currentFrame != null) {
            Access a = currentFrame.allocLocal(false);
            varEnv.put(n.i.s, a);
        }

        this.expResult = new Nx(null);
    }

    // --------------------------------------------------------
    // 2. Acesso a Variáveis
    // --------------------------------------------------------

    private int getFieldIndex(String className, String fieldName) {
        Object classObj = symbolTable.get(Symbol.symbol(className));
        int baseCount;

        if (classObj instanceof ClassDeclExtends c) {
            baseCount = getParentFieldCount(c.j.s);
            for (int i = 0; i < c.vl.size(); i++) {
                if (c.vl.elementAt(i).i.s.equals(fieldName)) return baseCount + i;
            }
        } else if (classObj instanceof ClassDeclSimple c) {
            for (int i = 0; i < c.vl.size(); i++) {
                if (c.vl.elementAt(i).i.s.equals(fieldName)) return i;
            }
        }
        return 0;
    }

    private int getParentFieldCount(String className) {
        Object classObj = symbolTable.get(Symbol.symbol(className));
        if (classObj instanceof ClassDeclSimple c) return c.vl.size();
        if (classObj instanceof ClassDeclExtends c) {
            return c.vl.size() + getParentFieldCount(c.j.s);
        }
        return 0;
    }

    public void visit(IdentifierExp n) {
        Access a = varEnv.get(n.s);

        if (a != null) {
            this.expResult = new Ex(a.exp(new Tree.TEMP(currentFrame.FP())));
        } else {
            Access thisAccess = currentFrame.formals.get(0);
            Tree.Exp thisPtr = thisAccess.exp(new TEMP(new Temp()));

            int fieldIndex = getFieldIndex(currentClassName, n.s);
            int offset = (fieldIndex + 1) * currentFrame.wordSize();

            this.expResult = new Ex(new Tree.MEM(new Tree.BINOP(Tree.BINOP.PLUS, thisPtr, new Tree.CONST(offset))));
        }
    }

    // --------------------------------------------------------
    // 3. Comandos e Expressões Base
    // --------------------------------------------------------

    public void visit(Assign n) {
        n.e.accept(this);
        Tree.Exp right = this.expResult.unEx();

        Access a = varEnv.get(n.i.s);
        Tree.Exp left;

        if (a != null) {
            left = a.exp(new Tree.TEMP(currentFrame.FP()));
        } else {
            Access thisAccess = currentFrame.formals.get(0);
            Tree.Exp thisPtr = thisAccess.exp(new TEMP(new Temp()));

            int fieldIndex = getFieldIndex(currentClassName, n.i.s);
            int offset = (fieldIndex + 1) * currentFrame.wordSize();

            left = new Tree.MEM(new Tree.BINOP(Tree.BINOP.PLUS, thisPtr, new Tree.CONST(offset)));
        }

        this.expResult = new Nx(new Tree.MOVE(left, right));
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

    public void visit(MainClass n) {
        currentClassName = n.i1.s;
        varEnv.clear();

        Label mainLabel = new Label("main");
        List<Boolean> formals = new ArrayList<>();
        formals.add(false);

        Frame previousFrame = currentFrame;
        currentFrame = frameFactory.newFrame(mainLabel, formals);

        Access argsAccess = currentFrame.formals.get(0);
        varEnv.put(n.i2.s, argsAccess);

        n.s.accept(this);
        Tree.Stm bodyStm = this.expResult.unNx();

        if (bodyStm == null) {
            bodyStm = new Tree.EXPR(new Tree.CONST(0));
        }

        bodyStm = currentFrame.procEntryExit1(bodyStm);
        addFrag(new ProcFrag(bodyStm, currentFrame));

        currentFrame = previousFrame;
        this.expResult = new Nx(bodyStm);
    }

    public void visit(ClassDeclSimple n) {
        currentClassName = n.i.s;
        for (int i = 0; i < n.vl.size(); i++) n.vl.elementAt(i).accept(this);
        for (int i = 0; i < n.ml.size(); i++) n.ml.elementAt(i).accept(this);
        this.expResult = new Nx(null);
    }

    public void visit(ClassDeclExtends n) {
        currentClassName = n.i.s;
        for (int i = 0; i < n.vl.size(); i++) n.vl.elementAt(i).accept(this);
        for (int i = 0; i < n.ml.size(); i++) n.ml.elementAt(i).accept(this);
        this.expResult = new Nx(null);
    }

    public void visit(Minus n) {
        n.e1.accept(this);
        Tree.Exp left = this.expResult.unEx();

        n.e2.accept(this);
        Tree.Exp right = this.expResult.unEx();

        this.expResult = new Ex(new Tree.BINOP(Tree.BINOP.MINUS, left, right));
    }

    public void visit(Times n) {
        n.e1.accept(this);
        Tree.Exp left = this.expResult.unEx();

        n.e2.accept(this);
        Tree.Exp right = this.expResult.unEx();

        this.expResult = new Ex(new Tree.BINOP(Tree.BINOP.MUL, left, right));
    }

    public void visit(ArrayLookup n) {
        n.e1.accept(this);
        Tree.Exp arrayBase = this.expResult.unEx();

        n.e2.accept(this);
        Tree.Exp index = this.expResult.unEx();

        Tree.Exp offset = new Tree.BINOP(Tree.BINOP.MUL,
                new Tree.BINOP(Tree.BINOP.PLUS, index, new Tree.CONST(1)),
                new Tree.CONST(currentFrame.wordSize()));

        this.expResult = new Ex(new Tree.MEM(new Tree.BINOP(Tree.BINOP.PLUS, arrayBase, offset)));
    }

    public void visit(ArrayLength n) {
        n.e.accept(this);
        Tree.Exp arrayBase = this.expResult.unEx();

        this.expResult = new Ex(new Tree.MEM(arrayBase));
    }

    public void visit(Block n) {
        Tree.Stm blockStm = null;

        for (int i = 0; i < n.sl.size(); i++) {
            n.sl.elementAt(i).accept(this);
            Tree.Stm currentStm = this.expResult.unNx();

            if (currentStm != null) {
                if (blockStm == null) {
                    blockStm = currentStm;
                } else {
                    blockStm = new Tree.SEQ(blockStm, currentStm);
                }
            }
        }

        this.expResult = new Nx(blockStm);
    }

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

    public void visit(Print n) {
        n.e.accept(this);
        Tree.Exp arg = this.expResult.unEx();

        List<Tree.Exp> args = new ArrayList<>();
        args.add(arg);

        this.expResult = new Nx(new Tree.EXPR(currentFrame.externalCall("_printint", args)));
    }

    public void visit(ArrayAssign n) {
        Access a = varEnv.get(n.i.s);
        Tree.Exp arrayBase;

        if (a != null) {
            arrayBase = a.exp(new Tree.TEMP(currentFrame.FP()));
        } else {
            Access thisAccess = currentFrame.formals.get(0);
            Tree.Exp thisPtr = thisAccess.exp(new TEMP(new Temp()));
            int fieldIndex = getFieldIndex(currentClassName, n.i.s);
            int offset = (fieldIndex + 1) * currentFrame.wordSize();
            arrayBase = new Tree.MEM(new Tree.BINOP(Tree.BINOP.PLUS, thisPtr, new Tree.CONST(offset)));
        }

        n.e1.accept(this);
        Tree.Exp index = this.expResult.unEx();

        n.e2.accept(this);
        Tree.Exp value = this.expResult.unEx();

        Tree.Exp offsetExp = new Tree.BINOP(Tree.BINOP.MUL,
                new Tree.BINOP(Tree.BINOP.PLUS, index, new Tree.CONST(1)),
                new Tree.CONST(currentFrame.wordSize()));

        Tree.Exp dest = new Tree.MEM(new Tree.BINOP(Tree.BINOP.PLUS, arrayBase, offsetExp));

        this.expResult = new Nx(new Tree.MOVE(dest, value));
    }

    public void visit(And n) {
        n.e1.accept(this);
        Translate.Exp left = this.expResult;

        n.e2.accept(this);
        Translate.Exp right = this.expResult;

        this.expResult = new Cx() {
            @Override
            public Tree.Stm unCx(Label t, Label f) {
                Label z = new Label();
                return new Tree.SEQ(left.unCx(z, f),
                        new Tree.SEQ(new Tree.LABEL(z), right.unCx(t, f)));
            }
        };
    }

    public void visit(This n) {
        Access thisAccess = currentFrame.formals.get(0);
        this.expResult = new Ex(thisAccess.exp(new Tree.TEMP(currentFrame.FP())));
    }

    public void visit(NewArray n) {
        n.e.accept(this);
        Tree.Exp size = this.expResult.unEx();

        List<Tree.Exp> args = new ArrayList<>();
        args.add(size);

        this.expResult = new Ex(currentFrame.externalCall("_initArray", args));
    }

    public void visit(NewObject n) {
        List<Tree.Exp> args = new ArrayList<>();
        int count = getParentFieldCount(n.i.s) + 1;
        args.add(new Tree.CONST(count * currentFrame.wordSize()));

        this.expResult = new Ex(currentFrame.externalCall("_allocRecord", args));
    }

    public void visit(Call n) {
        n.e.accept(this);
        Tree.Exp objInstance = this.expResult.unEx();

        List<Tree.Exp> javaArgs = new ArrayList<>();
        javaArgs.add(objInstance);

        for (int i = 0; i < n.el.size(); i++) {
            n.el.elementAt(i).accept(this);
            javaArgs.add(this.expResult.unEx());
        }

        Tree.ExpList irArgs = null;
        for (int i = javaArgs.size() - 1; i >= 0; i--) {
            irArgs = new Tree.ExpList(javaArgs.get(i), irArgs);
        }

        Label funcLabel = new Label(n.i.s);
        this.expResult = new Ex(new Tree.CALL(new Tree.NAME(funcLabel), irArgs));
    }

    public void visit(True n) {
        this.expResult = new Ex(new Tree.CONST(1));
    }

    public void visit(False n) {
        this.expResult = new Ex(new Tree.CONST(0));
    }

    public void visit(Not n) {
        n.e.accept(this);
        Tree.Exp exp = this.expResult.unEx();
        this.expResult = new Ex(new Tree.BINOP(Tree.BINOP.MINUS, new Tree.CONST(1), exp));
    }

    public void visit(Formal n) {
        this.expResult = new Nx(null);
    }

    public void visit(IntArrayType n) {
        this.expResult = new Nx(null);
    }

    public void visit(BooleanType n) {
        this.expResult = new Nx(null);
    }

    public void visit(IntegerType n) {
        this.expResult = new Nx(null);
    }

    public void visit(IdentifierType n) {
        this.expResult = new Nx(null);
    }

    public void visit(Identifier n) {
        this.expResult = new Nx(null);
    }

}