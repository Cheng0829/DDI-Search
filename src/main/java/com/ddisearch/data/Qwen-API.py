from threading import Thread
from transformers import AutoModelForCausalLM, AutoTokenizer, TextIteratorStreamer
from flask import Flask, request

# 请简要回答（不要超过100个字）：为什么药物Trioxsalen可能增加Verteporfin的光敏活性？

def _chat_stream(model, tokenizer, query):
    conversation = []
    conversation.append({"role": "user", "content": query})
    input_text = tokenizer.apply_chat_template(
        conversation,
        add_generation_prompt=True,
        tokenize=False,
    )
    inputs = tokenizer([input_text], return_tensors="pt").to(model.device)
    streamer = TextIteratorStreamer(
        tokenizer=tokenizer, skip_prompt=True, timeout=60.0, skip_special_tokens=True
    )
    generation_kwargs = {
        **inputs,
        "streamer": streamer,
    }
    thread = Thread(target=model.generate, kwargs=generation_kwargs)
    thread.start()

    for new_text in streamer:
        yield new_text

def llm(query):

    tokenizer = AutoTokenizer.from_pretrained(
        # args.checkpoint_path,
        r'./Qwen2.5-0.5B-Instruct-GPTQ-Int4',
        resume_download=True,
    )

    model = AutoModelForCausalLM.from_pretrained(
        # args.checkpoint_path,
        r'./Qwen2.5-0.5B-Instruct-GPTQ-Int4',
        torch_dtype="auto",
        device_map="auto",
        resume_download=True,
    ).eval()
    
    model.generation_config.max_new_tokens = 256  # For chat.

    while True:
        print(f"\nUser: {query}")
        print(f"\nQwen: ", end="")
        
        partial_text = ""
        for new_text in _chat_stream(model, tokenizer, query):
            print(new_text, end="", flush=True)
            partial_text += new_text
        response = partial_text
        print()
        return response


if __name__ == "__main__":
    app = Flask(__name__)
    # 定义路由和视图函数
    # http://127.0.0.1:829/llm?query=请简要回答（不要超过100个字）：为什么药物Trioxsalen可能增加Verteporfin的光敏活性？
    # http://127.0.0.1:829/llm?drugA=Trioxsalen&drugB=Verteporfin&type=1&ddi=description
    @app.route('/llm')
    def web():
        drugA_name = request.args.get('drugA', default='', type=str)
        drugB_name = request.args.get('drugB', default='', type=str)
        query_type = request.args.get('type', default='', type=int)
        ddi = request.args.get('description', default='', type=str)
        if(drugA_name == '' or drugB_name == '' or query_type not in [1, 2]):
            return "服务器繁忙，请稍后再试~"
        if(query_type == 1):
            # 查询已有DDI的原因
            query = f"请简要回答（不要超过100个字）：为什么药物{drugA_name}和{drugB_name}之间存在如下联合作用：{ddi}？"
        else:
            query = f"请简要回答（不要超过100个字）药物{drugA_name}和{drugB_name}之间可能存在哪些联合作用以及原因。"
        return llm(query)
    app.run(host='127.0.0.1', port=8290)
