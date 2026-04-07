class ErroArrays {
    public static void main(String[] a) {
        System.out.println(new Arr().test());
    }
}

class Arr {
    public int test() {
        int[] lista;
        int x;
        lista = new int[true]; // ERRO: Tamanho do array deve ser int
        x = 10;
        x[0] = 5; // ERRO: Tentativa de indexar algo que nao eh array
        lista[false] = 10; // ERRO: Indice deve ser int
        return x.length; // ERRO: .length usado em tipo nao-array
    }
}