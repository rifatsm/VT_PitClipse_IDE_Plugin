/*******************************************************************************
 * Copyright 2012-2019 Phil Glover and contributors
 *  
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 *  
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/

package org.pitest.pitclipse.runner.results.summary;

import static org.apache.commons.lang3.builder.EqualsBuilder.reflectionEquals;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import java.util.Optional;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

class Verification {
    private final Optional<SummaryResult> actualResult;

    public Verification(Optional<SummaryResult> actualResult) {
        this.actualResult = actualResult;
    }

    public void thenTheResultsAre(SummaryResult expectedResult) {
        assertThat(actualResult.isPresent(), is(equalTo(true)));
        assertThat(actualResult.get(), is(sameAs(expectedResult)));
    }
    
    public void thenTheResultsAre(SummaryResultListenerTestSugar.SummaryResultWrapper wrapper) {
        thenTheResultsAre(wrapper.getResult());
    }

    private Matcher<SummaryResult> sameAs(final SummaryResult expected) {
        return new TypeSafeDiagnosingMatcher<SummaryResult>() {

            @Override
            public void describeTo(Description description) {
                description.appendValue(expected.toString());
            }

            @Override
            protected boolean matchesSafely(SummaryResult actual, Description mismatchDescription) {
                mismatchDescription.appendValue(actual.toString());
                return reflectionEquals(expected, actual);
            }
        };
    }
}
