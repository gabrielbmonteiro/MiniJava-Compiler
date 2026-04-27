class TesteArray {
    public static void main(String[] a) {
        System.out.println(new Work().run());
    }
}

class Work {
    public int run() {
        int[] lista;
        lista = new int[5];
        lista[0] = 42;
        return lista[0];
    }
}