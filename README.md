# MiniJava Compiler - Equipe 02

Este repositório contém a implementação de um compilador para a linguagem **MiniJava**, desenvolvido para a disciplina de Compiladores. A fase atual contempla a **Análise Léxica e a Análise Sintática (N1)**.

## 📋 Relatório de Conclusão - Etapa N1

### 1. Status da Etapa
**Concluída Completamente**. O compilador é capaz de realizar a análise léxica (identificação de tokens, descarte de comentários e espaços) e a análise sintática (validação da estrutura gramatical) de qualquer programa escrito em MiniJava, seguindo a especificação oficial.

### 2. Testes Realizados
O programa foi testado utilizando uma bateria de testes automatizada que inclui:

* **SucessoFatorial.java:** Teste clássico de recursão e estrutura de classes.
* **SucessoExpressoes.java:** Validação de precedência aritmética (incluindo o operador de divisão `/`).
* **SucessoEscopo.java:** Teste de variáveis globais e locais e ordem de declaração.
* **SucessoLength.java:** Validação do atributo especial `.length` de arrays.
* **ErroSintatico.java / ErroLexico.java:** Entradas propositalmente incorretas para validar a robustez do tratamento de erros.

### 3. Erros de Execução Encontrados
Durante o desenvolvimento e a passagem pela bateria de testes, foram identificados e corrigidos os seguintes problemas:

* **Conflito Léxico/Sintático no Atributo `.length`:** O parser lançava um erro (`Esperava-se: "("`) ao ler `lista.length`. A causa foi que o Analisador Léxico interpretava "length" como um `<ID>` comum, o que forçava o parser a entrar na regra de chamada de métodos (`<DOT> <ID> <LPAREN> ...`). 
  * **Resolução:** Criação de um token reservado `<LENGTH>` com precedência sobre `<ID>` e ajuste na regra `PrimaryExpression()` para aceitar `<DOT> <LENGTH>` sem exigir parênteses.
* **Ambiguidade de Atribuição (Shift-Reduce equivalente):** O parser falhava ao distinguir uma atribuição simples (`x = 1`) de uma atribuição de array (`x[0] = 1`), pois ambas começam com o token `<ID>`. 
  * **Resolução:** Implementação de `LOOKAHEAD(2)` na regra de `Statement()` para forçar o parser a "espiar" o próximo token (`=` ou `[`) antes de tomar a decisão de derivação.
* **Omissão de Operadores Aritméticos:** A gramática base do MiniJava não cobre nativamente o operador de divisão (`/`). 
  * **Resolução:** O token `<DIVIDE>` foi adicionado manualmente e integrado ao nível correto de precedência na regra `MultiplicativeExpression()`.

### 4. Dificuldades Encontradas
As principais barreiras técnicas enfrentadas nesta etapa foram:

* **Adaptação para Gramática LL(k):** O JavaCC utiliza análise preditiva descendente. O maior desafio foi reescrever regras que naturalmente teriam "recursão à esquerda" (como as regras de precedência matemática) para uma abordagem iterativa (usando o fecho de Kleene `*`), evitando loops infinitos no compilador.
* **Encadeamento de Sufixos em Expressões:** Modelar a gramática para suportar encadeamentos complexos na mesma linha (ex: `objeto.metodo()[0].length`) exigiu um refinamento na regra `PrimaryExpression()`, garantindo que os sufixos de acesso a arrays, chamadas de métodos e o atributo de tamanho não entrassem em conflito sintático.

### 5. Participação da Equipe
* **Gabriel Batista Monteiro:** Responsável pela arquitetura do parser no JavaCC, definição da gramática, resolução de conflitos de precedência via `LOOKAHEAD` e criação dos scripts de automação de testes.

## 🚀 Como Executar o Projeto

### Pré-requisitos
* Java JDK 17 ou superior.
* Maven 3.6 ou superior.

### 1. Compilar e Gerar o Parser
Sempre que houver alterações no ficheiro `.jj` ou no código Java, execute o comando abaixo para garantir que o projeto seja reconstruído:
```bash
    mvn compile
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
