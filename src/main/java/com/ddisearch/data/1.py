# -*- coding: utf-8 -*-
# @author  : Junkai Cheng
# @time    : 2024/9/29 21:22


import requests, json, random, time, csv
from bs4 import BeautifulSoup

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
        f.write("")
    for i in range(1, len(ddi_DB)):
        ddi_replace = ddi_DB[i].strip().split('\t')
        print(ddi_replace)
        print(DrugName_DrugBankId[ddi_replace[0]] + '\t' + DrugName_DrugBankId[ddi_replace[1]] + '\t' + ddi_replace[2] + '\n')
        pass
        with open('ddi_name.txt', mode='a', newline='', encoding='utf-8') as f:
            f.write(ddi_replace.join('\t') + '\n')

# ddi_from_DB_to_name()


