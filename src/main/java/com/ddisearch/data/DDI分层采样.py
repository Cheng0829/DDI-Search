import pandas as pd
import numpy as np

def assign_drug_frequency_label(row, drug_freq, thresholds):
    # 获取药物A和B的出现频率
    freq_a = drug_freq[row['drug_a']]
    freq_b = drug_freq[row['drug_b']]
    
    # 取两个药物中较少出现的那个作为这个配对的稀有度标准
    min_freq = min(freq_a, freq_b)
    
    if min_freq <= thresholds['rare']:
        return 'rare'
    elif min_freq <= thresholds['medium']:
        return 'medium'
    else:
        return 'common'
def validate_sampling(original_df, sampled_df):
    # 检查各层的采样比例
    original_dist = original_df['frequency_label'].value_counts()
    sampled_dist = sampled_df['frequency_label'].value_counts()
    
    print("Original distribution:")
    print(original_dist)
    print("\nSampled distribution:")
    print(sampled_dist)
    
    # 检查是否保留了所有罕见药物对
    rare_pairs_original = set(original_df[original_df['frequency_label'] == 'rare']
                            .apply(lambda x: tuple(sorted([x['drug_a'], x['drug_b']])), axis=1))
    rare_pairs_sampled = set(sampled_df[sampled_df['frequency_label'] == 'rare']
                            .apply(lambda x: tuple(sorted([x['drug_a'], x['drug_b']])), axis=1))
    
    print(f"\nRare pairs retention rate: {len(rare_pairs_sampled)/len(rare_pairs_original):.2%}")
    
# 保存采样后的数据，去掉frequency_label列，保持原始的三元组格式
def save_sampled_data(sampled_df, output_file):
    # 只保留原始的三列
    result_df = sampled_df[['drug_a', 'drug_b', 'interaction']]
    
    # 写入文件，不包含索引和列名
    result_df.to_csv(output_file, 
                    sep='\t', 
                    index=False, 
                    header=False)
    
    # 打印保存信息
    # print(f"\nSampled data saved to {output_file}")
    # print(f"Original data size: {len(df)}")
    # print(f"Sampled data size: {len(result_df)}")
    # print(f"Sampling rate: {len(result_df)/len(df):.2%}")
    
def stratified_sampling(df, sampling_rates):
    sampled_dfs = []
    for label, rate in sampling_rates.items():
        stratum = df[df['frequency_label'] == label]
        sampled_stratum = stratum.sample(frac=rate, random_state=42)
        sampled_dfs.append(sampled_stratum)
    
    return pd.concat(sampled_dfs)

# 假设数据格式为：
df = pd.read_csv('ALL_ddi.txt', sep='\t', names=['drug_a', 'drug_b', 'interaction'])

# 统计每个药物参与相互作用的频率
drug_freq = pd.concat([df['drug_a'], df['drug_b']]).value_counts()

# 统计每种相互作用值的频率
interaction_freq = df['interaction'].value_counts()

# 设定阈值，这些值需要根据实际数据分布调整
thresholds = {
    'rare': 100,      # 出现次数<=10次的视为罕见
    'medium': 500    # 出现次数<=100次的视为中等
}

# 添加频率标签
df['frequency_label'] = df.apply(lambda x: assign_drug_frequency_label(x, drug_freq, thresholds), axis=1)

# 设定各层的采样比例
sampling_rates = {
    'rare': 1.0,      # 罕见药物对保留100%
    'medium': 0.5,    # 中等频率药物对保留50%
    'common': 0.1     # 常见药物对保留20%
}

# 执行分层抽样
sampled_df = stratified_sampling(df, sampling_rates)

validate_sampling(df, sampled_df)

# 调用保存函数
save_sampled_data(sampled_df, "ddi_stratified_sampling.txt")
