package com.tcs.mmpl.customer.Goibibo.Bus.Pojo;

import java.util.Comparator;

/**
 * Created by hp on 2016-07-26.
 */
public class BusSeatMapColumnComparator implements Comparator<BusSeatMapDetails> {
    @Override
    public int compare(BusSeatMapDetails b1, BusSeatMapDetails b2) {
        return Integer.parseInt(b1.getColumnNo()) - Integer.parseInt(b2.getColumnNo());
    }
}
