# -*- coding: utf-8 -*-
# @author  : Junkai Cheng
# @time    : 2024/9/29 21:22


import requests, json, random, time, csv
from bs4 import BeautifulSoup
from rdkit import Chem
from rdkit.Chem import Draw
from collections import defaultdict

def crawlDrugbank(drugbankId):
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
    drugName = soup.find('dt', {'id': 'generic-name'})
    if drugName is None:
        drugName = soup.find('dt', {'id': 'name'})
    drugName = drugName.find_next_sibling('dd').text.strip().replace("\n", " ")

    return drugName

def addressCrawlDrug():
    # 从0到1709找到每个药物的DrugBank Accession Number，然后调用crawl_drugbank函数获取相关信息
    with open(r'./node2id.json', 'r', encoding='utf-8') as f:
        node2id = json.load(f)
    # DrugName_DrugBankId = {}
    # with open('./DrugName_DrugBankId.json', mode='w', newline='', encoding='utf-8') as f:
    #     json.dump({}, f, ensure_ascii=False)
    for drugbankId in node2id:
        if(node2id[drugbankId] < 1336):
            continue
        drugName = crawlDrugbank(drugbankId)
        print("{}. {}: {}.\n".format(node2id[drugbankId], drugbankId, drugName), end='')
        # DrugName_DrugBankId[drugbankId] = drugName
        time.sleep(3)
        # if(node2id[drugbankId] == 3):
        #     break
        with open('./DrugName_DrugBankId.json', mode='r', newline='', encoding='utf-8') as f:
            old_DrugName_DrugBankId = json.load(f)
        with open('./DrugName_DrugBankId.json', mode='w', newline='', encoding='utf-8') as f:
            old_DrugName_DrugBankId[drugbankId] = drugName
            json.dump(old_DrugName_DrugBankId, f, ensure_ascii=False)

# addressCrawlDrug()

def ddi_from_DB_to_name():
    with open('./DrugName_DrugBankId.json', mode='r', newline='', encoding='utf-8') as f:
        DrugName_DrugBankId = json.load(f)
    with open('ddi_DB.txt', mode='r', newline='', encoding='utf-8') as f:
        ddi_DB = f.readlines()
    with open('ddi_name.txt', mode='w', newline='', encoding='utf-8') as f:
        f.write("drug1\tdrug2\tLabel\n")
    for i in range(1, len(ddi_DB)):
        ddi_replace = ddi_DB[i].strip().split('\t')
        # print(ddi_replace)
        if(i % 1000 == 0):
            print(i, end=', ')
        with open('ddi_name.txt', mode='a', newline='', encoding='utf-8') as f:
            f.write(DrugName_DrugBankId[ddi_replace[0]] + '\t' + DrugName_DrugBankId[ddi_replace[1]] + '\t' + ddi_replace[2] + '\n')

# ddi_from_DB_to_name()

def read():
    with open('./DrugName_DrugBankId.json', mode='r', newline='', encoding='utf-8') as f:
        DrugName_DrugBankId = json.load(f)
    with open('ddi_DB_sampling.txt', mode='r', newline='', encoding='utf-8') as f:
        ddi_DB = f.readlines()
    # with open('ddi_DB_sampling_add_description.txt', mode='w', newline='', encoding='utf-8') as f:
    #     f.write("drug1\tdrug2\tLabel\tdescription\n")
    for i in range(94, len(ddi_DB)):
        drugAId = ddi_DB[i].strip().split('\t')[0]
        drugBName = DrugName_DrugBankId[ddi_DB[i].strip().split('\t')[1]]
        url = "https://go.drugbank.com/drugs/{}/drug_interactions.json?search%5Bvalue={}".format(drugAId, drugBName)
        response = requests.get(url)

        data = response.json()['data']
        description = data[0][1]
        with open('ddi_DB_sampling_add_description.txt', mode='a', newline='', encoding='utf-8') as f:
            f.write(ddi_DB[i].strip().split('\t')[0] + '\t' + ddi_DB[i].strip().split('\t')[1] + '\t' + ddi_DB[i].strip().split('\t')[2] + '\t' + description + '\n')
        print(i, 'type=' + ddi_DB[i].strip().split('\t')[2] + ': ', description)
        time.sleep(3)
        # break
# read()

def DrugBankId_DrugName():
    with open('./DrugName_DrugBankId.json', mode='r', newline='', encoding='utf-8') as f:
        DrugName_DrugBankId = json.load(f)
    DrugBankId_DrugName = {}
    for DrugBankId in DrugName_DrugBankId:
        DrugBankId_DrugName[DrugName_DrugBankId[DrugBankId]] = DrugBankId
    print(DrugBankId_DrugName)
    with open('./DrugBankId_DrugName.json', mode='w', newline='', encoding='utf-8') as f:
        json.dump(DrugBankId_DrugName, f, ensure_ascii=False)
# DrugBankId_DrugName()

def gaixie():
    with open('./DDI-DescriptionTemplate.json', mode='r', newline='', encoding='utf-8') as f:
        DDIDescriptionTemplate = json.load(f)

    # 创建一个默认字典来存储value及其对应的key列表
    value_to_keys = defaultdict(list)

    # 遍历原始数据，构建value_to_keys映射
    for key, value in DDIDescriptionTemplate.items():
        value_to_keys[value].append(key)

    # 创建新的字典，其中key是原始的value，value是key列表
    new_data = {value: keys for value, keys in value_to_keys.items()}
    print(new_data)
    # 将新字典写入新的JSON文件
    with open('DDI-DescriptionTemplate-reverse.json', 'w') as file:
        json.dump(new_data, file, indent=4)

# gaixie()
