class TesteN4MuitosArgs {
    public static void main(String[] args) {
        // Passamos 6 argumentos. A soma esperada é 21.
        System.out.println(new Calculadora().somarTodos(1, 2, 3, 4, 5, 6));
    }
}

class Calculadora {
    // a, b e c vão para os registadores $a1, $a2 e $a3
    // d, e e f terão de ser lidos da memória (offsets 16, 20 e 24)
    public int somarTodos(int a, int b, int c, int d, int e, int f) {
        int temp1;
        int temp2;
        int temp3;

        temp1 = a + b;
        temp2 = c + d;
        temp3 = e + f;

        return temp1 + temp2 + temp3;
    }
}