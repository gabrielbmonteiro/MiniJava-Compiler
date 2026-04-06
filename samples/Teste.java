class Teste {
    public static void main(String[] a) {
        // Ponto de entrada: aceitar apenas um comando e não pode usar 'this'
        System.out.println(new BateriaDeTestes().executarTudo());
    }
}

class BateriaDeTestes {
    public int executarTudo() {
        // 1. Declarações antes dos comandos
        int[] meuArray;
        int resultadoAritmetico;
        boolean flagBooleana;
        Animal meuPet;
        Cachorro meuCachorro;
        Matematica math;

        // 2. Teste de Expressões e Precedência (+, *, <, &&, !)
        flagBooleana = (10 < 20) && (!(false));
        if (flagBooleana) {
            resultadoAritmetico = 1 + 2 * 3; // Deve respeitar precedência
        } else {
            resultadoAritmetico = 0;
        }

        // 3. Teste de Arrays (Instanciação, Atribuição, Length e Lookup)
        meuArray = new int[5];
        meuArray[0] = resultadoAritmetico;
        meuArray[1] = meuArray.length;

        // 4. Teste de Laço While e Impressão
        while (0 < meuArray[1]) {
            System.out.println(meuArray[1]); // Vai imprimir de 5 até 1
            meuArray[1] = meuArray[1] - 1;
        }

        // 5. Teste de Polimorfismo (Subtipagem) e Instanciação
        meuCachorro = new Cachorro();
        meuPet = meuCachorro; // ACEITO: Cachorro é subclasse de Animal

        // 6. Teste de Acesso a Atributos e Métodos Herdados
        resultadoAritmetico = meuCachorro.setIdade(5); // setIdade é herdado de Animal
        resultadoAritmetico = meuCachorro.configurar(10); // Acessa o atributo herdado dentro da classe

        // 7. Teste de Sobrescrita de Método
        resultadoAritmetico = meuPet.fazerSom(); // Vai chamar o método de Cachorro, mesmo a ref sendo Animal

        // 8. Teste de Chamada de Método Complexa com 'this'
        math = new Matematica();
        resultadoAritmetico = math.fatorial(5);

        return resultadoAritmetico;
    }
}

// Hierarquia de Classes para testar Herança
class Animal {
    int idade;

    public int fazerSom() {
        return 0;
    }

    public int setIdade(int n) {
        idade = n;
        return idade;
    }
}

class Cachorro extends Animal {
    int peso;

    // Sobrescrita exata: mesmo tipo de retorno e argumentos da superclasse
    public int fazerSom() {
        return 1;
    }

    public int configurar(int p) {
        peso = p;
        idade = p; // ACESSO A ATRIBUTO HERDADO: Testa a nossa lógica de subir na hierarquia
        return peso + idade;
    }
}

// Classe Utilitária para testar Recursão e 'this'
class Matematica {
    public int fatorial(int n) {
        int result;
        if (n < 1) {
            result = 1;
        } else {
            // Teste rigoroso: uso do 'this', chamada de método e subtração
            result = n * (this.fatorial(n - 1));
        }
        return result;
    }
}