package com.ddisearch.entity;

/**
 * @author Junkai Cheng
 * @date 2024/10/2 0:18
 */
public class batchDrugResult {

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

    // 药效学
    private String pharmacodynamics;

    // 作用机制
    private String actionMechanism;

    // 蛋白质结合
    private String proteinBinding;

    // 代谢
    private String metabolism;


    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String id) {
        if (id.startsWith("DB")) {
            String numberPart = id.substring(2);
            this.orderId = String.valueOf(Long.parseLong(numberPart));
        } else {
            this.orderId = id;
        }
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

    public String getPharmacodynamics() {
        return pharmacodynamics;
    }

    public void setPharmacodynamics(String pharmacodynamics) {
        this.pharmacodynamics = pharmacodynamics;
    }

    public String getActionMechanism() {
        return actionMechanism;
    }

    public void setActionMechanism(String actionMechanism) {
        this.actionMechanism = actionMechanism;
    }

    public String getProteinBinding() {
        return proteinBinding;
    }

    public void setProteinBinding(String proteinBinding) {
        this.proteinBinding = proteinBinding;
    }

    public String getMetabolism() {
        return metabolism;
    }

    public void setMetabolism(String metabolism) {
        this.metabolism = metabolism;
    }
}
