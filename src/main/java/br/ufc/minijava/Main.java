package br.ufc.minijava;

import br.ufc.minijava.parser.MiniJavaParser;
import br.ufc.minijava.parser.ParseException;
import br.ufc.minijava.parser.TokenMgrError;

import syntaxtree.Program;
import semantic.BuildSymbolTableVisitor;
import semantic.TypeCheckVisitor;

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

            // Passo 1 da Semântica: Percorre a AST para construir a Tabela de Símbolos
            BuildSymbolTableVisitor buildSymTab = new BuildSymbolTableVisitor();
            root.accept(buildSymTab);

            // checar se existem duplicatas
            if (buildSymTab.getQuantidadeErros() > 0) {
                System.err.println("\nAnalise concluida. Foram encontrados " + buildSymTab.getQuantidadeErros() + " erro(s) de declaracao.");
                return;
            }

            // Passo 2 da Semântica: Percorre a AST verificando os tipos usando a tabela criada
            TypeCheckVisitor typeCheck = new TypeCheckVisitor(buildSymTab.getTable());
            root.accept(typeCheck);

            if (typeCheck.getQuantidadeErros() > 0) {
                System.err.println("\nAnalise concluida. Foram encontrados " + typeCheck.getQuantidadeErros() + " erro(s) semantico(s).");
            } else {
                System.out.println("\nAnalise concluida com sucesso! Nenhum erro lexico, sintatico ou semantico.");
            }

            // 1. Instancia a fábrica de Frames do MIPS
            Frame.Frame mipsFrame = new Mips.MipsFrame();

            // 2. Instancia o visitante de tradução passando o frame e a sua tabela de símbolos da N2
            visitor.TranslateVisitor translateVisitor = new visitor.TranslateVisitor(mipsFrame, buildSymTab.getTable());

            // 3. Inicia a tradução a partir da raiz da AST
            root.accept(translateVisitor);

            // 4. Recupera a lista de fragmentos gerados
            Translate.Frag fragments = translateVisitor.getResult();

            // 5. Imprime a Árvore IR de cada método
            System.out.println("=== ARVORES DE REPRESENTACAO INTERMEDIARIA (IR) ===");
            Translate.Frag f = fragments;
            Tree.Print irPrinter = new Tree.Print(System.out);

            while (f != null) {
                if (f instanceof Translate.ProcFrag proc) {
                    System.out.println("Metodo: " + proc.frame.name.toString());
                    irPrinter.prStm(proc.body);
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