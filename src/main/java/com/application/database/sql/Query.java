package com.application.database.sql;


import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class Query {
    
    private static final int LIMITRANGE = 100;
    
    private String sqlQuery;
    private int limitCounter;
    private boolean queryIsSelect;
    
    public Query(String sqlQuery) {
        this.sqlQuery = sqlQuery;
        limitCounter = 0;
        queryIsSelect = StringUtils.containsIgnoreCase(sqlQuery, "select");
    }
    
    public String getLimitedQuery(int limit) {
        if (!queryIsSelect)
            return null;
        
        StringBuilder stringBuilder = new StringBuilder(sqlQuery);
        
        if (sqlQuery.contains(";"))
            stringBuilder.insert(sqlQuery.indexOf(";"), " limit " + limit);
        
        else
            stringBuilder.append(" limit ").append(limit);

        return stringBuilder.toString();
    }
    
    public String getNextPackQuery() {
        if (!queryIsSelect)
            return null;
    
        StringBuilder stringBuilder = new StringBuilder(sqlQuery);
        limitCounter += LIMITRANGE;
        
        if (sqlQuery.contains(";"))
            stringBuilder.insert(sqlQuery.indexOf(";"), " limit " + limitCounter);
        
        else
            stringBuilder.append(" limit ").append(limitCounter);
        
        return stringBuilder.toString();
    }
    
    public String getRawQuery() {
        return sqlQuery;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(sqlQuery)
                .append(limitCounter)
                .toHashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof Query))
            return false;
        
        Query query = (Query) o;
        
        return new EqualsBuilder()
                .append(sqlQuery, query.sqlQuery)
                .append(limitCounter, query.limitCounter)
                .append(queryIsSelect, query.queryIsSelect)
                .isEquals();
    }
}
