package com.ddisearch.entity;

/**
 * @author Junkai Cheng
 * @date 2024/9/27 18:10
 */
public class Drug {

    // 药物编号
    private String orderId;

    // 药物drugbank序列号
    private String drugbankId;

    // 药物名称
    private String name;

    // 药物类别
    private String category;

    // 药物分子式
    private String chemicalFormula;

    // 药物SMILES序列
    private String smiles;

    // 药物描述
    private String description;

    // 相关的药物
    private String relatedDrugs;

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
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

    public String getSmiles() {
        return smiles;
    }

    public void setSmiles(String smiles) {
        this.smiles = smiles;
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

    public Drug(String orderId, String drugbankId, String name, String category, String chemicalFormula, String smiles, String description, String relatedDrugs) {
        this.orderId = orderId;
        this.drugbankId = drugbankId;
        this.name = name;
        this.category = category;
        this.chemicalFormula = chemicalFormula;
        this.smiles = smiles;
        this.description = description;
        this.relatedDrugs = relatedDrugs;
    }

    @Override
    public String toString() {
        return "Drug{<br>" +
                "orderId: " + orderId + ",<br>" +
                "drugbankId: " + drugbankId +  ",<br>" +
                "name: " + name  + ",<br>" +
                "category: " + category + ",<br>" +
                "chemicalFormula: " + chemicalFormula + ",<br>" +
                "smiles: " + smiles + ",<br>" +
                "description: " + description + ",<br>" +
                "relatedDrugs: " + relatedDrugs + ",<br>" +
                "}";
    }
}
