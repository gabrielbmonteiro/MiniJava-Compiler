class TesteElse {
    public static void main(String[] a) {
        System.out.println(new Runner().run(10, 5));
    }
}

class Runner {
    public int run(int a, int b) {
        int x;
        if (a < 20)
            if (b < 10) x = 1;
            else x = 2; // Este else pertence ao 'if (b < 10)'
        else x = 3;     // Este else pertence ao 'if (a < 20)'
        return x;
    }
}