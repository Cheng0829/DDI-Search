package com.ddisearch.entity;

import java.util.ArrayList;

/**
 * @author Junkai Cheng
 * @date 2024/9/27 18:10
 */
public class DrugInfo {

    // 药物编号
    private int orderId;

    // 药物drugbank序列号
    private String drugbankId;

    // 药物名称
    private String name;

    // 药物类别
    private String category;

    // 药物分子式
    private String chemicalFormula;

    // 药物描述
    private String description;

    // 相关的药物
    private String relatedDrugs;

    public int getOrder() {
        return orderId;
    }

    public void setOrder(int orderId) {
        this.orderId = orderId;
    }

    public String getDrugbankId() {
        return drugbankId;
    }

    public void setDrugbankId(String drugbankId) {
        this.drugbankId = drugbankId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setChemicalFormula(String chemicalFormula) {
        this.chemicalFormula = chemicalFormula;
    }

    public String getChemicalFormula() {
        return chemicalFormula;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRelatedDrugs() {
        return relatedDrugs;
    }

    public void setRelatedDrugs(String relatedDrugs) {
        this.relatedDrugs = relatedDrugs;
    }

    public DrugInfo(int orderId, String drugbankId, String name, String category, String chemicalFormula, String description, String relatedDrugs) {
        this.orderId = orderId;
        this.drugbankId = drugbankId;
        this.name = name;
        this.category = category;
        this.chemicalFormula = chemicalFormula;
        this.description = description;
        this.relatedDrugs = relatedDrugs;
    }
}
