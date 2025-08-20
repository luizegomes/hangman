package br.com.dio.hangman;

import br.com.dio.hangman.exception.GameIsFinishedException;
import br.com.dio.hangman.model.HangmanChar;
import br.com.dio.hangman.model.HangmanGame;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class Main {

    public static void main(String... args) {
        if (args.length == 0) {
            System.out.println("Você precisa informar a palavra como argumento!");
            return;
        }
        
        // Pega a palavra inteira passada como argumento
        String word = args[0].toLowerCase();
        
        // Transforma a string em lista de HangmanChar
        List<HangmanChar> characters = word.chars()
                .mapToObj(c -> new HangmanChar((char) c))
                .toList();
        
        HangmanGame game = new HangmanGame(characters);
        Scanner scanner = new Scanner(System.in);
        
        // Conjunto para guardar letras já tentadas
        Set<Character> usedLetters = new HashSet<>();

        System.out.println("Bem-vindo ao jogo da forca!");
        System.out.println("A palavra tem " + characters.size() + " letras.");
        System.out.println(game);

        while (!game.isFinished()) {
            System.out.print("Digite uma letra: ");
            String input = scanner.nextLine();

            if (input.isEmpty()) {
                System.out.println("Você precisa digitar uma letra.");
                continue;
            }

            char guess = Character.toLowerCase(input.charAt(0));
            
            // Verifica se já foi usada
            if (usedLetters.contains(guess)) {
                System.out.println("Você já tentou a letra '" + guess + "'. Tente outra.");
                continue;
            }
            usedLetters.add(guess);
            
            try {
                game.inputCharacter(guess);
            } catch (GameIsFinishedException e) {
                System.out.println(e.getMessage());
                break;
            }

            System.out.println(game);
            System.out.print("Palavra: ");
            characters.forEach(c -> System.out.print(c.isVisible() ? c.getCharacter() + " " : "_ "));
            System.out.println();
            System.out.println("Status: " + game.getStatus());
        }

        System.out.println("Fim de jogo!");
    }
}
