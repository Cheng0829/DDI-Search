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
        if (id.startsWith("DB")){
            String numberPart = id.substring(2);
            this.orderId = String.valueOf(Long.parseLong(numberPart));
        }
        else{
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

    public Drug(String orderId, String drugbankId, String name, String category, String chemicalFormula, String smiles, String description, String relatedDrugs, String pharmacodynamics, String actionMechanism, String proteinBinding, String metabolism) {
        this.orderId = orderId;
        this.drugbankId = drugbankId;
        this.name = name;
        this.category = category;
        this.chemicalFormula = chemicalFormula;
        this.smiles = smiles;
        this.description = description;
        this.relatedDrugs = relatedDrugs;
        this.pharmacodynamics = pharmacodynamics;
        this.actionMechanism = actionMechanism;
        this.proteinBinding = proteinBinding;
        this.metabolism = metabolism;
    }
    public Drug(){
        this.orderId = "";
        this.drugbankId = "";
        this.name = "";
        this.category = "";
        this.chemicalFormula = "";
        this.smiles = "";
        this.description = "";
        this.relatedDrugs = "";
        this.pharmacodynamics = "";
        this.actionMechanism = "";
        this.proteinBinding = "";
        this.metabolism = "";
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
                "pharmacodynamics: " + pharmacodynamics + ",<br>" +
                "actionMechanism: " + actionMechanism + ",<br>" +
                "proteinBinding: " + proteinBinding + ",<br>" +
                "metabolism: " + metabolism + "<br>" +
                "}";
    }
}
