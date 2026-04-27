class TesteLength {
    public static void main(String[] a) {
        System.out.println(new Arr().getLen());
    }
}

class Arr {
    public int getLen() {
        int[] lista;
        lista = new int[10];
        return lista.length;
    }
}