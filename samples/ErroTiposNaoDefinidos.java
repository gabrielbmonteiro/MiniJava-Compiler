class Tipos {
    public static void main(String[] a) {
        System.out.println(new Processador().calcular());
    }
}

class Processador {
    // ERRO 1: O parâmetro usa uma classe que não existe
    public int processar(ClasseFantasmaParametro param) {
        // ERRO 2: A variável local usa uma classe que não existe
        ClasseFantasmaLocal variavel;
        return 1;
    }

    public int calcular() {
        return 0;
    }
}