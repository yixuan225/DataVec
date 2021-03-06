/*-
 *  * Copyright 2016 Skymind, Inc.
 *  *
 *  *    Licensed under the Apache License, Version 2.0 (the "License");
 *  *    you may not use this file except in compliance with the License.
 *  *    You may obtain a copy of the License at
 *  *
 *  *        http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  *    Unless required by applicable law or agreed to in writing, software
 *  *    distributed under the License is distributed on an "AS IS" BASIS,
 *  *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  *    See the License for the specific language governing permissions and
 *  *    limitations under the License.
 */

package org.datavec.api.transform.condition.column;

import org.datavec.api.transform.condition.SequenceConditionMode;
import org.datavec.api.writable.Writable;

/**
 * A column condition that simply checks whether a floating point value is infinite
 *
 * @author Alex Black
 */
public class InfiniteColumnCondition extends BaseColumnCondition {

    /**
     * @param columnName Column check for the condition
     */
    public InfiniteColumnCondition(String columnName) {
        this(columnName, DEFAULT_SEQUENCE_CONDITION_MODE);
    }

    /**
     * @param columnName            Column to check for the condition
     * @param sequenceConditionMode Sequence condition mode
     */
    public InfiniteColumnCondition(String columnName, SequenceConditionMode sequenceConditionMode) {
        super(columnName, sequenceConditionMode);
    }

    @Override
    public boolean columnCondition(Writable writable) {
        return Double.isInfinite(writable.toDouble());
    }

    @Override
    public boolean condition(Object input) {
        return Double.isInfinite(((Number) input).doubleValue());
    }

    @Override
    public String toString() {
        return "InfiniteColumnCondition()";
    }

}
