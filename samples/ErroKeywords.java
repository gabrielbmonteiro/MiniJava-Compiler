class TesteErro {
    public static void main(String[] a) {
        System.out.println(1);
    }
}

class Tentativa {
    public int bad() {
        int class; // ERRO: 'class' é palavra reservada
        class = 10;
        return class;
    }
}