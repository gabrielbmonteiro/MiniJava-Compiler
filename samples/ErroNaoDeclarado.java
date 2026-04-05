class ErroNaoDeclarado {
    public static void main(String[] a) {
        System.out.println(new A().exec());
    }
}

class A {
    public int exec() {
        int x;
        x = y + 1; // ERRO: 'y' nao foi declarado
        return this.metodoInexistente(); // ERRO: Metodo nao existe
    }
}