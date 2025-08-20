package br.com.dio.hangman.model;

import br.com.dio.hangman.exception.GameIsFinishedException;
import static br.com.dio.hangman.model.HangmanGameStatus.LOSE;
import static br.com.dio.hangman.model.HangmanGameStatus.PENDING;
import static br.com.dio.hangman.model.HangmanGameStatus.WIN;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class HangmanGame {

    private final static int HANGMAN_INITIAL_LINE_LENGTH = 9;
    private final static int HANGMAN_INITIAL_LINE_LENGTH_WITH_LINE_SEPARATOR = 10;

    private final int lineSize;
    private final int hangmanInitialSize;
    private final Deque<HangmanChar> hangmanPaths;
    private final List<HangmanChar> characters;
    private final List<Character> failAttempts = new ArrayList<>();

    private String hangman;
    private HangmanGameStatus hangmanGameStatus;

    public HangmanGame(final List<HangmanChar> characters) {
        String whiteSpaces = " ".repeat(characters.size());
        String characterSpace = "-".repeat(characters.size());
        this.lineSize = HANGMAN_INITIAL_LINE_LENGTH_WITH_LINE_SEPARATOR + whiteSpaces.length();
        this.hangmanGameStatus = PENDING;
        this.hangmanPaths = buildHangmanPathsPositions();
        buildHangmanDesign(whiteSpaces, characterSpace);
        this.characters = setCharacterSpacesPositionInGame(characters, whiteSpaces.length());
        this.hangmanInitialSize = hangman.length();
    }

    public void inputCharacter(final char character) {
        if (this.hangmanGameStatus != PENDING) {
            String message = this.hangmanGameStatus == WIN
                    ? "Parabéns você ganhou!"
                    : "Você perde, tente novamente";
            throw new GameIsFinishedException(message);
        }
        List<HangmanChar> found = this.characters.stream().filter(c -> c.getCharacter() == character).collect(Collectors.toList());
        if (found.isEmpty()) {
            failAttempts.add(character);
            if (failAttempts.size() >= 6) {
                this.hangmanGameStatus = LOSE;
            }
            if (!hangmanPaths.isEmpty()) {
                rebuildHangman(hangmanPaths.removeFirst());
            }
            return;
        }
        // Revela os caracteres corretos
        this.characters.forEach(c -> {
            if (c.getCharacter() == found.get(0).getCharacter()) {
                c.enableVisibility();
            }
        });
        // Atualiza o desenho com os acertos
        rebuildHangman(found.toArray(HangmanChar[]::new));
        // Verifica vitória
        boolean allLettersRevealed = this.characters.stream()
                .filter(c -> Character.isLetter(c.getCharacter()))
                .allMatch(HangmanChar::isVisible);

        if (allLettersRevealed) {
            this.hangmanGameStatus = WIN;
        }
    }

    @Override
    public String toString() {
        return this.hangman;
    }

    public boolean isFinished() {
        return this.hangmanGameStatus != PENDING;
    }

    public HangmanGameStatus getStatus() {
        return this.hangmanGameStatus;
    }

    private Deque<HangmanChar> buildHangmanPathsPositions() {
        final var HEAD_LINE = 3;
        final var BODY_LINE = 4;
        final var LEGS_LINE = 5;
        return new LinkedList<>(
                List.of(
                        new HangmanChar('0', this.lineSize * HEAD_LINE + 6),
                        new HangmanChar('|', this.lineSize * BODY_LINE + 6),
                        new HangmanChar('/', this.lineSize * BODY_LINE + 5),
                        new HangmanChar('\\', this.lineSize * BODY_LINE + 7),
                        new HangmanChar('/', this.lineSize * LEGS_LINE + 5),
                        new HangmanChar('\\', this.lineSize * LEGS_LINE + 7)
                )
        );

    }

    private List<HangmanChar> setCharacterSpacesPositionInGame(final List<HangmanChar> characters, final int whiteSpacesAmount) {
        final var LINE_LETTER = 6;
        for (int i = 0; i < characters.size(); i++) {
            characters.get(i).setPosition(this.lineSize * LINE_LETTER + HANGMAN_INITIAL_LINE_LENGTH + i);
        }
        return characters;
    }

    private void rebuildHangman(final HangmanChar... hangmanChars) {
        var hangmanBuilder = new StringBuilder(this.hangman);
        Stream.of(hangmanChars).forEach(h -> hangmanBuilder.setCharAt(h.getPosition(), h.getCharacter()));
        String failMessage = this.failAttempts.isEmpty() ? "" : "Tentativas" + this.failAttempts;
        this.hangman = hangmanBuilder.substring(0, hangmanInitialSize) + failMessage;
    }

    private void buildHangmanDesign(final String whiteSpaces, final String characterSpaces) {
        this.hangman = "  ----- " + whiteSpaces + System.lineSeparator()
                + "  |   | " + whiteSpaces + System.lineSeparator()
                + "  |   | " + whiteSpaces + System.lineSeparator()
                + "  |     " + whiteSpaces + System.lineSeparator()
                + "  |     " + whiteSpaces + System.lineSeparator()
                + "  |     " + whiteSpaces + System.lineSeparator()
                + "  |     " + whiteSpaces + System.lineSeparator()
                + "========" + characterSpaces + System.lineSeparator();
    }
}
