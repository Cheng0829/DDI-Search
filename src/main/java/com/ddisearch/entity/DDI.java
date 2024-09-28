package com.ddisearch.entity;

/**
 * @author Junkai Cheng
 * @date 2024/9/28 12:04
 */
public class DDI {

    // 药物A名称
    private String drugAName;

    // 药物B名称
    private String drugBName;

    // DDI类型
    private String ddiType;

    // DDI描述
    private String description;

    // 模型置信度
    private float confidence;

    public String getDrugAName() {
        return drugAName;
    }

    public void setDrugAName(String drugAName) {
        this.drugAName = drugAName;
    }

    public String getDrugBName() {
        return drugBName;
    }

    public void setDrugBName(String drugBName) {
        this.drugBName = drugBName;
    }

    public String getDdiType() {
        return ddiType;
    }

    public void setDdiType(String ddiType) {
        this.ddiType = ddiType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getConfidence() {
        return confidence;
    }

    public void setConfidence(float confidence) {
        this.confidence = confidence;
    }

    public DDI(String drugAName, String drugBName, String ddiType, String description, float confidence) {
        this.drugAName = drugAName;
        this.drugBName = drugBName;
        this.ddiType = ddiType;
        this.description = description;
        this.confidence = confidence;
    }

    @Override
    public String toString() {
        return "DDI{<br>" +
                "drugAName: " + drugAName + ",<br>" +
                "drugBName: " + drugBName + ",<br>" +
                "ddiType: " + ddiType + ",<br>" +
                "description: " + description + ",<br>" +
                "confidence: " + confidence + "<br>" +
                "}";
    }
}
