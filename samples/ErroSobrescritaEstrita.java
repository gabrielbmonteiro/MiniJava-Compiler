class SobrescritaEstrita {
    public static void main(String[] a) {
        System.out.println(0);
    }
}

class Animal {}
class Cachorro extends Animal {}

class SistemaAntigo {
    // Método original espera um Animal genérico
    public int registar(Animal pet) {
        return 1;
    }
}

class SistemaNovo extends SistemaAntigo {
    // ERRO: Tentativa de sobrescrever o método mudando 'Animal' para 'Cachorro'.
    // Em Java, isso seria um Overload, não um Override
    public int registar(Cachorro pet) {
        return 2;
    }
}