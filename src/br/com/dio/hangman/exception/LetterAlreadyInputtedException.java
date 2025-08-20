
package br.com.dio.hangman.exception;


public class LetterAlreadyInputtedException extends RuntimeException {

    public LetterAlreadyInputtedException(final String msg) {
        super(msg);
    }
}
