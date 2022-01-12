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

package org.pitest.pitclipse.runner.results;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

/**
 * <p>A collection of mutations.</p>
 * 
 * <p>This class is serializable so that it can be sent from the PIT
 * application running in a background VM to the Eclipse listeners.</p>
 */
public class Mutations implements Serializable {

    private static final long serialVersionUID = 1L;
    private List<Mutations.Mutation> mutation;

    public List<Mutations.Mutation> getMutation() {
        if (mutation == null) {
            mutation = new ArrayList<>();
        }
        return this.mutation;
    }

    /**
     * <p>A mutation detected by PIT.</p>
     * 
     * <p>This class is serializable so that it can be sent from the PIT
     * application running in a background VM to the Eclipse listeners.</p>
     */
    public static class Mutation implements Serializable {

        private static final long serialVersionUID = 1L;
        protected String sourceFile;
        protected String mutatedClass;
        protected String mutatedMethod;
        protected BigInteger lineNumber;
        protected String mutator;
        protected BigInteger index;
        protected String killingTest;
        protected String description;
        protected Boolean detected;
        protected DetectionStatus status;

        public String getSourceFile() {
            return sourceFile;
        }

        public void setSourceFile(String value) {
            this.sourceFile = value;
        }

        public String getMutatedClass() {
            return mutatedClass;
        }

        public void setMutatedClass(String value) {
            this.mutatedClass = value;
        }

        public String getMutatedMethod() {
            return mutatedMethod;
        }

        public void setMutatedMethod(String value) {
            this.mutatedMethod = value;
        }

        public BigInteger getLineNumber() {
            return lineNumber;
        }

        public void setLineNumber(BigInteger value) {
            this.lineNumber = value;
        }

        public String getMutator() {
            return mutator;
        }

        public void setMutator(String value) {
            this.mutator = value;
        }

        public BigInteger getIndex() {
            return index;
        }

        public void setIndex(BigInteger value) {
            this.index = value;
        }

        public String getKillingTest() {
            return killingTest;
        }

        public void setKillingTest(String value) {
            this.killingTest = value;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String value) {
            this.description = value;
        }

        public Boolean isDetected() {
            return detected;
        }

        public void setDetected(Boolean value) {
            this.detected = value;
        }

        public DetectionStatus getStatus() {
            return status;
        }

        public void setStatus(DetectionStatus value) {
            this.status = value;
        }
    }
}
