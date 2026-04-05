package semantic;

import syntaxtree.*;
import visitor.TypeDepthFirstVisitor;
import symbol.Table;
import symbol.Symbol;

public class TypeCheckVisitor extends TypeDepthFirstVisitor {

    private final Table symbolTable;
    private String currClass = null;
    private String currMethod = null;

    public TypeCheckVisitor(Table st) {
        this.symbolTable = st;
    }

    // Procura o tipo de uma variável na tabela
    private Type getVarType(String varName) {
        Type t = null;
        if (currMethod != null) {
            // Tenta achar no escopo do método (Local ou Parâmetro)
            t = (Type) symbolTable.get(Symbol.symbol(currClass + "." + currMethod + "." + varName));
        }
        if (t == null && currClass != null) {
            // Tenta achar no escopo da classe (Atributo)
            t = (Type) symbolTable.get(Symbol.symbol(currClass + "." + varName));
        }
        return t;
    }

    // Verifica se dois tipos são compatíveis
    private boolean checkTypeMatch(Type t1, Type t2) {
        if (t1 == null || t2 == null) return false;
        if (t1 instanceof IntegerType && t2 instanceof IntegerType) return true;
        if (t1 instanceof BooleanType && t2 instanceof BooleanType) return true;
        if (t1 instanceof IntArrayType && t2 instanceof IntArrayType) return true;
        if (t1 instanceof IdentifierType && t2 instanceof IdentifierType) {
            return ((IdentifierType) t1).s.equals(((IdentifierType) t2).s);
        }
        return false;
    }

    // CONTROLO DE CONTEXTO
    @Override
    public Type visit(MainClass n) {
        currClass = n.i1.s;
        currMethod = "main";
        super.visit(n);
        currClass = null;
        currMethod = null;
        return null;
    }

    @Override
    public Type visit(ClassDeclSimple n) {
        currClass = n.i.s;
        super.visit(n);
        currClass = null;
        return null;
    }

    @Override
    public Type visit(ClassDeclExtends n) {
        currClass = n.i.s;
        super.visit(n);
        currClass = null;
        return null;
    }

    @Override
    public Type visit(MethodDecl n) {
        currMethod = n.i.s;
        super.visit(n);

        // Verifica se o tipo da expressão do 'return' bate com o tipo de retorno declarado do método
        Type returnExpType = n.e.accept(this);
        if (!checkTypeMatch(n.t, returnExpType)) {
            System.err.println("Erro Semântico [" + currClass + "." + currMethod + "]: O tipo de retorno não corresponde ao declarado.");
        }

        currMethod = null;
        return null;
    }

    // VALIDAÇÃO DE STATEMENTS
    @Override
    public Type visit(If n) {
        Type cond = n.e.accept(this);
        if (!(cond instanceof BooleanType)) {
            System.err.println("Erro Semântico: A condição do 'if' deve ser booleana.");
        }
        n.s1.accept(this);
        n.s2.accept(this);
        return null;
    }

    @Override
    public Type visit(While n) {
        Type cond = n.e.accept(this);
        if (!(cond instanceof BooleanType)) {
            System.err.println("Erro Semântico: A condição do 'while' deve ser booleana.");
        }
        n.s.accept(this);
        return null;
    }

    @Override
    public Type visit(Print n) {
        Type exp = n.e.accept(this);
        if (!(exp instanceof IntegerType)) {
            System.err.println("Erro Semântico: System.out.println requer uma expressão inteira.");
        }
        return null;
    }

    @Override
    public Type visit(Assign n) {
        Type varType = getVarType(n.i.s);
        if (varType == null) {
            System.err.println("Erro Semântico: A variável '" + n.i.s + "' não foi declarada.");
            return null;
        }
        Type expType = n.e.accept(this);
        if (!checkTypeMatch(varType, expType)) {
            System.err.println("Erro Semântico: Tipo incompatível na atribuição para '" + n.i.s + "'.");
        }
        return null;
    }

    @Override
    public Type visit(ArrayAssign n) {
        Type varType = getVarType(n.i.s);
        if (!(varType instanceof IntArrayType)) {
            System.err.println("Erro Semântico: A variável '" + n.i.s + "' não é um array de inteiros.");
        }
        Type indexType = n.e1.accept(this);
        Type valueType = n.e2.accept(this);

        if (!(indexType instanceof IntegerType)) {
            System.err.println("Erro Semântico: O índice do array deve ser um inteiro.");
        }
        if (!(valueType instanceof IntegerType)) {
            System.err.println("Erro Semântico: O valor atribuído ao array deve ser um inteiro.");
        }
        return null;
    }

