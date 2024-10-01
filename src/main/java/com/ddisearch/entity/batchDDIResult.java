package com.ddisearch.entity;

/**
 * @author Junkai Cheng
 * @date 2024/10/2 0:18
 */
public class batchDDIResult {
    private String drugAName;
    private String drugBName;
    private String ddiDescription;

    public String getDdiDescription() {
        return ddiDescription;
    }

    public void setDdiDescription(String ddiDescription) {
        this.ddiDescription = ddiDescription;
    }

    public String getDrugBName() {
        return drugBName;
    }

    public void setDrugBName(String drugBName) {
        this.drugBName = drugBName;
    }

    public String getDrugAName() {
        return drugAName;
    }

    public void setDrugAName(String drugAName) {
        this.drugAName = drugAName;
    }
// Getters and Setters
}
