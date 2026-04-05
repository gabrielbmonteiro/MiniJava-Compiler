class ErroChamada {
    public static void main(String[] a) {
        System.out.println(new Calc().start());
    }
}

class Calc {
    public int start() {
        int res;
        res = this.soma(10); // ERRO: Esperava 2 argumentos, recebeu 1
        res = this.soma(10, true); // ERRO: Segundo argumento devia ser int
        return res;
    }

    public int soma(int a, int b) {
        return a + b;
    }
}