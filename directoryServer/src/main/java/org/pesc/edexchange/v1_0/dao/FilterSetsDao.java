package org.pesc.edexchange.v1_0.dao;

import org.pesc.cds.datatables.FilterSet;

import java.util.List;

/**
 * Created by rgehbauer on 9/16/15.
 */
public interface FilterSetsDao extends DBDataSourceDao<FilterSet> {
    List<FilterSet> byUserId(Integer userId);

    List<FilterSet> byTable(String table);

    List<FilterSet> byUserIdAndTable();
}
