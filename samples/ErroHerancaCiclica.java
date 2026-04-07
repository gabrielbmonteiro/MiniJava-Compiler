class HerancaCiclica {
    public static void main(String[] a) {
        System.out.println(0);
    }
}

class A extends B {
    public int m() { return 1; }
}

class B extends A {
    public int n() { return 2; }
}

class Tentativa {
    public int executar() {
        A obj;
        obj = new B(); // chamada ao isSubType("B", "A")
        return obj.m();
    }
}