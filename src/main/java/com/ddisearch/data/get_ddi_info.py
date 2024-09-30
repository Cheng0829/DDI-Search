# -*- coding: utf-8 -*-
# @author  : Junkai Cheng
# @time    : 2024/9/27 18:09
import json, csv, requests, os, time, random
class drug:
    # drug信息包括：药物编号、药物drugbank序列号、药物名、类别、化学分子式、描述、相关药物
    def __init__(self, orderId, drugbankId='', name='', category='', chemicalFormula='', smiles='', description='', relatedDrugs=''):
        self.orderId = orderId
        self.drugbankId = drugbankId
        self.name = name
        self.category = category
        self.chemicalFormula = chemicalFormula
        self.smiles = smiles
        self.description = description
        self.relatedDrugs = relatedDrugs

class ddi:
    def __init__(self, drugA, drugB, ddiType='', description='', confidence=0):
        self.drugA = drugA
        self.drugB = drugB
        self.ddiType = ddiType
        self.description = description
        self.confidence = confidence

def write_ddi():
    with open('ddi_name.txt', mode='r', newline='', encoding='utf-8') as f:
        ddis = f.readlines()
    with open('DDI-DescriptionTemplate.json', mode='r', newline='', encoding='utf-8') as f:
        type_description = json.load(f)
    with open('./ddi.csv', mode='w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['drugA', 'drugB', 'ddiType', 'description', 'confidence'])
    old_time = time.time()
    for i in range(1, len(ddis)):
        ddi = ddis[i].strip().split('\t')
        drugA = ddi[0]
        drugB = ddi[1]
        ddi_type = ddi[2]
        description = type_description[ddi_type]
        with open('./ddi.csv', mode='a', newline='', encoding='utf-8') as f:
            writer = csv.writer(f)
            writer.writerow([drugA, drugB, ddi_type, description, round(random.random(), 2)])
        if(i % 1000 == 0):
            print("i={}, time={}s.".format(i, round(time.time() - old_time, 2)))
write_ddi()