    // VALIDAÇÃO DE EXPRESSÕES
    @Override
    public Type visit(Plus n) {
        Type t1 = n.e1.accept(this);
        Type t2 = n.e2.accept(this);
        if (!(t1 instanceof IntegerType) || !(t2 instanceof IntegerType)) {
            System.err.println("Erro Semântico: Os operandos do '+' devem ser inteiros.");
        }
        return new IntegerType();
    }

    @Override
    public Type visit(Minus n) {
        Type t1 = n.e1.accept(this);
        Type t2 = n.e2.accept(this);
        if (!(t1 instanceof IntegerType) || !(t2 instanceof IntegerType)) {
            System.err.println("Erro Semântico: Os operandos do '-' devem ser inteiros.");
        }
        return new IntegerType();
    }

    @Override
    public Type visit(Times n) {
        Type t1 = n.e1.accept(this);
        Type t2 = n.e2.accept(this);
        if (!(t1 instanceof IntegerType) || !(t2 instanceof IntegerType)) {
            System.err.println("Erro Semântico: Os operandos do '*' devem ser inteiros.");
        }
        return new IntegerType();
    }

    @Override
    public Type visit(LessThan n) {
        Type t1 = n.e1.accept(this);
        Type t2 = n.e2.accept(this);
        if (!(t1 instanceof IntegerType) || !(t2 instanceof IntegerType)) {
            System.err.println("Erro Semântico: Os operandos do '<' devem ser inteiros.");
        }
        return new BooleanType();
    }

    @Override
    public Type visit(And n) {
        Type t1 = n.e1.accept(this);
        Type t2 = n.e2.accept(this);
        if (!(t1 instanceof BooleanType) || !(t2 instanceof BooleanType)) {
            System.err.println("Erro Semântico: Os operandos do '&&' devem ser booleanos.");
        }
        return new BooleanType();
    }

    @Override
    public Type visit(Not n) {
        Type t = n.e.accept(this);
        if (!(t instanceof BooleanType)) {
            System.err.println("Erro Semântico: O operando do '!' deve ser booleano.");
        }
        return new BooleanType();
    }

    @Override
    public Type visit(ArrayLookup n) {
        Type arrayType = n.e1.accept(this);
        Type indexType = n.e2.accept(this);
        if (!(arrayType instanceof IntArrayType)) {
            System.err.println("Erro Semântico: Tentativa de indexar algo que não é um array de inteiros.");
        }
        if (!(indexType instanceof IntegerType)) {
            System.err.println("Erro Semântico: O índice do array deve ser um inteiro.");
        }
        return new IntegerType();
    }

    @Override
    public Type visit(ArrayLength n) {
        Type arrayType = n.e.accept(this);
        if (!(arrayType instanceof IntArrayType)) {
            System.err.println("Erro Semântico: .length só pode ser usado em arrays de inteiros.");
        }
        return new IntegerType();
    }

    @Override
    public Type visit(Call n) {
        Type callerType = n.e.accept(this);

        if (!(callerType instanceof IdentifierType)) {
            System.err.println("Erro Semântico: Chamada de método num tipo inválido.");
            return new IntegerType();
        }

        String className = ((IdentifierType) callerType).s;
        String methodName = n.i.s;

        // Pede o tipo de retorno deste método à Tabela de Símbolos
        Type returnType = (Type) symbolTable.get(Symbol.symbol(className + "." + methodName + ".returnType"));

        if (returnType == null) {
            System.err.println("Erro Semântico: O método '" + methodName + "' não existe na classe '" + className + "'.");
            return new IntegerType();
        }

        for (int i = 0; i < n.el.size(); i++) {
            n.el.elementAt(i).accept(this);
        }

        return returnType;
    }

    // TERMINAIS
    @Override
    public Type visit(IntegerLiteral n) {
        return new IntegerType();
    }

    @Override
    public Type visit(True n) {
        return new BooleanType();
    }

    @Override
    public Type visit(False n) {
        return new BooleanType();
    }

    @Override
    public Type visit(IdentifierExp n) {
        Type t = getVarType(n.s);
        if (t == null) {
            System.err.println("Erro Semântico: Variável '" + n.s + "' não declarada.");
            return new IntegerType();
        }
        return t;
    }

    @Override
    public Type visit(This n) {
        if (currClass == null) {
            System.err.println("Erro Semântico: 'this' usado fora do contexto de uma classe.");
            return new IntegerType();
        }
        return new IdentifierType(currClass);
    }

    @Override
    public Type visit(NewArray n) {
        Type sizeType = n.e.accept(this);
        if (!(sizeType instanceof IntegerType)) {
            System.err.println("Erro Semântico: O tamanho do array deve ser um número inteiro.");
        }
        return new IntArrayType();
    }

    @Override
    public Type visit(NewObject n) {
        // Assume-se que a classe existe (uma verificação mais robusta procuraria a classe na tabela)
        return new IdentifierType(n.i.s);
    }
}