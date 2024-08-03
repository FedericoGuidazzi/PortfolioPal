package models.enums;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public enum DurationIntervalEnum {
    S1("1S"),
    A1("1A"),
    A5("5A"),
    MAX("Max");

    private final String value;

    DurationIntervalEnum(String value) {
        this.value = value;
    }

    public static LocalDate getDateFromNow(DurationIntervalEnum interval){
        return switch (interval.getValue()) {
            case "1S" -> LocalDate.now().minusWeeks(1);
            case "1A" -> LocalDate.now().minusYears(1);
            case "5A" -> LocalDate.now().minusYears(5);
            default -> LocalDate.MIN;
        };
    }

    public static DurationIntervalEnum fromValue(String value) {
        for (DurationIntervalEnum range : DurationIntervalEnum.values()) {
            if (range.getValue().equalsIgnoreCase(value)) {
                return range;
            }
        }
        return DurationIntervalEnum.S1;
    }
}
