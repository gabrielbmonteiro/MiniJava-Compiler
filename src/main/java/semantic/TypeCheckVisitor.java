package semantic;

import syntaxtree.*;
import visitor.TypeDepthFirstVisitor;
import symbol.Table;
import symbol.Symbol;

public class TypeCheckVisitor extends TypeDepthFirstVisitor {

    private Table symbolTable;
    private String currClass = null;
    private String currMethod = null;
    private int quantidadeErros = 0;

    public TypeCheckVisitor(Table st) {
        this.symbolTable = st;
    }

    public int getQuantidadeErros() {
        return quantidadeErros;
    }

    private void reportError(String message) {
        System.err.println("Erro Semantico: " + message);
        quantidadeErros++;
    }

    // Auxiliares de checagem p/ evitar problemas com múltiplas instâncias de classes de tipo
    private boolean isInt(Type t) { return t != null && t.getClass().getSimpleName().equals("IntegerType"); }
    private boolean isBool(Type t) { return t != null && t.getClass().getSimpleName().equals("BooleanType"); }
    private boolean isIntArray(Type t) { return t != null && t.getClass().getSimpleName().equals("IntArrayType"); }

    private Type getVarType(String id) {
        Type t = null;
        if (currMethod != null) {
            t = (Type) symbolTable.get(Symbol.symbol(currClass + "." + currMethod + "." + id));
        }
        if (t == null) {
            t = (Type) symbolTable.get(Symbol.symbol(currClass + "." + id));
        }
        return t;
    }

    private boolean isCompatible(Type t1, Type t2) {
        if (t1 == null || t2 == null) return false;
        String n1 = t1.getClass().getSimpleName();
        String n2 = t2.getClass().getSimpleName();
        if (n1.equals(n2)) {
            if (t1 instanceof IdentifierType && t2 instanceof IdentifierType) {
                return ((IdentifierType) t1).s.equals(((IdentifierType) t2).s);
            }
            return true;
        }
        return false;
    }

    // ESTRUTURA E ESCOPO
    @Override
    public Type visit(MainClass n) {
        currClass = n.i1.s;
        currMethod = "main";
        n.s.accept(this);
        currClass = null;
        currMethod = null;
        return null;
    }

    @Override
    public Type visit(ClassDeclSimple n) {
        currClass = n.i.s;
        for (int i = 0; i < n.ml.size(); i++) n.ml.elementAt(i).accept(this);
        currClass = null;
        return null;
    }

    @Override
    public Type visit(ClassDeclExtends n) {
        currClass = n.i.s;
        for (int i = 0; i < n.ml.size(); i++) n.ml.elementAt(i).accept(this);
        currClass = null;
        return null;
    }

    @Override
    public Type visit(MethodDecl n) {
        currMethod = n.i.s;
        for (int i = 0; i < n.sl.size(); i++) n.sl.elementAt(i).accept(this);
        Type returnExpType = n.e.accept(this);
        if (!isCompatible(n.t, returnExpType)) {
            reportError("Tipo de retorno incompativel no metodo '" + n.i.s + "'.");
        }
        currMethod = null;
        return null;
    }

    // STATEMENTS
    @Override
    public Type visit(Assign n) {
        Type tVar = getVarType(n.i.s);
        Type tExp = n.e.accept(this);
        if (tVar == null) reportError("Variavel '" + n.i.s + "' nao declarada.");
        else if (!isCompatible(tVar, tExp)) reportError("Tipo incompativel na atribuição para '" + n.i.s + "'.");
        return null;
    }

    @Override
    public Type visit(ArrayAssign n) {
        Type tVar = getVarType(n.i.s);
        if (!isIntArray(tVar)) reportError("Variavel '" + n.i.s + "' nao eh um array.");
        if (!isInt(n.e1.accept(this))) reportError("Indice do array deve ser inteiro.");
        if (!isInt(n.e2.accept(this))) reportError("Valor atribuido ao array deve ser inteiro.");
        return null;
    }

    @Override
    public Type visit(If n) {
        if (!isBool(n.e.accept(this))) reportError("Condicao do 'if' deve ser booleana.");
        n.s1.accept(this);
        n.s2.accept(this);
        return null;
    }

