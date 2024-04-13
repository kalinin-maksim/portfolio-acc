package edu.kalinin.acc.exception;

public class FlexCubeAlgorithmException extends RuntimeException {
    public FlexCubeAlgorithmException(String number) {
        super(String.format("'K'-part generate error for %s number", number));
    }
}
