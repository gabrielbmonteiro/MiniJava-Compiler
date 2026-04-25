class TesteN3Objetos {
    public static void main(String[] args) {
        System.out.println(new Derivada().iniciar());
    }
}

class Base {
    int valorBase;
    public int setBase(int v) {
        valorBase = v;
        return valorBase;
    }
}

class Derivada extends Base {
    int valorDerivado;

    public int iniciar() {
        int temp;
        Derivada obj;

        obj = new Derivada();
        temp = this.setBase(100);
        valorDerivado = temp + 50;

        return valorDerivado;
    }
}