package com.ddisearch.controller;

public class DrugInfo {

    // 药物编号
    private int order;

    // 药物drugbank序列号
    private String drugbankId;

    // 药物名称
    private String name;

    // 药物类别
    private String category;

    // 药物分子式
    private String formula;

    // 药物描述
    private String description;

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
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

    public void setFormula(String formula) {
        this.formula = formula;
    }

    public String getFormula() {
        return formula;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
