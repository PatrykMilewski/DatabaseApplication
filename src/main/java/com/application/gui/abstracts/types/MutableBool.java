package com.application.gui.abstracts.types;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class MutableBool {
    
    private boolean value;
    
    public MutableBool(boolean bool) {
        value = bool;
    }
    
    public void setTrue() {
        value = true;
    }
    
    public void setFalse() {
        value = false;
    }
    
    public void setValue(boolean value) {
        this.value = value;
    }
    
    public boolean getValue() {
        return value;
    }
    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
                .append(value)
                .toHashCode();
    }
    
    @Override
    public boolean equals(Object o) {
        if (o == this)
            return true;
        if (!(o instanceof MutableBool))
            return false;
    
        MutableBool mutableBool = (MutableBool) o;
    
        return new EqualsBuilder()
                .append(value, mutableBool.value)
                .isEquals();
    }
}
