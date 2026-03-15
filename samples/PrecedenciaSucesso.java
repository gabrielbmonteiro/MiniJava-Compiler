class TestePrec {
    public static void main(String[] a) {
        System.out.println(new Prec().check());
    }
}

class Prec {
    public int check() {
        boolean b;
        int x;

        b = !!true; // Testa a negação dupla

        if (1 + 2 < 4 && 5 < 6) { // Testa se + > < > &&
            x = 1;
        } else {
            x = 0;
        }
        return x;
    }
}