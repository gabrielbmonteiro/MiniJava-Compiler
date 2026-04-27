package br.ufc.minijava;

import br.ufc.minijava.parser.MiniJavaParser;
import br.ufc.minijava.parser.ParseException;
import br.ufc.minijava.parser.TokenMgrError;

import syntaxtree.Program;
import semantic.BuildSymbolTableVisitor;
import semantic.TypeCheckVisitor;
import Frame.Frame;
import Mips.MipsFrame;
import Translate.Frag;
import Translate.ProcFrag;
import Translate.DataFrag;

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
            Frame mipsFrame = new MipsFrame();
            visitor.TranslateVisitor translateVisitor = new visitor.TranslateVisitor(mipsFrame, buildSymTab.getTable());
            root.accept(translateVisitor);

            Frag fragments = translateVisitor.getResult();

            // Passo 4: Canonização e Seleção de Instruções
            System.out.println("\n=== SELECAO DE INSTRUCOES MIPS (N4) ===");
            Frag f = fragments;

            Temp.TempMap tempMap = new Temp.CombineMap((MipsFrame) mipsFrame, new Temp.DefaultMap());

            while (f != null) {
                if (f instanceof ProcFrag proc) {
                    System.out.println("\n>>> Metodo: " + proc.frame.name.toString());

                    // 4.1 Canonização: Lineariza a árvore, agrupa em blocos básicos e ordena os saltos (TraceSchedule)
                    Tree.StmList stms = Canon.Canon.linearize(proc.body);
                    Canon.BasicBlocks b = new Canon.BasicBlocks(stms);
                    Tree.StmList traced = new Canon.TraceSchedule(b).stms;

                    // 4.2 Seleção de Instruções (Maximal Munch)
                    Mips.Codegen codegen = new Mips.Codegen((MipsFrame) proc.frame);
                    for (Tree.StmList s = traced; s != null; s = s.tail) {
                        codegen.munchStm(s.head);
                    }

                    // 4.3 Aplicar o Liveness Sink
                    Assem.InstrList instrs = codegen.getInstrList();
                    instrs = proc.frame.procEntryExit2(instrs);

                    // 4.4 Formatação e Impressão do Assembly
                    for (Assem.InstrList i = instrs; i != null; i = i.tail) {
                        if (!i.head.assem.equals("")) {
                            System.out.println(i.head.format(tempMap));
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