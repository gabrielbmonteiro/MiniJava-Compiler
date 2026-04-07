class TipoInexistente {
    public static void main(String[] a) {
        System.out.println(new CausarErro().rodar());
    }
}

class CausarErro {
    public int rodar() {
        Inexistente objeto; // Tipo referenciando classe que nao existe
        objeto = new Inexistente(); // Instanciacao de classe inexistente
        return 0;
    }
}