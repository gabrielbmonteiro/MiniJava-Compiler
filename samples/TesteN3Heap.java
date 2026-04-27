class TesteN3Heap {
    public static void main(String[] args) {
        System.out.println(new HeapTest().run());
    }
}

class HeapTest {
    public int run() {
        int[] arr;
        boolean flag;

        arr = new int[5];
        arr[2] = 42;

        // Testa curto-circuito (&&) e ArrayLookup ao mesmo tempo
        flag = (0 < 1) && (arr[2] < 50);

        return arr[2];
    }
}