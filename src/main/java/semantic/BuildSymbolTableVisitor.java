package semantic;

import syntaxtree.*;
import visitor.DepthFirstVisitor;
import symbol.Table;
import symbol.Symbol;

public class BuildSymbolTableVisitor extends DepthFirstVisitor {

    private final Table symbolTable;
    private String currClass = null;
    private String currMethod = null;

    public BuildSymbolTableVisitor() {
        this.symbolTable = new Table();
    }

    public Table getTable() {
        return this.symbolTable;
    }

    // 1. Visita a classe principal (Main)
    @Override
    public void visit(MainClass n) {
        currClass = n.i1.s;
        currMethod = "main";

        symbolTable.put(Symbol.symbol(currClass), n);
        symbolTable.put(Symbol.symbol(currClass + "." + currMethod + "." + n.i2.s), new IdentifierType("String[]"));

        super.visit(n);

        currClass = null;
        currMethod = null;
    }

    // 2. Visita uma Classe sem herança
    @Override
    public void visit(ClassDeclSimple n) {
        currClass = n.i.s;

        symbolTable.put(Symbol.symbol(currClass), n);

        super.visit(n);

        currClass = null;
    }

    // 3. Visita uma Classe com Herança
    @Override
    public void visit(ClassDeclExtends n) {
        currClass = n.i.s;

        symbolTable.put(Symbol.symbol(currClass), n);
        symbolTable.put(Symbol.symbol(currClass + ".extends"), n.j.s);

        super.visit(n);

        currClass = null;
    }

    // 4. Visita as declarações de Métodos
    @Override
    public void visit(MethodDecl n) {
        currMethod = n.i.s;

        symbolTable.put(Symbol.symbol(currClass + "." + currMethod), n);
        symbolTable.put(Symbol.symbol(currClass + "." + currMethod + ".returnType"), n.t);

        super.visit(n);

        currMethod = null;
    }

    // 5. Visita a Declaração de Variáveis
    @Override
    public void visit(VarDecl n) {
        if (currMethod != null) {
            symbolTable.put(Symbol.symbol(currClass + "." + currMethod + "." + n.i.s), n.t);
        } else {
            symbolTable.put(Symbol.symbol(currClass + "." + n.i.s), n.t);
        }

        super.visit(n);
    }

    // 6. Visita os Parâmetros dos Métodos
    @Override
    public void visit(Formal n) {
        symbolTable.put(Symbol.symbol(currClass + "." + currMethod + "." + n.i.s), n.t);

        super.visit(n);
    }
}