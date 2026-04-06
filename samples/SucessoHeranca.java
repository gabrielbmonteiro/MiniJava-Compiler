class Heranca {
    public static void main(String[] a) {
        System.out.println(new Logica().executar());
    }
}

class Animal {
    public int fazerSom() {
        return 1;
    }
}

class Cachorro extends Animal {
    // Cachorro herda fazerSom() de Animal
}

class Logica {
    public int executar() {
        Animal meuPet;
        meuPet = new Cachorro();
        return meuPet.fazerSom();
    }
}