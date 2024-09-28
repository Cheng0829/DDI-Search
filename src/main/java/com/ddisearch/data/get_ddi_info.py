# -*- coding: utf-8 -*-
# @author  : Junkai Cheng
# @time    : 2024/9/27 18:09
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

# 读取txt文本中ddi数据
def a():
    # 调用两个药物各自的drug_info
    # 爬取drugbank的ddi数据
    # 保存
    pass

# 爬取drugbank的ddi数据
def c():
    pass

# 保存
def d():
    pass
