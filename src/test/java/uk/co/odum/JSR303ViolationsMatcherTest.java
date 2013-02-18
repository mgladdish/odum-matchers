/*
 * Copyright 2013 Odum Software Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.co.odum;

import org.junit.Before;
import org.junit.Test;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotNull;

import static org.hamcrest.MatcherAssert.assertThat;
import static uk.co.odum.JSR303ViolationsMatcher.hasNoViolations;
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
    public void noViolationsShouldFailHasViolationsTest() {
        annotated.name = "A valid name";
        assertThat(annotated, hasViolations());
    }

    @Test
    public void nullNameShouldRaiseViolation() {
        assertThat(annotated, hasViolations());
    }

    @Test(expected = AssertionError.class)
    public void unexpectedInterpolatedMessageShouldFailHasViolationsTest() {
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

    @Test
    public void noViolationsShouldPassHasNoViolationsTest() {
        annotated.name = "A valid name";
        assertThat(annotated, hasNoViolations());
    }

    @Test(expected = AssertionError.class)
    public void violationsShouldFailHasNoViolationsTest() {
        assertThat(annotated, hasNoViolations());
    }

}
