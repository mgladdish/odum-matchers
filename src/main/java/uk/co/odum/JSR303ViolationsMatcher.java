package uk.co.odum;

import org.hamcrest.Description;
import org.hamcrest.Factory;
import org.hamcrest.TypeSafeMatcher;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

import static javax.validation.Validation.buildDefaultValidatorFactory;

public class JSR303ViolationsMatcher<T> extends TypeSafeMatcher<T> {

    private Validator validator;
    private Set<ConstraintViolation<T>> violations;
    private String interpolatedMessage;

    public JSR303ViolationsMatcher(String interpolatedMessage) {
        validator = buildDefaultValidatorFactory().getValidator();
        this.interpolatedMessage = interpolatedMessage;
    }

    @Override
    protected boolean matchesSafely(T item) {
        violations = validator.validate(item);
        if (interpolatedMessage != null) {
            return violationsContainsMessage(interpolatedMessage);
        }
        else {
            return violations.size() > 0;
        }
    }

    private boolean violationsContainsMessage(String message) {
        boolean contains = false;
        for (ConstraintViolation v : violations) {
            if (v.getMessage().equals(message)) {
                contains = true;
                break;
            }
        }
        return contains;
    }

    @Override
    public void describeTo(Description description) {
        if (interpolatedMessage != null) {
            description.appendValue(interpolatedMessage);
        }
        else {
            description.appendText("jsr303 validation violations");
        }
    }

    @Override
    protected void describeMismatchSafely(T item, Description mismatchDescription) {
        mismatchDescription.appendText("was ").appendValue(violations);
    }

    @Factory
    public static JSR303ViolationsMatcher hasViolations() {
        return new JSR303ViolationsMatcher(null);
    }

    @Factory
    public static JSR303ViolationsMatcher hasViolation(String interpolatedMessage) {
        return new JSR303ViolationsMatcher(interpolatedMessage);
    }
}