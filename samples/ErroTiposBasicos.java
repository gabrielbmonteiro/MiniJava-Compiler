class ErroTipos {
    public static void main(String[] a) {
        System.out.println(new B().verificar());
    }
}

class B {
    public int verificar() {
        int n;
        boolean b;
        n = 10 + true; // ERRO: Soma exige dois inteiros
        b = true && 5;  // ERRO: AND exige dois booleanos

        if (n) { // ERRO: Condicao do IF deve ser boolean
            System.out.println(1);
        } else { }

        System.out.println(true); // ERRO: Print exige inteiro no MiniJava
        return b; // ERRO: Retorno do Metodo deve ser int (conforme declarado)
    }
}