package ace.fingerprinting.matching;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;

public class Matcher {
    static boolean matchIfEqual(final Optional<String> left, final Optional<String> right) {
        if (left.isPresent() == false || right.isPresent() == false) {
            return false;
        }
        return left.get().equalsIgnoreCase(right.get());
    }

    static boolean matchIntValues(final Optional<String> left, final Optional<String> right) {
        if (left.isPresent() == false || right.isPresent() == false) {
            return false;
        }

        return matchIntValues(left.get(), right.get());
    }

    static boolean matchIntValues(final String left, final String right) {
        try {
            int leftValue = Float.valueOf(left).intValue();
            int rightValue = Float.valueOf(right).intValue();
            return leftValue == rightValue;
        } catch (Throwable t) {
            t.printStackTrace();
            return false;
        }
    }

    /**
     *
     * @param leftMap Comes from Request from iOS
     * @param rightMap Comes from DB.
     * @return true if they match.
     */
    public static boolean match(Map<String, String> leftMap, Map<String, String> rightMap) {
        return matchers.stream().reduce(
                true,
                (lastResult, nextMatcher) -> {
                    Optional<String> leftValue = Optional.ofNullable(leftMap.get(nextMatcher.leftKey));
                    Optional<String> rightValue = Optional.ofNullable(rightMap.get(nextMatcher.rightKey));
                    boolean currentResult = nextMatcher.matcher.apply(leftValue, rightValue);
                    return lastResult && currentResult;
                },
                (lastResult, currentResult) -> lastResult && currentResult);
    }

    // Keep adding matching logic here.
    private static void initializeMatchers() {
        matchers = new ArrayList<>();
        matchers.add(new OneMatcher("screenScale", "scale", Matcher::matchIntValues));
        matchers.add(new OneMatcher("screenWidth", "screenWidth", Matcher::matchIntValues));
        matchers.add(new OneMatcher("screenHeight", "screenHeight", Matcher::matchIntValues));
    }

    static class OneMatcher {
        public OneMatcher(String leftKey, String rightKey, BiFunction<Optional<String>, Optional<String>, Boolean> matcher) {
            this.leftKey = leftKey;
            this.rightKey = rightKey;
            this.matcher = matcher;
        }

        String leftKey;
        String rightKey;
        BiFunction<Optional<String>, Optional<String>, Boolean> matcher;
    }

    static {
        initializeMatchers();
    }

    static List<OneMatcher> matchers;
}
