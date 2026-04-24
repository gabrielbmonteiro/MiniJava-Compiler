# MiniJava Compiler - Equipe 02

Este repositório contém a implementação de um compilador para a linguagem **MiniJava**, desenvolvido para a disciplina de Compiladores. A fase atual contempla a **Árvore Sintática Abstrata e Análise Semântica (N2)**.

## 📋 Relatório de Conclusão - Etapa N2

### 1. Status da Etapa
**Concluída Completamente**. O compilador é capaz de realizar a Análise Semântica completa de programas escritos em MiniJava. A implementação engloba a construção da Árvore de Sintaxe Abstrata (AST), a criação da Tabela de Símbolos (para verificação de escopo e declarações) e a Verificação de Tipos (incluindo checagem de expressões e lógicas de Orientação a Objetos).

### 2. Testes Realizados
O compilador foi validado utilizando uma bateria automatizada:
* **`SemanticoSucessoTotal.java` / `Teste.java`:** Teste de estresse contendo todas as estruturas da linguagem, regras de escopo, instanciação de arrays e precedência.
* **`SucessoHeranca.java` / `SucessoAtributosHerdados.java`:** Validação de polimorfismo (subtipagem) e acesso a atributos herdados ao longo da árvore de classes.
* **`ErroSobrescritaEstrita.java`:** Verifica se o compilador proíbe a mudança de assinatura herdada e exige "Overriding" estrito.
* **`ErroHerancaCiclica.java`:** Valida a proteção do compilador contra classes que estendem umas às outras em loop.
* **`ErroParametroDuplicado.java` / `ErroVariavelNaoDeclarada.java`:** Testes de validação de escopo local, parâmetros e existência prévia de classes.
* **`ErroThisNoMain.java`:** Valida o bloqueio do uso de referência de objeto dentro do contexto estático.

### 3. Erros de Execução Encontrados
Durante o desenvolvimento da análise semântica, identificamos e corrigimos os seguintes problemas críticos:
* **Travamento por Herança Cíclica:** Ao rodar um código onde `class A extends B` e `class B extends A`, o método `isSubType` entrava em loop infinito tentando resolver a hierarquia.
  * Resolução: Implementação de um contador de profundidade (limite de saltos) no `isSubType` e uma verificação proativa no nó `ClassDeclExtends` para barrar a compilação imediatamente ao detectar o ciclo.
* **NullPointerException na AST em Classes/Métodos Vazios:** O JavaCC pode instanciar listas como `null` se um método não possuir parâmetros ou variáveis. O `TypeCheckVisitor` quebrava ao tentar chamar `.size()` nessas listas.
  * Resolução: Adição de guardas de segurança (`if (lista != null)`) antes de iterar sobre `FormalList`, `VarDeclList` e `StatementList`.
* **Falso Positivo no uso de this no main:** O compilador barrava o `this` no `main`, mas emitia uma mensagem confusa ("Método não encontrado") porque tentava procurar a chamada dentro da própria classe principal.
  * Resolução: Interceptação do nó `This` verificando se o método atual é o main. Caso positivo, dispara um erro específico de "contexto estático" e retorna um tipo seguro para evitar erros em cascata.

### 4. Dificuldades Encontradas
As principais barreiras técnicas superadas nesta etapa foram:
* **Mapeamento do Escopo Achatado:** Em vez de criar tabelas de símbolos aninhadas e complexas na memória, a dificuldade foi modelar uma tabela única que não perdesse o contexto. A solução adotada foi o uso de chaves hierárquicas em strings (ex: `Classe.Metodo.Variavel` e `Classe.extends`).
* **Resolução Hierárquica de Métodos e Atributos:** No MiniJava, uma classe filha pode acessar um método do "avô". Modelar o `TypeCheckVisitor` para subir recursivamente pela hierarquia (`while (searchClass != null)`) sempre que um método ou variável não fosse encontrado no escopo local foi um desafio lógico.
* **Diferenciação de Sobrescrita e Sobrecarga:** Garantir que um método sobrescrito na classe filha tenha exatamente o mesmo tipo da classe pai, mas ao mesmo tempo permitir que no `return` haja polimorfismo. Isso exigiu a criação de um método auxiliar de comparação rigorosa (`isExactType`) separado do verificador de subtipagem padrão (`isCompatible`).

### 5. Participação da Equipe
* **Gabriel Batista Monteiro:** Responsável pela implementação dos Visitors (`BuildSymbolTableVisitor` e `TypeCheckVisitor`), resolução das lógicas de escopo achatado, implementação de checagem de tipos (subtipagem, proteção contra ciclos) e integração com a AST
* **Albert Moren Paulino da Anunciação:** Responsável pela lógica de interceptação de escopo estático, resolvendo o tratamento adequado para o uso indevido da referência this no método main, além de auxiliar na estruturação dos casos de erro semântico.
* **Antônio Kevin Carvalho Primo:** Responsavel pela elaboração e execução da bateria de testes de estresse estrutural, regras de escopo e tipagem estrita, documentando os resultados e garantindo a validação contínua da análise semântica.

## 🚀 Como Executar o Projeto

### Pré-requisitos
* Java JDK 17 ou superior.
* Maven 3.6 ou superior.

### 1. Compilar e Gerar o Parser
Sempre que houver alterações no ficheiro `.jj` ou no código Java, execute o comando abaixo para garantir que o projeto seja reconstruído:
```bash
    mvn clean compile
```

### 2. Rodar o Compilador
Para processar um ficheiro MiniJava específico, utilize o plugin exec-maven-plugin através do comando:
```bash
    mvn exec:java "-Dexec.mainClass=br.ufc.minijava.Main" "-Dexec.args=samples/Teste.java"
```
## 🧪 Testes Automatizados
Para rodar toda a bateria de testes de uma vez e gerar logs de erro/sucesso:

### (Linux/Mac)
```bash
    chmod +x scripts/run_tests.sh
    ./scripts/run_tests.sh
```
### (Windows)
```bash
    .\scripts\run_tests.ps1
```

Os resultados detalhados de cada arquivo serão salvos na pasta `scripts/test_results/`.
