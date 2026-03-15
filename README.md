# MiniJava Compiler - Equipe 02

Repositório destinado ao desenvolvimento do compilador para a linguagem MiniJava, como parte dos requisitos da disciplina de Compiladores.

## 📁 Estrutura do Projeto

* `src/main/java`: Contém o código fonte Java do compilador.
* `src/main/javacc`: Contém o ficheiro de gramática `MiniJavaParser.jj`.
* `samples/`: Pasta dedicada a ficheiros de teste escritos em MiniJava.

## 🚀 Como Executar o Projeto

### Pré-requisitos
* Java JDK 17 ou superior.
* Maven 3.6 ou superior instalado e configurado no PATH.

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

### (Linux/Mac/Git Bash)
```bash
    chmod +x scripts/run_tests.sh
    ./scripts/run_tests.sh
```
### (Windows PowerShell)
```bash
    .\scripts\run_tests.ps1
```

Os resultados detalhados de cada arquivo serão salvos na pasta `scripts/test_results/`.

## 🛠️ Tecnologias Utilizadas
* JavaCC: Gerador de analisadores sintáticos e léxicos.
* Maven: Gestão de dependências e automação de build.
* Java: Linguagem base para a implementação do compilador.

## 👥 Equipe
* Gabriel Batista Monteiro
* Albert Moren Paulino da Anunciação
* Antônio Kevin Carvalho Primo