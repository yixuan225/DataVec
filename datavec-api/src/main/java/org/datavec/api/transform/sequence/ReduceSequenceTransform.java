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

package org.datavec.api.transform.sequence;

import lombok.EqualsAndHashCode;
import org.datavec.api.transform.Transform;
import org.datavec.api.transform.metadata.ColumnMetaData;
import org.datavec.api.transform.ops.IAggregableReduceOp;
import org.datavec.api.transform.reduce.IAssociativeReducer;
import org.datavec.api.transform.schema.Schema;
import org.datavec.api.transform.schema.SequenceSchema;
import org.datavec.api.writable.Writable;
import org.nd4j.shade.jackson.annotation.JsonIgnoreProperties;
import org.nd4j.shade.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;

/**
 * Reduce The values in each column in the sequence to a single value using a reducer.
 * Note: after applying ReduceSequenceTransform, you have sequences of length 1. Consequently, this transform
 * is often used in conjunction with {@link ConvertFromSequence}
 *
 * @author Alex Black
 */
@JsonIgnoreProperties({"inputSchema"})
@EqualsAndHashCode(exclude = {"inputSchema"})
public class ReduceSequenceTransform implements Transform {

    private IAssociativeReducer reducer;
    private Schema inputSchema;

    public ReduceSequenceTransform(@JsonProperty("reducer") IAssociativeReducer reducer) {
        this.reducer = reducer;
    }


    @Override
    public Schema transform(Schema inputSchema) {
        if (inputSchema != null && !(inputSchema instanceof SequenceSchema)) {
            throw new IllegalArgumentException("Invalid input: input schema must be a SequenceSchema");
        }

        //Approach here: The reducer gives us a schema for one time step -> simply convert this to a sequence schema...
        Schema oneStepSchema = reducer.transform(inputSchema);
        List<ColumnMetaData> meta = oneStepSchema.getColumnMetaData();

        return new SequenceSchema(meta);
    }

    @Override
    public void setInputSchema(Schema inputSchema) {
        this.inputSchema = inputSchema;
        reducer.setInputSchema(inputSchema);
    }

    @Override
    public Schema getInputSchema() {
        return inputSchema;
    }

    @Override
    public List<Writable> map(List<Writable> writables) {
        throw new UnsupportedOperationException("ReduceSequenceTransform can only be applied on sequences");
    }

    @Override
    public List<List<Writable>> mapSequence(List<List<Writable>> sequence) {
        IAggregableReduceOp<List<Writable>, List<Writable>> accu = reducer.aggregableReducer();
        for (List<Writable> l : sequence)
            accu.accept(l);
        return Collections.singletonList(accu.get());
    }

    /**
     * Transform an object
     * in to another object
     *
     * @param input the record to transform
     * @return the transformed writable
     */
    @Override
    public Object map(Object input) {
        throw new UnsupportedOperationException("ReduceSequenceTransform can only be applied on sequences");
    }

    /**
     * Transform a sequence
     *
     * @param sequence
     */
    @Override
    public Object mapSequence(Object sequence) {
        throw new UnsupportedOperationException("Needs to be implemented");
    }

    @Override
    public String toString() {
        return "ReduceSequenceTransform(reducer=" + reducer + ")";
    }

    /**
     * The output column name
     * after the operation has been applied
     *
     * @return the output column name
     */
    @Override
    public String outputColumnName() {
        return outputColumnNames()[0];
    }

    /**
     * The output column names
     * This will often be the same as the input
     *
     * @return the output column names
     */
    @Override
    public String[] outputColumnNames() {
        return columnNames();
    }

    /**
     * Returns column names
     * this op is meant to run on
     *
     * @return
     */
    @Override
    public String[] columnNames() {
        return getInputSchema().getColumnNames().toArray(new String[getInputSchema().numColumns()]);
    }

    /**
     * Returns a singular column name
     * this op is meant to run on
     *
     * @return
     */
    @Override
    public String columnName() {
        return columnNames()[0];
    }
}
