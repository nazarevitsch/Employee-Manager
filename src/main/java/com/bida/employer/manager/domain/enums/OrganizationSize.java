package com.bida.employer.manager.domain.enums;

public enum OrganizationSize {
    SMALL(10), MIDDLE(25), LARGE(50);

    private int size;

    private OrganizationSize(int size){
        this.size = size;
    }

    public int getSize() {
        return size;
    }
}
