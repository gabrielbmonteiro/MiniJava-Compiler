class Sobrescrita {
    public static void main(String[] a) {
        System.out.println(0);
    }
}

class SuperClasse {
    // Método original espera um booleano
    public int processar(boolean flag) {
        return 1;
    }
}

class SubClasse extends SuperClasse {
    // ERRO: Tentativa de sobrescrever o método mudando o argumento para int
    public int processar(int flag) {
        return 0;
    }
}