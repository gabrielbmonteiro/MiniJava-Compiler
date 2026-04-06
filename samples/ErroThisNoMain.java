class This {
    public static void main(String[] a) {
        // ERRO: Tentativa de usar 'this' num contexto estático (main)
        System.out.println(this.iniciar());
    }
}

class Auxiliar {
    public int iniciar() {
        return 1;
    }
}