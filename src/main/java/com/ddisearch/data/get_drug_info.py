# -*- coding: utf-8 -*-
# @author  : Junkai Cheng
# @time    : 2024/9/27 18:09

import requests, json, random, time
from bs4 import BeautifulSoup
from rdkit import Chem
from rdkit.Chem import Draw

class drug:
    # drug信息包括：药物编号、药物drugbank序列号、药物名、类别、化学分子式、描述、相关药物
    def __init__(self, orderId, drugbankId='', name='', category='', chemicalFormula='', description='', relatedDrugs=''):
        self.orderId = orderId
        self.drugbankId = drugbankId
        self.name = name
        self.category = category
        self.chemicalFormula = chemicalFormula
        self.description = description
        """
        ********************************************************
        """
        self.relatedDrugs = relatedDrugs

def crawlDrugbank(orderId, drugbankId):
    url = "https://go.drugbank.com/drugs/" + drugbankId
    headers = {
        "User-Agent": "User-Agent': 'Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"
    }

    # 代理IP池
    proxies = {
        "http": "http://127.0.0.1:7890",
        "https": "http://127.0.0.1:7890",
    }
    # proxy = random.choice(proxy_pool)  # 随机选择代理IP

    # # 发送请求获取响应
    response = requests.get(url, headers=headers, proxies=proxies)
    # print(response.text)
    soup = BeautifulSoup(response.content, 'html.parser')
    # soup = BeautifulSoup(hhhh(), 'html.parser')

    # 提取Drug Name
    drugName = soup.find('dt', {'id': 'generic-name'}).find_next_sibling('dd').text.strip()

    # # 提取DrugBank Accession Number
    # drugbankId = soup.find('dt', {'id': 'drugbankId'}).find_next_sibling('dd').text.strip()

    # 提取Background
    description = soup.find('dt', {'id': 'background'}).find_next_sibling('dd').text.strip()

    # 提取Type
    category = soup.find('dt', {'id': 'type'}).find_next_sibling('dd').text.strip()

    # 提取Chemical Formula
    if soup.find('dt', {'id': 'chemical-formula'}):
        chemicalFormula = soup.find('dt', {'id': 'chemical-formula'}).find_next_sibling('dd').text.strip()
    else:
        chemicalFormula = ''

    relatedDrugs = ['DrugA', 'DrugB', 'DrugC']
    relatedDrugs = str(relatedDrugs)

    return drug(orderId=orderId, drugbankId=drugbankId, name=drugName, category=category,
                chemicalFormula=chemicalFormula, description=description, relatedDrugs=relatedDrugs)

def FromSmilesToImage(drugSmiles, drugName):
    # 从SMILES创建分子对象
    # mol = Chem.MolFromSmiles(drugSmiles)
    mol = Chem.MolFromSmiles("CC1=CC2=CC3=C(OC(=O)C=C3C)C(C)=C2O1")
    # 绘制分子
    img = Draw.MolToImage(mol)
    # 保存图像
    img.save("./drugImage/{}.png".format(drugName))

def saveDrugToJson(drugJson, orderId, drugInfo, length):
    drugJson[drugInfo.name] = {
        "orderId": drugInfo.orderId,
        "drugbankId": drugInfo.drugbankId,
        "name": drugInfo.name,
        "category": drugInfo.category,
        "chemicalFormula": drugInfo.chemicalFormula,
        "description": drugInfo.description,
        "relatedDrugs": drugInfo.relatedDrugs
    }
    # 保存药物分子图
    FromSmilesToImage(drugInfo.chemicalFormula, drugInfo.name)

    if orderId == length - 1 or 1:
        with open('drugInfo_1710_crawl.json', 'w', encoding='utf-8') as f:
            f.write(json.dumps(drugJson, ensure_ascii=False))

def addressCrawlDrug():
    # 从0到1709找到每个药物的DrugBank Accession Number，然后调用crawl_drugbank函数获取相关信息
    with open(r'D:\Java\code\DDI-Search\src\main\java\com\ddisearch\data\node2id.json', 'r', encoding='utf-8') as f:
        node2id = json.load(f)

    drugJson = {}
    for drugbankId in node2id:
        drugInfo = crawlDrugbank(node2id[drugbankId], drugbankId)
        saveDrugToJson(drugJson, node2id[drugbankId], drugInfo, len(node2id))

        print("{},{} have beena crawled.\n".format(node2id[drugbankId], drugbankId), end='')
        # time.sleep(3)
        if(node2id[drugbankId]==3):
            break
        # break
    # print(666)

addressCrawlDrug()


