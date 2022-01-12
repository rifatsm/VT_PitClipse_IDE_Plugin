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

package org.pitest.pitclipse.runner.model;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Ordering;

import org.pitest.pitclipse.runner.results.DetectionStatus;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static com.google.common.collect.Collections2.transform;
import static org.pitest.pitclipse.runner.results.DetectionStatus.KILLED;
import static org.pitest.pitclipse.runner.results.DetectionStatus.MEMORY_ERROR;
import static org.pitest.pitclipse.runner.results.DetectionStatus.NON_VIABLE;
import static org.pitest.pitclipse.runner.results.DetectionStatus.NOT_STARTED;
import static org.pitest.pitclipse.runner.results.DetectionStatus.NO_COVERAGE;
import static org.pitest.pitclipse.runner.results.DetectionStatus.RUN_ERROR;
import static org.pitest.pitclipse.runner.results.DetectionStatus.STARTED;
import static org.pitest.pitclipse.runner.results.DetectionStatus.SURVIVED;
import static org.pitest.pitclipse.runner.results.DetectionStatus.TIMED_OUT;

public class MutationsModel implements Visitable, Countable {

    private enum StatusComparator implements Comparator<Status> {
        INSTANCE;

        private static final Ordering<DetectionStatus> STATUSES_IN_ORDER = Ordering.explicit(ImmutableList.of(SURVIVED,
                NOT_STARTED, STARTED, KILLED, TIMED_OUT, NON_VIABLE, MEMORY_ERROR, RUN_ERROR, NO_COVERAGE));

        @Override
        public int compare(Status lhs, Status rhs) {
            return STATUSES_IN_ORDER.compare(lhs.getDetectionStatus(), rhs.getDetectionStatus());
        }
    }

    public static final MutationsModel EMPTY_MODEL = make(ImmutableList.<Status>of());

    private final ImmutableList<Status> statuses;

    private MutationsModel(ImmutableList<Status> statuses) {
        this.statuses = ImmutableList.copyOf(
                transform(statuses, input -> input.copyOf().withModel(MutationsModel.this).build())
        );
    }

    public static MutationsModel make(List<Status> statuses) {
        ImmutableList<Status> sortedStatuses = Ordering.from(StatusComparator.INSTANCE).immutableSortedCopy(statuses);
        return new MutationsModel(sortedStatuses);
    }

    public List<Status> getStatuses() {
        return statuses;
    }

    @Override
    public <T> T accept(MutationsModelVisitor<T> visitor) {
        return visitor.visitModel(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        MutationsModel that = (MutationsModel) o;
        return Objects.equals(statuses, that.statuses);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statuses);
    }

    @Override
    public String toString() {
        return "MutationsModel [statuses=" + statuses + "]";
    }

    @Override
    public long count() {
        long sum = 0L;
        for (Status status : statuses) {
            sum += status.count();
        }
        return sum;
    }
}
