package org.datavec.spark.transform.join;

import org.datavec.api.transform.join.Join;
import org.datavec.api.writable.Writable;
import org.datavec.spark.functions.FlatMapFunctionAdapter;

import java.util.Collections;
import java.util.List;

/**
 * Doing two things here:
 * (a) filter out any unnecessary values, and
 * (b) extract the List<Writable> values from the JoinedValue
 *
 * @author Alex Black
 */
public class FilterAndFlattenJoinedValuesAdapter implements FlatMapFunctionAdapter<JoinedValue, List<Writable>> {

    private final Join.JoinType joinType;

    public FilterAndFlattenJoinedValuesAdapter(Join.JoinType joinType) {
        this.joinType = joinType;
    }

    @Override
    public Iterable<List<Writable>> call(JoinedValue joinedValue) throws Exception {
        boolean keep;
        switch (joinType) {
            case Inner:
                //Only keep joined values where we have both left and right
                keep = joinedValue.isHaveLeft() && joinedValue.isHaveRight();
                break;
            case LeftOuter:
                //Keep all values where left is not missing/null
                keep = joinedValue.isHaveLeft();
                break;
            case RightOuter:
                //Keep all values where right is not missing/null
                keep = joinedValue.isHaveRight();
                break;
            case FullOuter:
                //Keep all values
                keep = true;
                break;
            default:
                throw new RuntimeException("Unknown/not implemented join type: " + joinType);
        }

        if (keep) {
            return Collections.singletonList(joinedValue.getValues());
        } else {
            return Collections.emptyList();
        }
    }
}
