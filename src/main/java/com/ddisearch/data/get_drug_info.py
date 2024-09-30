# -*- coding: utf-8 -*-
# @author  : Junkai Cheng
# @time    : 2024/9/27 18:09

import requests, json, random, time, csv
from bs4 import BeautifulSoup
from rdkit import Chem
from rdkit.Chem import Draw

class drugInfo:
    # drug信息包括：药物编号、药物drugbank序列号、药物名、类别、化学分子式、描述、相关药物
    def __init__(self, orderId, drugbankId='', name='', category='', chemicalFormula='', smiles='', description='', relatedDrugs='', pharmacodynamicsm='', actionMechanism='', proteinBinding='', metabolism=''):
        self.orderId = orderId
        self.drugbankId = drugbankId
        self.name = name
        self.category = category
        self.chemicalFormula = chemicalFormula
        self.smiles = smiles
        self.description = description
        self.relatedDrugs = relatedDrugs
        self.pharmacodynamicsm = pharmacodynamicsm
        self.actionMechanism = actionMechanism
        self.proteinBinding = proteinBinding
        self.metabolism = metabolism


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
    if soup.find('dt', {'id': 'generic-name'}):
        drugName = soup.find('dt', {'id': 'generic-name'}).find_next_sibling('dd').text.strip().replace("\n", " ")
    elif soup.find('dt', {'id': 'name'}):
        drugName = soup.find('dt', {'id': 'name'}).find_next_sibling('dd').text.strip().replace("\n", " ")
    else:
        drugName = ''
    # # 提取DrugBank Accession Number
    # drugbankId = soup.find('dt', {'id': 'drugbankId'}).find_next_sibling('dd').text.strip().replace("\n", " ")

    # 提取Background
    if soup.find('dt', {'id': 'background'}):
        description = soup.find('dt', {'id': 'background'}).find_next_sibling('dd').text.strip().replace("\n", " ")
    else:
        description = ''

    if soup.find('dt', {'id': 'drug-categories'}):
        dd_element = soup.find('dt', {'id': 'drug-categories'}).find_next_sibling('dd')
        # 在 dd 元素中找到 ul 元素
        ul_element = dd_element.find('ul', {'class': 'list-unstyled table-list'})
        # 提取所有 li 元素的文本内容
        categories = [li.text.strip() for li in ul_element.find_all('li')][:5]
        category = ", ".join(categories)
    else:
        category = ''

    # 提取Chemical Formula
    if soup.find('dt', {'id': 'chemical-formula'}):
        chemicalFormula = soup.find('dt', {'id': 'chemical-formula'}).find_next_sibling('dd').text.strip().replace("\n", " ")
    else:
        chemicalFormula = ''

    if soup.find('dt', {'id': 'smiles'}):
        smiles = soup.find('dt', {'id': 'smiles'}).find_next_sibling('dd').text.strip().replace("\n", " ")
    else:
        smiles= ''
    FromSmilesToImage(drugName=drugName, drugSmiles=smiles)

    # 药效学
    if soup.find('dt', {'id': 'pharmacodynamics'}):
        pharmacodynamics = soup.find('dt', {'id': 'pharmacodynamics'}).find_next_sibling('dd').text.strip().replace("\n", " ")
        if pharmacodynamics == 'Not Available':
            pharmacodynamics = ''
    else:
        pharmacodynamics = ''

    # 作用机制
    if soup.find('dt', {'id': 'mechanism-of-action'}):
        actionMechanism = soup.find('dt', {'id': 'mechanism-of-action'}).find_next_sibling('dd').text.strip().replace("\n", " ")
        if actionMechanism == 'Not Available':
            actionMechanism = ''
    else:
        actionMechanism = ''

    # 蛋白质结合
    if soup.find('dt', {'id': 'protein_binding'}):
        proteinBinding = soup.find('dt', {'id': 'protein_binding'}).find_next_sibling('dd').text.strip().replace("\n", " ")
        if proteinBinding == 'Not Available':
            proteinBinding = ''
    else:
        proteinBinding = ''

    # 代谢
    if soup.find('dt', {'id': 'metabolism'}):
        metabolism = soup.find('dt', {'id': 'metabolism'}).find_next_sibling('dd').text.strip().replace("\n", " ")
        if metabolism == 'Not Available':
            metabolism = ''
    else:
        metabolism = ''

    relatedDrugs = findRelatedDrugs(drugName, n=5)

    return [orderId, drugbankId, drugName, category, chemicalFormula, smiles, description, relatedDrugs, pharmacodynamics, actionMechanism, proteinBinding, metabolism]
def findRelatedDrugs(drugName, n=5):
    with open('ddi_name.txt', mode='r', newline='', encoding='utf-8') as f:
        ddis = f.readlines()
    relatedDrugs = []
    for i in range(1, len(ddis)):
        ddi = ddis[i].strip().split('\t')
        if(ddi[0] == drugName and ' ' not in ddi[1]):
            relatedDrugs.append(ddi[1])
        if(ddi[1] == drugName and ' ' not in ddi[0]):
            relatedDrugs.append(ddi[0])
        if len(relatedDrugs) == n:
            break
    return ", ".join(relatedDrugs)

def FromSmilesToImage(drugName, drugSmiles=''):
    # 从SMILES创建分子对象
    mol = Chem.MolFromSmiles(drugSmiles)
    # mol = Chem.MolFromSmiles("CC1=CC2=CC3=C(OC(=O)C=C3C)C(C)=C2O1")
    # 绘制分子
    img = Draw.MolToImage(mol)
    # 保存图像
    img.save("./drugImage/{}.png".format(drugName))
def addressCrawlDrug():
    # 从0到1709找到每个药物的DrugBank Accession Number，然后调用crawl_drugbank函数获取相关信息
    with open(r'D:\Java\code\DDI-Search\src\main\java\com\ddisearch\data\node2id.json', 'r', encoding='utf-8') as f:
        node2id = json.load(f)

    # 将csv清空
    with open('./drugInfo_1710_crawl.csv', mode='w', newline='', encoding='utf-8') as f:
        writer = csv.writer(f)
        writer.writerow(['orderId', 'drugbankId', 'name', 'category', 'chemicalFormula', 'smiles', 'description', 'relatedDrugs', 'pharmacodynamics', 'actionMechanism', 'proteinBinding', 'metabolism'])

    for drugbankId in node2id:
        orderId = node2id[drugbankId]
        if(orderId >= 0):
            drugInfo = crawlDrugbank(orderId, drugbankId)

            print("{}, {}.\n".format(node2id[drugbankId], drugbankId), end='')
            time.sleep(3)
            # if(orderId == 3):
            #     break
            # break
            with open('./drugInfo_1710_crawl.csv', mode='a', newline='', encoding='utf-8') as f:
                writer = csv.writer(f)
                writer.writerow(drugInfo)

# addressCrawlDrug()

# with open('./drugInfo_1710_crawl.csv', mode='r', newline='', encoding='utf-8') as f:
#     drugInfos = f.readlines()
# for i in range(1, len(drugInfos)):
#     FromSmilesToImage(drugName=drugInfos[i][2], drugSmiles=drugInfos[i][5])