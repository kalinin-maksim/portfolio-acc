package edu.kalinin.acc.helper.mapstruct;

import lombok.experimental.UtilityClass;
import edu.kalinin.acc.helper.ParseHelper;




import java.time.LocalDate;

@UtilityClass
public class Parser {

    public static LocalDate toLocalDate(String str) {
        return ParseHelper.toLocalDate(str);
    }
}