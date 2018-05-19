package com.tcs.mmpl.customer.Goibibo.Bus.Pojo;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by hp on 2016-07-26.
 */
public class BusSeatMapChainedComparator implements Comparator<BusSeatMapDetails> {

    private List<Comparator<BusSeatMapDetails>> listComparators;

    @SafeVarargs
    public BusSeatMapChainedComparator(Comparator<BusSeatMapDetails>... comparators) {
        this.listComparators = Arrays.asList(comparators);
    }
    @Override
    public int compare(BusSeatMapDetails b1, BusSeatMapDetails b2) {
        for (Comparator<BusSeatMapDetails> comparator : listComparators) {
            int result = comparator.compare(b1, b2);
            if (result != 0) {
                return result;
            }
        }
        return 0;
    }
}
