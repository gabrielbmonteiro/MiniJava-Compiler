class TesteN3Vetores {
    public static void main(String[] args) {
        System.out.println(new VetorApp().iniciar());
    }
}

class VetorApp {
    public int iniciar() {
        int[] arr;
        int tamanho;
        int valor;

        arr = new int[5];
        tamanho = arr.length;

        arr[0] = tamanho;
        arr[1] = 42;
        valor = arr[1];

        return valor;
    }
}