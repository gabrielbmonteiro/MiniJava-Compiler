package semantic;

import syntaxtree.*;
import visitor.DepthFirstVisitor;
import symbol.Table;
import symbol.Symbol;

public class BuildSymbolTableVisitor extends DepthFirstVisitor {
    private final Table symbolTable;
    private String currClass = null;
    private String currMethod = null;
    private int quantidadeErros = 0;

    public BuildSymbolTableVisitor() {
        this.symbolTable = new Table();
    }

    public Table getTable() { return this.symbolTable; }
    public int getQuantidadeErros() { return quantidadeErros; }

    // --- CLASSES ---
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

    @Override
    public void visit(ClassDeclSimple n) {
        currClass = n.i.s;
        if (symbolTable.get(Symbol.symbol(currClass)) != null) {
            System.err.println("Erro Semantico: Classe '" + currClass + "' duplicada.");
            quantidadeErros++;
        }
        symbolTable.put(Symbol.symbol(currClass), n);
        super.visit(n);
        currClass = null;
    }

    @Override
    public void visit(ClassDeclExtends n) {
        currClass = n.i.s;
        if (symbolTable.get(Symbol.symbol(currClass)) != null) {
            System.err.println("Erro Semantico: Classe '" + currClass + "' duplicada.");
            quantidadeErros++;
        }
        symbolTable.put(Symbol.symbol(currClass), n);
        symbolTable.put(Symbol.symbol(currClass + ".extends"), n.j.s);
        super.visit(n);
        currClass = null;
    }

    // --- Metodos---
    @Override
    public void visit(MethodDecl n) {
        String methodName = n.i.s;
        String key = currClass + "." + methodName;

        if (symbolTable.get(Symbol.symbol(key)) != null) {
            System.err.println("Erro Semantico: Metodo '" + methodName + "' duplicado na classe " + currClass);
            quantidadeErros++;
        }

        // Salva o metodo e seu tipo de retorno
        symbolTable.put(Symbol.symbol(key), n);
        symbolTable.put(Symbol.symbol(key + ".returnType"), n.t);

        // Salva os argumentos
        int numArgs = (n.fl != null) ? n.fl.size() : 0;
        symbolTable.put(Symbol.symbol(key + ".numArgs"), numArgs);

        for (int i = 0; i < numArgs; i++) {
            Formal arg = n.fl.elementAt(i);
            symbolTable.put(Symbol.symbol(key + ".arg." + i), arg.t);
        }

        currMethod = methodName;
        super.visit(n);
        currMethod = null;
    }

    // --- VARIÁVEIS E PARÂMETROS ---
    @Override
    public void visit(VarDecl n) {
        String varName = n.i.s;
        String key = (currMethod != null) ? currClass + "." + currMethod + "." + varName : currClass + "." + varName;

        if (symbolTable.get(Symbol.symbol(key)) != null) {
            System.err.println("Erro Semantico: Variavel '" + varName + "' ja declarada neste escopo.");
            quantidadeErros++;
        }
        symbolTable.put(Symbol.symbol(key), n.t);
    }

    @Override
    public void visit(Formal n) {
        // Parâmetros são tratados como variáveis locais do metodo
        String key = currClass + "." + currMethod + "." + n.i.s;
        symbolTable.put(Symbol.symbol(key), n.t);
    }

}