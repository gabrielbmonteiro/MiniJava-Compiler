class VariavelNaoDeclarada {
    public static void main(String[] a) {
        System.out.println(new Logica().calcular());
    }
}

class Logica {
    public int calcular() {
        int x;
        x = 10;
        // ERRO: 'y' não foi declarado em nenhum escopo
        return x + y;
    }
}