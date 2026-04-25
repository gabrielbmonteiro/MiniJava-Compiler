# MiniJava Compiler - Equipe 02

Este repositório contém a implementação de um compilador para a linguagem **MiniJava**, desenvolvido para a disciplina de Compiladores. A fase atual contempla a **Tradução para o Código Intermediário (N3)**.

## 📋 Relatório de Conclusão - Etapa N2

### 1. Status da Etapa
**Concluída Completamente**. O compilador é capaz de realizar a tradução da Árvore de Sintaxe Abstrata (AST) para uma Árvore de Representação Intermediária (IR Tree) pura, independente da arquitetura da máquina alvo. A implementação engloba o `TranslateVisitor`, a abstração de Frames e Registradores (focada na convenção MIPS), tradução de controle de fluxo (If/While), mapeamento dinâmico de memória no Heap e Despacho Dinâmico (Dynamic Dispatch) suportando polimorfismo real através de Tabelas de Métodos Virtuais (VTables).

### 2. Testes Realizados
O compilador foi validado utilizando uma bateria de testes modulares e de estresse focados nos nós geradores de IR:

* `TesteN3Polimorfismo.java`: Validação rigorosa do polimorfismo. Testa se uma variável do tipo superclasse consegue invocar o método sobrescrito da subclasse (Override) através de navegação de ponteiros na VTable em tempo de execução, substituindo os saltos estáticos.
* `TesteN3Aritmetica.java`: Validação de expressões matemáticas e booleanas, com foco na tradução do operador `&&` usando saltos condicionais (curto-circuito) sem utilizar instrução `AND` de máquina.
* `TesteN3Fluxo.java`: Teste rigoroso do aninhamento de `LABEL`s, `CJUMP`s e comandos sequenciais (`SEQ`) em laços `while` e blocos `if/else`.
* `TesteN3Memoria.java`: Validação da diferença entre acesso a variáveis locais (em registradores/pilha) e acesso a atributos de classe calculando o deslocamento (`offset`) a partir do ponteiro `this`.
* `TesteN3Vetores.java`: Validação da alocação de vetores (`_initArray`), leitura de tamanho mágico na base do array (`ArrayLength`), cálculo de índice de acesso e atribuição dinâmica (`ArrayLookup` e `ArrayAssign`).
* `TesteN3Completo.java`: Teste de estresse desenhado cirurgicamente para acionar os 29 nós geradores de código da AST em um único fluxo de execução, garantindo a robustez do `Visitor`.

### 3. Erros de Execução Encontrados
Durante o desenvolvimento da tradução para IR, identificamos e corrigimos os seguintes problemas críticos:

* **Referência Indefinida para VTable no Assembly:** Durante a instanciação de objetos, tentávamos armazenar um rótulo (Label) `vtable_X` no slot 0 da memória, mas o rótulo não existia fisicamente.
  * Resolução: Implementação do método `buildVTable` para gerar fragmentos de dados estáticos (`DataFrag`) contendo os endereços dos métodos (`.word`), que agora são impressos lado a lado com os fragmentos de código (`ProcFrag`).

* **NullPointerException na Declaração de Atributos de Classe:** O compilador quebrava ao tentar invocar `currentFrame.allocLocal()` para variáveis declaradas no escopo da classe (atributos), já que não existe um Frame ativo fora dos métodos.
  * Resolução: Adição de uma verificação em `VarDecl` para alocar espaço no Frame apenas se for uma variável local. Atributos de classe agora são ignorados nessa passagem e alocados dinamicamente via `_allocRecord` no `NewObject`.

* **Registradores "Fantasmas" e perda do Frame Pointer (FP):** O acesso a variáveis em memória estava gerando temporários limpos (`new Temp()`) a cada chamada, quebrando a referência com o Frame atual.
  * Resolução: Sincronização da interface abstrata `Frame` com a implementação `MipsFrame` para utilizar registradores fixos estáticos (`RV` para retorno em $v0 e `FP` para o Frame Pointer em $fp), garantindo o acesso correto na pilha.
### 4. Dificuldades Encontradas
As principais barreiras técnicas superadas nesta etapa foram:

* **Implementação Real de Polimorfismo (Dynamic Dispatch):** Adaptar o nó `Call` para não fazer chamadas estáticas pelo nome do método. Foi necessário implementar um avaliador de tipos on-the-fly (`getTypeOfExp`) no visitante de tradução, realizar aritmética de ponteiros na memória do objeto (`MEM(BINOP(PLUS, vtable, offset))`) e chamar o endereço retornado de forma dinâmica.
* **Cálculo de Deslocamento (Offset) em Herança:** Determinar a posição de um atributo na memória quando a classe atual herda de outra. A solução foi criar um método de busca recursiva (`getParentFieldCount`) na Tabela de Símbolos, garantindo que a classe filha calcule seu tamanho exato e não sobrescreva a memória da classe pai.
* **Conversão de Controle de Fluxo:** Implementar as classes abstratas `Ex`, `Nx` e `Cx` e fazer com que estruturas como o `While` montassem o seu próprio laço de repetição usando a união de `LABEL`, `JUMP` e `CJUMP`.

### 5. Participação da Equipe
* **Gabriel Batista Monteiro:** Responsável pela implementação completa do `TranslateVisitor`, lógicas de escopo em memória (offsets e herança), abstração dos nós `Nx`/`Ex`/`Cx`, implementação de VTables e Dynamic Dispatch, sincronização de convenções MIPS (`RV` e `FP`) e estruturação da bateria de testes.
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
Para processar um ficheiro MiniJava específico e ver a Árvore IR e as VTables geradas no terminal, utilize o plugin exec-maven-plugin através do comando:
```bash
    mvn exec:java "-Dexec.mainClass=br.ufc.minijava.Main" "-Dexec.args=samples/TesteN3Completo.java"
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
