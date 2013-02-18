package uk.co.odum;

import org.junit.Before;
import org.junit.Test;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.odum.JSR303ViolationsMatcher.hasViolation;
import static uk.co.odum.JSR303ViolationsMatcher.hasViolations;

public class JSR303ViolationsMatcherTest {

    private class JSR303AnnotatedBean {
        @NotNull(message = "custom null message")
        public String name;
        @Max(value = 4, message = "maximum number message")
        public int number;

        public JSR303AnnotatedBean() {
            this.name = null;
            this.number = 2;
        }
    }

    private JSR303AnnotatedBean annotated;

    @Before
    public void setUp() {
        annotated = new JSR303AnnotatedBean();
    }

    @Test(expected = AssertionError.class)
    public void noViolationsShouldFailTest() {
        annotated.name = "A valid name";
        assertThat(annotated, hasViolations());
    }

    @Test
    public void nullNameShouldRaiseViolation() {
        assertThat(annotated, hasViolations());
    }

    @Test(expected = AssertionError.class)
    public void unexpectedInterpolatedMessageShouldFailTest() {
        assertThat(annotated, hasViolation("there is no violation with this message"));
    }

    @Test
    public void expectedViolationMessageMustMatchInterpolatedMessage() {
        assertThat(annotated, hasViolation("custom null message"));
    }

    @Test
    public void canMatchOnAnyInterpolatedMessageWhenMultipleViolations() {
        annotated.number = 30;
        assertThat(annotated, hasViolation("custom null message"));
        assertThat(annotated, hasViolation("maximum number message"));
    }


}
