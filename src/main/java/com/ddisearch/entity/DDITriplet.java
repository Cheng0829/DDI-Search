package com.ddisearch.entity;

/**
 * @author Junkai Cheng
 * @date 2024/10/5 21:55
 */
public class DDITriplet {
    public String drugA;
    public String drugB;
    public String ddiType;

    public String getDrugA() {
        return drugA;
    }

    public void setDrugA(String drugA) {
        this.drugA = drugA;
    }

    public String getDrugB() {
        return drugB;
    }

    public void setDrugB(String drugB) {
        this.drugB = drugB;
    }

    public String getDdiType() {
        return ddiType;
    }

    public void setDdiType(String ddiType) {
        this.ddiType = ddiType;
    }

    public DDITriplet() {
    }

    public DDITriplet(String drugA, String drugB, String ddiType) {
        this.drugA = drugA;
        this.drugB = drugB;
        this.ddiType = ddiType;
    }

    public String toString() {
        return drugA + " " + drugB + " " + ddiType;
    }
}
