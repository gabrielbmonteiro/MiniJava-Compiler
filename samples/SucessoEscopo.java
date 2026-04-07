class TesteEscopo {
    public static void main(String[] a) {
        System.out.println(new Logica().executar(5));
    }
}

class Logica {
    // Variáveis globais
    int global1;
    boolean status;
    int[] lista;

    public int executar(int parametro) {
        // Variáveis locais
        int local1;
        int local2;

        local1 = parametro * 2;
        local2 = local1 + global1;

        return local2;
    }
}