package br.ufc.minijava;

import br.ufc.minijava.parser.MiniJavaParser;
import br.ufc.minijava.parser.ParseException;
import br.ufc.minijava.parser.TokenMgrError;

import syntaxtree.Program;
import semantic.BuildSymbolTableVisitor;
import semantic.TypeCheckVisitor;
import mips.MipsFrame;
import translate.Frag;
import translate.ProcFrag;
import translate.DataFrag;
import flowgraph.AssemFlowGraph;
import regalloc.Liveness;
import regalloc.Color;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Uso: java br.ufc.minijava.Main <ficheiro.java>");
            return;
        }

        try {
            FileInputStream ficheiro = new FileInputStream(args[0]);
            new MiniJavaParser(ficheiro);
            Program root = MiniJavaParser.Program();

            // Passo 1: Construção da Tabela de Símbolos
            BuildSymbolTableVisitor buildSymTab = new BuildSymbolTableVisitor();
            root.accept(buildSymTab);

            if (buildSymTab.getQuantidadeErros() > 0) {
                System.err.println("\nAnalise concluida. Foram encontrados " + buildSymTab.getQuantidadeErros() + " erro(s) de declaracao.");
                return;
            }

            // Passo 2: Verificação de Tipos
            TypeCheckVisitor typeCheck = new TypeCheckVisitor(buildSymTab.getTable());
            root.accept(typeCheck);

            if (typeCheck.getQuantidadeErros() > 0) {
                System.err.println("\nAnalise concluida. Foram encontrados " + typeCheck.getQuantidadeErros() + " erro(s) semantico(s).");
                return;
            } else {
                System.out.println("\nAnalise concluida com sucesso! Nenhum erro lexico, sintatico ou semantico.");
            }

            // Passo 3: Geração da Árvore Intermédia (IR)
            MipsFrame mipsFrame = new MipsFrame();
            visitor.TranslateVisitor translateVisitor = new visitor.TranslateVisitor(mipsFrame, buildSymTab.getTable());
            root.accept(translateVisitor);

            Frag fragments = translateVisitor.getResult();

            // Passos 4 e 5: Canonização, Seleção de Instruções e Alocação
            System.out.println("\n=== SELECAO E ALOCACAO DE REGISTRADORES MIPS (N4 & N5) ===");
            Frag f = fragments;

            temp.TempMap baseTempMap = new temp.CombineMap(mipsFrame, new temp.DefaultMap());

            while (f != null) {
                if (f instanceof ProcFrag proc) {
                    System.out.println("\n>>> Metodo: " + proc.frame.name.toString());

                    // 4.1 Canonização: Lineariza a árvore, agrupa em blocos básicos e ordena os saltos (TraceSchedule)
                    tree.StmList stms = canon.Canon.linearize(proc.body);
                    canon.BasicBlocks b = new canon.BasicBlocks(stms);
                    tree.StmList traced = new canon.TraceSchedule(b).stms;

                    // 4.2 Seleção de Instruções (Maximal Munch)
                    mips.Codegen codegen = new mips.Codegen((MipsFrame) proc.frame);
                    for (tree.StmList s = traced; s != null; s = s.tail) {
                        codegen.munchStm(s.head);
                    }

                    // 4.3 Aplicar o Liveness Sink
                    assem.InstrList instrs = codegen.getInstrList();
                    instrs = proc.frame.procEntryExit2(instrs);

                    System.out.println("--- Iniciando Alocacao de Registradores (N5) ---");

                    AssemFlowGraph flowGraph = new AssemFlowGraph(instrs);
                    Liveness liveness = new Liveness(flowGraph);
                    Color allocator = new Color(liveness, (MipsFrame) proc.frame, proc.frame.registers());

                    temp.TempMap finalTempMap;
                    if (allocator.spills() != null) {
                        System.err.println("[AVISO] Ocorreu SPILL neste metodo! Temporarios necessitam de ir para a memoria.");
                        finalTempMap = baseTempMap;
                    } else {
                        System.out.println("[SUCESSO] Alocacao concluida sem spills.");
                        finalTempMap = allocator;
                    }

                    // 5.1 Embrulhar as instruções finais
                    instrs = proc.frame.procEntryExit3(instrs);

                    // 5.2 Formatação e Impressão do Assembly Final
                    for (assem.InstrList i = instrs; i != null; i = i.tail) {
                        if (!i.head.assem.isEmpty()) {
                            System.out.println(i.head.format(finalTempMap));
                        }
                    }
                    System.out.println("---------------------------------------------");

                } else if (f instanceof DataFrag data) {
                    System.out.println("\n>>> Dados na Memoria (VTable):");
                    System.out.print(data.data);
                    System.out.println("---------------------------------------------");
                }
                f = f.next;
            }

        } catch (FileNotFoundException e) {
            System.out.println("Erro: Ficheiro nao encontrado - " + args[0]);
        } catch (ParseException e) {
            System.err.println("\n[ERRO SINTATICO]");
            int linha = e.currentToken.next.beginLine;
            int coluna = e.currentToken.next.beginColumn;
            String tokenEncontrado = e.currentToken.next.image;

            System.err.println("-> Erro na linha " + linha + ", coluna " + coluna);
            System.err.println("-> Token inesperado: \"" + tokenEncontrado + "\"");

            if (e.expectedTokenSequences.length > 0) {
                System.err.print("-> O parser esperava um destes tokens: ");
                for (int i = 0; i < Math.min(e.expectedTokenSequences.length, 5); i++) {
                    System.err.print(e.tokenImage[e.expectedTokenSequences[i][0]] + " ");
                }
                System.err.println();
            }
            System.err.println("--------------------------------------------------");
        } catch (TokenMgrError e) {
            System.err.println("\n[ERRO LEXICO]");
            System.err.println("-> " + e.getMessage());
            System.err.println("--------------------------------------------------");
        }
    }

}