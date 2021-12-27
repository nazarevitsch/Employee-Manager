package com.bida.employer.manager.domain.enums;

public enum OrganizationType {
    SMALL(10), MIDDLE(25), LARGE(50);

    private int size;

    private OrganizationType(int size){
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
