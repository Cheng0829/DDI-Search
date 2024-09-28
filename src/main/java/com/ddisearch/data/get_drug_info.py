# -*- coding: utf-8 -*-
# @author  : Junkai Cheng
# @time    : 2024/9/27 18:09

import requests, json, random, time, csv
from bs4 import BeautifulSoup
from rdkit import Chem
from rdkit.Chem import Draw

class drugInfo:
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
    drugName = soup.find('dt', {'id': 'generic-name'}).find_next_sibling('dd').text.strip().replace("\n", " ")

    # # 提取DrugBank Accession Number
    # drugbankId = soup.find('dt', {'id': 'drugbankId'}).find_next_sibling('dd').text.strip().replace("\n", " ")

    # 提取Background
    description = soup.find('dt', {'id': 'background'}).find_next_sibling('dd').text.strip().replace("\n", " ")

    # 提取Type
    category = soup.find('dt', {'id': 'type'}).find_next_sibling('dd').text.strip().replace("\n", " ")

    # 提取Chemical Formula
    if soup.find('dt', {'id': 'chemical-formula'}):
        chemicalFormula = soup.find('dt', {'id': 'chemical-formula'}).find_next_sibling('dd').text.strip().replace("\n", " ")
    else:
        chemicalFormula = ''

    smiles = ''
    if soup.find('dt', {'id': 'smiles'}):
        smiles = soup.find('dt', {'id': 'smiles'}).find_next_sibling('dd').text.strip().replace("\n", " ")
    else:
        smiles= ''

    relatedDrugs = ['DrugA', 'DrugB', 'DrugC']
    relatedDrugs = str(relatedDrugs)

    return [orderId, drugbankId, drugName, category, chemicalFormula, smiles, description, relatedDrugs]
    # return drugInfo(orderId=orderId, drugbankId=drugbankId, name=drugName, category=category, chemicalFormula=chemicalFormula, smiles=smiles, description=description, relatedDrugs=relatedDrugs)

def FromSmilesToImage(drugSmiles, drugName):
    # 从SMILES创建分子对象
    # mol = Chem.MolFromSmiles(drugSmiles)
    mol = Chem.MolFromSmiles("CC1=CC2=CC3=C(OC(=O)C=C3C)C(C)=C2O1")
    # 绘制分子
    img = Draw.MolToImage(mol)
    # 保存图像
    img.save("./drugImage/{}.png".format(drugName))

def addressCrawlDrug():
    # 从0到1709找到每个药物的DrugBank Accession Number，然后调用crawl_drugbank函数获取相关信息
    with open(r'D:\Java\code\DDI-Search\src\main\java\com\ddisearch\data\node2id.json', 'r', encoding='utf-8') as f:
        node2id = json.load(f)

    # 将csv清空
    with open('./drugInfo_1710_crawl.csv', mode='w', newline='', encoding='utf-8') as file:
        writer = csv.writer(file)
        writer.writerow(['orderId', 'drugbankId', 'name', 'category', 'chemicalFormula', 'smiles', 'description', 'relatedDrugs'])

    for drugbankId in node2id:
        orderId = node2id[drugbankId]
        if(orderId >= 0):
            drugInfo = crawlDrugbank(orderId, drugbankId)

            print("{}, {}.\n".format(node2id[drugbankId], drugbankId), end='')
            time.sleep(3)
            # if(orderId == 3):
            #     break
            # break
            with open('./drugInfo_1710_crawl.csv', mode='a', newline='', encoding='utf-8') as file:
                writer = csv.writer(file)
                writer.writerow(drugInfo)

addressCrawlDrug()


