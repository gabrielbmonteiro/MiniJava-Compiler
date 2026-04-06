class Parametros {
    public static void main(String[] a) {
        System.out.println(new Calculadora().somar(10, true));
    }
}

class Calculadora {
    // ERRO: O parâmetro 'x' está a ser declarado duas vezes na assinatura!
    public int somar(int x, boolean x) {
        return 1;
    }
}