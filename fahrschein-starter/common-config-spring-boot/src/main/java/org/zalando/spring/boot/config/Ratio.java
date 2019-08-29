package org.zalando.spring.boot.config;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public final class Ratio {

    private static final Pattern PATTERN = Pattern.compile("(\\d+)(?:(?:(?: +(?:out )?+of +)|(?: */ *))?(\\d+))?");

    private final int amount;
    private final int total;

    // used by SnakeYAML
    public Ratio(final Integer value) {
        this(value.toString());
    }

    // used by SnakeYAML
    public Ratio(final String value) {
        this(Ratio.valueOf(value));
    }

    private Ratio(final Ratio ratio) {
        this(ratio.amount, ratio.total);
    }

    void applyTo(final BiConsumer<Integer, Integer> consumer) {
        consumer.accept(amount, total);
    }

    static Ratio valueOf(final String value) {
        final Matcher matcher = PATTERN.matcher(value);
        checkArgument(matcher.matches(), "'%s' is not a valid ratio", value);

        final int amount = Integer.parseInt(matcher.group(1));
        final int total = Optional.ofNullable(matcher.group(2)).map(Integer::parseInt).orElse(amount);

        return new Ratio(amount, total);
    }

}
