class Atributos {
    public static void main(String[] a) {
        System.out.println(new Filha().configurar(42));
    }
}

class Pai {
    int valorSecreto; // Declarado no Pai
}

class Filha extends Pai {
    public int configurar(int n) {
        // Acede à variável da superclasse sem declará-la aqui
        valorSecreto = n;
        return valorSecreto;
    }
}