    @Override
    public Type visit(While n) {
        if (!isBool(n.e.accept(this))) reportError("Condicao do 'while' deve ser booleana.");
        n.s.accept(this);
        return null;
    }

    @Override
    public Type visit(Print n) {
        if (!isInt(n.e.accept(this))) reportError("System.out.println exige um inteiro.");
        return null;
    }

    // EXPRESSÕES
    @Override
    public Type visit(Plus n) {
        if (!isInt(n.e1.accept(this)) || !isInt(n.e2.accept(this))) reportError("Operandos de '+' devem ser inteiros.");
        return new IntegerType();
    }

    @Override
    public Type visit(Minus n) {
        if (!isInt(n.e1.accept(this)) || !isInt(n.e2.accept(this))) reportError("Operandos de '-' devem ser inteiros.");
        return new IntegerType();
    }

    @Override
    public Type visit(Times n) {
        if (!isInt(n.e1.accept(this)) || !isInt(n.e2.accept(this))) reportError("Operandos de '*' devem ser inteiros.");
        return new IntegerType();
    }

    @Override
    public Type visit(LessThan n) {
        if (!isInt(n.e1.accept(this)) || !isInt(n.e2.accept(this))) reportError("Operandos de '<' devem ser inteiros.");
        return new BooleanType();
    }

    @Override
    public Type visit(And n) {
        if (!isBool(n.e1.accept(this)) || !isBool(n.e2.accept(this))) reportError("Operandos de '&&' devem ser booleanos.");
        return new BooleanType();
    }

    @Override
    public Type visit(Not n) {
        if (!isBool(n.e.accept(this))) reportError("Operando de '!' deve ser booleano.");
        return new BooleanType();
    }

    @Override
    public Type visit(ArrayLookup n) {
        if (!isIntArray(n.e1.accept(this))) reportError("Acesso de indice exige um array.");
        if (!isInt(n.e2.accept(this))) reportError("Indice do array deve ser inteiro.");
        return new IntegerType();
    }

    @Override
    public Type visit(ArrayLength n) {
        if (!isIntArray(n.e.accept(this))) reportError("'.length' exige um array.");
        return new IntegerType();
    }

    @Override
    public Type visit(Call n) {
        Type classType = n.e.accept(this);
        if (!(classType instanceof IdentifierType)) {
            reportError("Chamada de metodo em algo que nao eh um objeto.");
            return new IntegerType();
        }
        String cName = ((IdentifierType) classType).s;
        String mName = n.i.s;
        String key = cName + "." + mName;

        Type retType = (Type) symbolTable.get(Symbol.symbol(key + ".returnType"));
        if (retType == null) {
            reportError("Metodo '" + mName + "' nao existe na classe '" + cName + "'.");
            return new IntegerType();
        }

        Integer expected = (Integer) symbolTable.get(Symbol.symbol(key + ".numArgs"));
        if (expected == null || n.el.size() != expected) {
            reportError("Numero de argumentos incorreto para o metodo '" + mName + "'.");
        } else {
            for (int i = 0; i < n.el.size(); i++) {
                Type formal = (Type) symbolTable.get(Symbol.symbol(key + ".arg." + i));
                Type actual = n.el.elementAt(i).accept(this);
                if (!isCompatible(formal, actual)) reportError("Tipo do argumento " + i + " incompativel em '" + mName + "'.");
            }
        }
        return retType;
    }

    // TERMINAIS
    @Override
    public Type visit(IntegerLiteral n) { return new IntegerType(); }

    @Override
    public Type visit(True n) { return new BooleanType(); }

    @Override
    public Type visit(False n) { return new BooleanType(); }

    @Override
    public Type visit(IdentifierExp n) {
        Type t = getVarType(n.s);
        if (t == null) {
            reportError("Variavel '" + n.s + "' nao declarada.");
            return new IntegerType();
        }
        return t;
    }

    @Override
    public Type visit(This n) { return new IdentifierType(currClass); }

    @Override
    public Type visit(NewArray n) {
        if (!isInt(n.e.accept(this))) reportError("Tamanho do array deve ser inteiro.");
        return new IntArrayType();
    }

    @Override
    public Type visit(NewObject n) { return new IdentifierType(n.i.s); }

}