class TesteN3Completo {
    public static void main(String[] args) {
        System.out.println(new B().runTest(10, 20));
    }
}

class A {
    int fieldA;
    public int setA(int v) {
        fieldA = v;
        return fieldA;
    }
}

class B extends A {
    int[] arr;
    boolean flag;

    public int runTest(int p1, int p2) {
        int local;

        // Testa: This, Call, IdentifierExp e Herança
        local = this.setA(p1);

        // Testa: NewArray
        arr = new int[5];

        // Testa: ArrayAssign e IdentifierExp
        arr[0] = local;
        arr[1] = p2;

        // Testa: ArrayLookup e Plus
        arr[2] = arr[0] + arr[1];

        // Testa: Minus e IntegerLiteral
        arr[3] = arr[2] - 5;

        // Testa: Times
        arr[4] = arr[3] * 2;

        // Testa: True
        flag = true;

        // Testa: If, Block, And (curto-circuito), LessThan, ArrayLength, Not
        if (!(arr.length < 5) && flag) {
            local = arr[4];
        } else {
            // Testa: False indiretamente (se entrasse no else)
            local = 0;
        }

        // Testa: While
        while (0 < local) {
            // Testa: Print
            System.out.println(local);
            local = local - 10;
        }

        return local;
    }
}