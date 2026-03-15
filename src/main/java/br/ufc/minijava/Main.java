package br.ufc.minijava;

import br.ufc.minijava.parser.MiniJavaParser;
import br.ufc.minijava.parser.ParseException;
import br.ufc.minijava.parser.TokenMgrError;

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
            MiniJavaParser parser = new MiniJavaParser(ficheiro);
            parser.Program();

            System.out.println("Analise concluida com sucesso! Nenhum erro lexico ou sintatico.");

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