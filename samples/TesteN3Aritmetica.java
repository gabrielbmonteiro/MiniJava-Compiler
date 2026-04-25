class TesteN3Aritmetica {
    public static void main(String[] args) {
        System.out.println(new Matematica().calcular(10));
    }
}

class Matematica {
    public int calcular(int a) {
        int b;
        boolean f1;
        boolean f2;

        b = (a * 2) + 5 - 1;
        f1 = true;
        f2 = false;

        if (!(a < b) && f1) {
            b = 0;
        } else {
            b = 1;
        }

        return b;
    }
}