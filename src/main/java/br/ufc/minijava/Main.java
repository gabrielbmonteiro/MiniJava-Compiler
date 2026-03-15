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

            // Chama a regra inicial da sua gramática (Program)
            parser.Program();

            System.out.println("Analise concluida com sucesso! Nenhum erro lexico ou sintatico.");

        } catch (FileNotFoundException e) {
            System.out.println("Erro: Ficheiro não encontrado - " + args[0]);
        } catch (ParseException e) {
            System.out.println("Erro Léxico ou Sintático encontrado:");
            System.out.println(e.getMessage());
        }
    }
}