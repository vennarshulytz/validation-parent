package io.github.vennarshulytz.validation.constant;

/**
 * 消息常量
 *
 * @author vennarshulytz
 * @since 1.0.0
 */
public final class MessageConstants {

    private MessageConstants() {
    }

    public static final String Null = "jakarta.validation.constraints.Null.message";
    public static final String NotNull = "jakarta.validation.constraints.NotNull.message";
    public static final String NotBlank = "jakarta.validation.constraints.NotBlank.message";
    public static final String NotEmpty = "jakarta.validation.constraints.NotEmpty.message";

    public static final String DecimalMax = "jakarta.validation.constraints.DecimalMax.message";
    public static final String DecimalMin = "jakarta.validation.constraints.DecimalMin.message";
    public static final String Digits = "jakarta.validation.constraints.Digits.message";
    public static final String Max = "jakarta.validation.constraints.Max.message";
    public static final String Min = "jakarta.validation.constraints.Min.message";
    public static final String Positive = "jakarta.validation.constraints.Positive.message";
    public static final String PositiveOrZero = "jakarta.validation.constraints.PositiveOrZero.message";
    public static final String Negative = "jakarta.validation.constraints.Negative.message";
    public static final String NegativeOrZero = "jakarta.validation.constraints.NegativeOrZero.message";

    public static final String AssertTrue = "jakarta.validation.constraints.AssertTrue.message";
    public static final String AssertFalse = "jakarta.validation.constraints.AssertFalse.message";

    public static final String Size = "jakarta.validation.constraints.Size.message";

    public static final String Future = "jakarta.validation.constraints.Future.message";
    public static final String FutureOrPresent = "jakarta.validation.constraints.FutureOrPresent.message";
    public static final String Past = "jakarta.validation.constraints.Past.message";
    public static final String PastOrPresent = "jakarta.validation.constraints.PastOrPresent.message";

    public static final String Email = "jakarta.validation.constraints.Email.message";
    public static final String Pattern = "jakarta.validation.constraints.Pattern.message";
}