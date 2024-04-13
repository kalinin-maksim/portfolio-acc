package edu.kalinin.acc.generator;

import lombok.experimental.UtilityClass;
import edu.kalinin.acc.exception.FlexCubeAlgorithmException;

@UtilityClass
public class AccountKeyGenerator {

    private static final String SET = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final int MODULUS = SET.length() + 1;
    private static final int MODULUS_HALF = MODULUS / 2;

    public static char key(String account) {

        var checkSum = checkSum(account, account.length() - 1, 0);

        var keyIndex = checkSum % MODULUS;

        if (keyIndex >= SET.length())
            throw new FlexCubeAlgorithmException(account);
        return SET.charAt(keyIndex);
    }

    private static int checkSum(String str, int i, int position) {
        if (i < 0) return 0;

        final var positionNew = getPositionNew(position);
        return positionNew * str.charAt(i) + checkSum(str, i - 1, positionNew);
    }

    private static int getPositionNew(int position) {
        if (position <= MODULUS_HALF) {
            return position + 1;
        } else if (position > MODULUS_HALF + 1) {
            return MODULUS_HALF;
        } else {
            return position - 1;
        }
    }

}
