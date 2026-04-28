# MiniJava Compiler - Equipe 02

Este repositório contém a implementação de um compilador para a linguagem **MiniJava**, desenvolvido para a disciplina de Compiladores. A fase atual contempla a **Alocação de Registradores (N5)**.

## 📋 Relatório de Conclusão - Etapa N5

### 1. Status da Etapa
**Concluída Completamente.** O compilador agora realiza a análise de fluxo de dados e coloração de grafos para mapear temporários infinitos nos registradores físicos da arquitetura MIPS. Foram implementadas otimizações de Coalescimento de Registradores (IRC), Tratamento de Spills com reescrita iterativa de código e Otimização Peephole.

### 2. Testes Realizados
A validação final foi executada sobre toda a bateria de testes, garantindo que o Assembly gerado seja 100% nativo e executável:

* `Teste.java` / `TesteN3Aritmetica.java`: Validação do uso de registradores Callee-Saves (`$s0-$s7`). O compilador identifica no método `Matematica_fatorial` que as variáveis precisam sobreviver à chamada recursiva (`jalr`) e as aloca em registradores protegidos, preservando a integridade da recursão sem necessidade de spills manuais.
* `TesteN3Completo.java`: Teste de estresse que validou o ciclo de RewriteProgram. No método `B_runTest`, o compilador gerencia uma alta carga de temporários virtuais, detectando a necessidade de memória, alocando espaço na pilha via `allocLocal` e inserindo instruções de `lw` e `sw` de forma iterativa.
* `SucessoListas.java`: Confirmação da expansão do Frame para chamadas com muitos argumentos (método `Calculadora_somarTodos)`. O prólogo aloca a margem de segurança de 32 bytes, permitindo que os argumentos excedentes (5º em diante) sejam escritos e lidos da pilha com `sw` e `lw` nos offsets corretos.
* `TesteN3Fluxo.java`: Demonstra a eficácia do Coalescimento (Coalescing) e do Peephole. No método `FluxoApp_executar`, instruções de MOVE redundantes entre temporários que não interferem foram eliminadas, resultando em um código MIPS limpo e sem a instrução `move $t0, $t0`.

### 3. Erros de Execução Encontrados
Durante o desenvolvimento da alocação e otimização, identificamos e corrigimos os seguintes problemas:

* **Falsos Spills em Métodos Recursivos:** Inicialmente, variáveis que cruzavam chamadas de função sofriam spill por falta de cores, pois o compilador só conhecia os registradores `$t`.
  * Resolução: Expansão do método `registers()` no `MipsFrame` para incluir os registradores Callee-Saves (`$s0-$s7`), permitindo ao alocador preservar dados durante chamadas de sub-rotinas.
* **Violação de Memória:** Em testes com muitos argumentos, o Assembly tentava escrever em offsets altos (ex: `24($sp)`) enquanto o prólogo só abria 8 bytes de espaço.
  * Resolução: Ajuste da lógica do `procEntryExit3` para garantir um `frameSize` mínimo de 32 bytes (32-byte stack alignment), cobrindo argumentos excedentes e salvamento de registradores de controle.
* **Redundância pós-Coloração:** O alocador frequentemente atribuía o mesmo registrador físico para origem e destino de um `MOVE`, gerando instruções inúteis como `move $t0, $t0`.
  * Resolução: Implementação de um filtro Peephole que remove estas instruções da lista final de `Assem.Instr`, reduzindo o tamanho do binário gerado.

### 4. Dificuldades Encontradas
Os principais desafios técnicos superados foram:

* **Cálculo de Ponto Fixo (Liveness Analysis):** Implementar as equações de fluxo de dados de forma iterativa exigiu o uso de estruturas de dados eficientes (`HashSet`) para garantir que os conjuntos Live-In e Live-Out convergissem rapidamente sem degradar o tempo de compilação.
* **Consistência da Pilha no Rewrite:** Garantir que o `frameSize` no `procEntryExit3` refletisse os novos espaços de memória criados dinamicamente durante a fase de Spill. A solução foi acoplar o `RewriteProgram` ao método `allocLocal` do Frame corrente, garantindo o ajuste automático dos offsets.
* **Convenção de Chamada MIPS:** Inicialmente, o compilador gerava Spills excessivos. O problema foi identificado como falta de registradores protegidos. A adição de `$s0-$s7` à lista de cores permitiu que o grafo de interferência resolvesse conflitos de forma muito mais inteligente.

### 5. Participação da Equipe
* **Gabriel Batista Monteiro:** Implementação integral de todo o pipeline da N5: construção do Grafo de Fluxo (`AssemFlowGraph`), Análise de Longevidade, construção do Grafo de Interferência, Algoritmo de Coloração de Kempe/Chaitin, Lógica de Reescrita de Programa (Spilling), Coalescimento de Registradores e Filtro Peephole.

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
Para ver o compilador em ação, realizando as tentativas de alocação e gerando o MIPS final sem variáveis virtuais:
```bash
    mvn exec:java "-Dexec.mainClass=br.ufc.minijava.Main" "-Dexec.args=samples/SucessoFatorial.java"
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
