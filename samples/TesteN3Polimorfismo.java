class TesteN3Polimorfismo {
    public static void main(String[] args) {
        System.out.println(new Teste().rodar());
    }
}

class Animal {
    public int falar() {
        return 0; // Som genérico
    }
    public int andar() {
        return 10; // Velocidade base
    }
}

class Cachorro extends Animal {
    // Substitui o método do pai
    public int falar() {
        return 1; // Latido
    }
}

class Teste {
    public int rodar() {
        Animal pet;
        pet = new Cachorro();

        // Polimorfismo em acção!
        // O compilador não sabe que é um Cachorro apenas pelo tipo da variável.
        // Terá de consultar a VTable em tempo de execução.
        return pet.falar();
    }
}