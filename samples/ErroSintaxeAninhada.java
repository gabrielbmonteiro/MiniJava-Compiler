class ErroAninhado {
    public static void main(String[] a) {
        System.out.println(1);
    }

    if (true) { } // Erro: Statement não pode ficar solto na classe
}