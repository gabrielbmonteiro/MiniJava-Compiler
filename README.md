# MiniJava Compiler - Equipe 02

Este repositório contém a implementação de um compilador para a linguagem **MiniJava**, desenvolvido para a disciplina de Compiladores. A fase atual contempla a **Seleção de Instruções (N4)**.

## 📋 Relatório de Conclusão - Etapa N4

### 1. Status da Etapa
**Concluída Completamente.** O compilador é agora capaz de receber as Árvores IR da etapa anterior (N3), prepará-las e convertê-las em uma lista linear de instruções de máquina MIPS.

### 2. Testes Realizados
O compilador foi validado com foco na verificação do Assembly MIPS gerado, utilizando os seguintes cenários:

* `TesteN4MuitosArgs.java`: Validação estrita da Pilha e Convenção de Chamadas. Uma função recebe 6 parâmetros; o compilador mapeia corretamente `this`, arg1, arg2, arg3 para `$a0-$a3`, e usa `lw` com o `$fp` (Frame Pointer) nos offsets 16, 20 e 24 para resgatar os argumentos excedentes.
* `SucessoHeranca.java` / `TesteN3Polimorfismo.java`: Confirma que a instrução gerada para chamadas de métodos sobrescritos utiliza navegação em VTable (`lw` encadeados) culminando em um `jalr`, e não em saltos estáticos (`jal`).
* `SucessoFatorial.java` / `SucessoDanglingElse.java`: Valida a Canonização. Garante que os nós `SEQ`, aninhamentos de if/else e loops se transformem em blocos lineares puramente guiados por `blt`, `bne`, `j` e Rótulos (Labels).
* `SucessoArrayAssign.java`: Teste da aritmética de ponteiros para alocação e indexação de vetores em memória (`mul` por 4 bytes e `add` com endereço base).

### 3. Erros de Execução Encontrados

Durante a transformação da Árvore IR em instruções Assembly, identificamos e corrigimos os seguintes problemas críticos:

* **NullPointerException ao tentar resolver `Tree.NAME`:** Ao tentar salvar a VTable no objeto alocado (via um `MOVE`), o método `munchExp` quebrava ao receber o nome do rótulo da VTable por não ter uma regra de tradução definida.
  * Resolução: Adição de uma regra no Maximal Munch para `Tree.NAME`, emitindo a instrução MIPS `la` (Load Address) que carrega o endereço estático para um registrador antes de guardá-lo na memória do objeto.
* **Colisão de Classes com Mesmo Nome (Ambiguidade de Pacotes):** O compilador Java falhava ao importar ambos os pacotes `Tree` e `Assem`, pois ambos possuem classes chamadas `MOVE` e `LABEL`.
  * Resolução: Refatoração completa da classe `Codegen` utilizando Fully Qualified Names (ex: `Tree.MOVE` vs `Assem.MOVE`), isolando as estruturas arquiteturais sem comprometer a limpeza do código.
* **Falta de Declaração dos Registradores "Sujos" (`calldefs`):** O pacote `Assem` do livro exigia uma lista de temporários alterados durante uma chamada de função (`jal`), causando falhas na compilação.
  * Resolução: Implementação do método `calldefs` no `MipsFrame`, retornando todos os registradores Caller-Saves do MIPS (`$t0-$t9`, `$a0-$a3`, `$v0-$v1`, `$ra`), essencial para o alocador de registradores.

### 4. Dificuldades Encontradas
As principais barreiras técnicas superadas nesta etapa foram:

* **Gestão do "View Shift":** Garantir que, ao entrar num método, o código Assembly "soubesse" que os argumentos estavam nos registradores `$a0-$a3`. A solução exigiu criar o `procEntryExit1` gerando `MOVE`s explícitos entre os registradores físicos do MIPS e os temporários alocados para o Frame corrente.
* **Proteger Registradores Especiais:** Entender por que o compilador precisaria do `procEntryExit2`. Foi necessário forjar uma instrução vazia (`""`) no final de cada função que consumisse os registradores `$v0`, `$ra` e `$fp`, garantindo a integridade dos dados no retorno.
* **Legibilidade do Assembly Gerado:** Os temporários gerados programaticamente recebiam nomes como `t21`, `t34`, mascarando a convenção de chamadas.

### 5. Participação da Equipe
* **Gabriel Batista Monteiro:** Responsável pela implementação completa da classe `Codegen.java`, mapeamento da arquitetura de registradores MIPS no MipsFrame (`calldefs`, `$a0-$a3`), implementação de "View Shift" via `procEntryExit1` e "Liveness Sink" via `procEntryExit2`, resolução de passagens de parâmetros longos (>4 args) via Pilha, e integração do pacote `Canon` no loop principal.
* **Albert Moren Paulino da Anunciação:** [A preencher]
* **Antônio Kevin Carvalho Primo:** [A preencher]

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
Para processar um ficheiro MiniJava e ver o código Assembly MIPS resultante gerado pelo processo de Canonização e Seleção de Instruções, utilize:
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
