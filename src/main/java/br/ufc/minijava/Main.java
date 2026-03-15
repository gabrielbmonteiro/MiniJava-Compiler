package br.ufc.minijava;

import br.ufc.minijava.parser.MiniJavaParser;
import br.ufc.minijava.parser.ParseException;

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

            // Instancia o parser gerado pelo JavaCC e passa o ficheiro como entrada
            MiniJavaParser parser = new MiniJavaParser(ficheiro);

            // Chama a regra inicial da gramática (Program)
            parser.Program();

            System.out.println("Analise concluida com sucesso! Nenhum erro lexico ou sintatico.");

        } catch (FileNotFoundException e) {
            System.out.println("Erro: Ficheiro nao encontrado - " + args[0]);
        } catch (ParseException e) {
            System.out.println("--------------------------------------------------");
            System.out.println("ERRO SINTATICO DETECTADO");
            System.out.println("Linha: " + e.currentToken.next.beginLine);
            System.out.println("Coluna: " + e.currentToken.next.beginColumn);
            System.out.println("Esperava-se um dos seguintes: " + e.tokenImage[e.expectedTokenSequences[0][0]]);
            System.out.println("--------------------------------------------------");
        }
    }
}