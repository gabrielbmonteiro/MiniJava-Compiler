class TesteN3Memoria {
    public static void main(String[] args) {
        System.out.println(new Memoria().setX(1, 2));
    }
}

class Memoria {
    int x; // Atributos primeiro
    int y;

    public int setX(int a, int b) { // Métodos depois, com 'public'
        x = a;
        y = b;
        return x;
    }
}