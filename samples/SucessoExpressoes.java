class TesteExp {
    public static void main(String[] a) {
        System.out.println(new Calculadora().calcular(10, true));
    }
}

class Calculadora {
    public int calcular(int num, boolean flag) {
        int res;
        if (!flag && num < 20) {
            res = num * 2 + 10 / (5 - 2);
        } else {
            res = num + 1;
        }
        return res;
    }
}