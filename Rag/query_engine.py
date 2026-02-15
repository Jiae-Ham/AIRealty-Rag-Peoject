# query_engine.py
import os
from dotenv import load_dotenv

from langchain_openai import ChatOpenAI, OpenAIEmbeddings
from langchain_community.vectorstores import FAISS
from langchain.chains import RetrievalQA

load_dotenv()
openai_api_key = os.getenv("OPENAI_API_KEY")
if not openai_api_key:
    raise ValueError("OPENAI_API_KEY가 .env에 설정되지 않았습니다.")

def load_vector_db(path):
    return FAISS.load_local(path, OpenAIEmbeddings(openai_api_key=openai_api_key), allow_dangerous_deserialization=True)

def query_vector_db(db_path: str, query: str):
    db = load_vector_db(db_path)
    retriever = db.as_retriever()
    llm = ChatOpenAI(openai_api_key=openai_api_key, model="gpt-4", temperature=0)
    chain = RetrievalQA.from_chain_type(llm=llm, retriever=retriever)
    return chain.run(query)
