class TesteN3Memoria {
    public static void main(String[] args) {
        System.out.println(new Memoria().setX(10, 20));
    }
}

class Memoria {
    int x; // Atributo de classe (requer cálculo de offset com this)

    public int setX(int a, int b) {
        int c; // Variável local
        x = a;
        c = b;
        return x + c;
    }
}