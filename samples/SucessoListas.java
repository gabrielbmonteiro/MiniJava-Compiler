class TesteListas {
    public static void main(String[] a) {
        System.out.println(new Calc().start());
    }
}

class Calc {
    public int start() {
        int x;
        x = this.noArgs();
        x = this.oneArg(1);
        x = this.manyArgs(1, 2, 3);
        return x;
    }

    public int noArgs() { return 0; }
    public int oneArg(int a) { return a; }
    public int manyArgs(int a, int b, int c) { return a + b + c; }
}