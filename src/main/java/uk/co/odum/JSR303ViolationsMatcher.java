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
    private boolean matchWithViolations;

    public JSR303ViolationsMatcher(String interpolatedMessage, boolean matchWithViolations) {
        validator = buildDefaultValidatorFactory().getValidator();
        this.interpolatedMessage = interpolatedMessage;
        this.matchWithViolations = matchWithViolations;
    }

    @Override
    protected boolean matchesSafely(T item) {
        violations = validator.validate(item);
        if (interpolatedMessage != null) {
            return violationsContainsMessage(interpolatedMessage);
        }
        else {
            return matchWithViolations ? violations.size() > 0 : violations.size() == 0;
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
        return new JSR303ViolationsMatcher(null, true);
    }

    @Factory
    public static JSR303ViolationsMatcher hasViolation(String interpolatedMessage) {
        return new JSR303ViolationsMatcher(interpolatedMessage, true);
    }

    @Factory
    public static JSR303ViolationsMatcher hasNoViolations() {
        return new JSR303ViolationsMatcher(null, false);
    }
}