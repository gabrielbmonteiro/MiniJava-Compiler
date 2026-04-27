class Sucesso {
    public static void main(String[] a) {
        System.out.println(new Operacoes().executar());
    }
}

class Operacoes {
    public int executar() {
        int[] lista;
        int resultado;
        lista = new int[5];
        lista[0] = 10;
        resultado = this.calcular(lista[0], true);
        return resultado;
    }

    public int calcular(int n, boolean cond) {
        int total;
        if (cond) {
            total = n * 2;
        } else {
            total = 0;
        }
        return total;
    }
}