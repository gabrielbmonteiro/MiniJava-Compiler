class ErroDuplicata {
    public static void main(String[] a) {
        System.out.println(0);
    }
}

class Teste {
    int x;
    boolean x; // ERRO: Variavel 'x' duplicada na classe

    public int m1() {
        int y;
        boolean y; // ERRO: Variavel 'y' duplicada no Metodo
        return 0;
    }

    public int m1() { // ERRO: Metodo 'm1' duplicado na classe
        return 1;
    }
}