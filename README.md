# MiniJava Compiler - Equipe 02

Este repositório contém a implementação de um compilador para a linguagem **MiniJava**, desenvolvido para a disciplina de Compiladores. A fase atual contempla a **Alocação de Registradores (N5)**.

## 📋 Relatório de Conclusão - Etapa N5

### 1. Status da Etapa

### 2. Testes Realizados

### 3. Erros de Execução Encontrados

### 4. Dificuldades Encontradas

### 5. Participação da Equipe
* **Gabriel Batista Monteiro:** 

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
Para processar um ficheiro MiniJava e ver o código Assembly MIPS resultante, utilize:
```bash
    mvn exec:java "-Dexec.mainClass=br.ufc.minijava.Main" "-Dexec.args=samples/TesteN4MuitosArgs.java"
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
