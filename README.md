# Compilador MiniJava para MIPS ☕⚙️

Este repositório contém o projeto de implementação de um compilador completo para a linguagem **MiniJava**, tendo como código alvo a arquitetura **MIPS**. 

O desenvolvimento deste compilador faz parte do critério de avaliação da disciplina de Compiladores, sendo projetado, estruturado e codificado por mim.

---

## 🌿 Estrutura do Repositório (Branches)

Para refletir a evolução natural do pipeline de compilação e manter a organização do código, **cada fase do compilador foi desenvolvida e isolada em uma branch específica**. 

A branch `main` atual serve como o ponto de consolidação e documentação. Para visualizar a implementação detalhada de cada módulo, por favor, navegue para a branch correspondente à etapa desejada.

---

## 📌 Etapas de Avaliação e Desenvolvimento

A construção e avaliação do compilador foram divididas rigorosamente em **5 etapas sequenciais (N1 a N5)**. Em cada etapa, um novo componente do pipeline de compilação foi implementado e integrado ao projeto:

### 🔍 N1: Analisador Léxico e Sintático
* **Objetivo:** Ler o código-fonte em MiniJava, agrupar os caracteres em *tokens* válidos (Análise Léxica) e validar a estrutura gramatical da linguagem (Análise Sintática).
* **Branch recomendada:** `[n1-analisador-lexico-sintatico]`

### 🌳 N2: Árvore Sintática Abstrata (AST) e Análise Semântica
* **Objetivo:** Construir a representação estrutural do código (AST) e realizar a checagem de tipos, validação de escopo, declaração de variáveis e verificação de regras semânticas da linguagem orientada a objetos.
* **Branch recomendada:** `[n2-AST-e-analisador-semantico]`

### ⚙️ N3: Tradução para o Código Intermediário (IR)
* **Objetivo:** Converter a Árvore Sintática Abstrata (AST) validada em uma Representação Intermediária (IR Tree) de baixo nível, independente da arquitetura final, facilitando otimizações.
* **Branch recomendada:** `[n3-traducao-codigo-intermediario]`

### 🎯 N4: Seleção de Instruções
* **Objetivo:** Mapear os nós da Árvore de Representação Intermediária (IR) para instruções reais do conjunto de instruções da arquitetura **MIPS** (*Assembly MIPS*).
* **Branch recomendada:** `[n4-selecao-instrucoes]`

### 🗄️ N5: Alocação de Registradores
* **Objetivo:** Substituir os registradores temporários infinitos gerados na etapa anterior por registradores físicos reais da máquina MIPS, utilizando algoritmos de coloração de grafos de interferência e tratamento de *spills* (vazamentos de memória).
* **Branch recomendada:** `[n5-alocacao-registradores]`

---

*Projeto puramente acadêmico focado na compreensão prática das fases de compilação, desde o front-end léxico/sintático até o back-end de geração de código de máquina.*
