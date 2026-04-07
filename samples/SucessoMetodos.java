class Metodos {
    public static void main(String[] a) {
        System.out.println(new Auxiliar().primeiro().segundo(true));
    }
}

class Auxiliar {
    public Auxiliar primeiro() {
        return this;
    }
    public int segundo(boolean b) {
        int x;
        if (b) x = 1; else x = 0;
        return x;
    }
}