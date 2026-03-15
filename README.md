# MiniJava Compiler - Equipe 02

Este repositório contém a implementação do front-end de um compilador para a linguagem **MiniJava**, desenvolvido para a disciplina de Compiladores. A fase atual contempla a **Análise Léxica e a Análise Sintática (N1)**.

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
Durante o desenvolvimento, foram identificados e corrigidos:
* **Conflitos de Lookahead:** O parser confundia atribuições simples (`a = 1`) com atribuições de array (`a[0] = 1`). Resolvido com `LOOKAHEAD(2)`.
* **Omissão de Operadores:** Inicialmente, o operador de divisão `/` não estava na gramática, causando erro léxico em expressões matemáticas complexas.
* **Atributo Length:** O parser esperava parênteses em `.length()`, mas foi ajustado para aceitar a forma de atributo `.length` conforme a especificação.

### 4. Dificuldades Encontradas
A maior dificuldade foi configurar a **precedência de operadores** e resolver ambiguidades sintáticas sem causar recursão infinita no **JavaCC**.

### 5. Participação da Equipe
* **Gabriel Batista Monteiro:** Responsável pela arquitetura do parser no JavaCC, definição da gramática, resolução de conflitos de precedência via `LOOKAHEAD` e criação da infraestrutura de build com Maven e dos scripts de automação de testes.
* **Albert Moren Paulino da Anunciação:** [A preencher pelo membro]
* **Antônio Kevin Carvalho Primo:** [A preencher pelo membro]

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