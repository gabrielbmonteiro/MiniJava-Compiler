class TesteN3Fluxo {
    public static void main(String[] args) {
        System.out.println(new FluxoApp().executar(3));
    }
}

class FluxoApp {
    public int executar(int max) {
        int i;
        i = 0;

        while (i < max) {
            {
                System.out.println(i);

                if (i < 1) {
                    i = i + 2;
                } else {
                    i = i + 1;
                }
            }
        }

        return i;
    }
}