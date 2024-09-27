# -*- coding: utf-8 -*-
# @author  : Junkai Cheng
# @time    : 2024/9/27 18:09

class drug:
    # drug信息包括：药物编号、药物drugbank序列号、药物名、类别、化学分子式、描述
    def __init__(self, order, drugbankId='', name='', category='', chemicalFormula='', description=''):
        self.order = order
        self.drugbankId = drugbankId
        self.name = name
        self.category = category
        self.chemicalFormula = chemicalFormula
        self.description = description

class ddi:
    def __init__(self, drugAName, drugBName, ddiType='', weight=0):
        self.drugAName = drugAName
        self.drugBName = drugBName
        self.ddiType = ddiType
        self.weight = weight

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
