import os
import pandas as pd
from dotenv import load_dotenv

from langchain_core.documents import Document
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_openai import OpenAIEmbeddings
from langchain_community.vectorstores import FAISS

# ✅ .env 파일에서 API 키 로드
load_dotenv()
openai_api_key = os.getenv("OPENAI_API_KEY")
if not openai_api_key:
    raise ValueError("OPENAI_API_KEY가 .env에 설정되지 않았습니다.")

# ✅ CSV 파일 로드 (ex: 2025_realty_refund.csv)
df = pd.read_csv("2025_realty_refund.csv")

# ✅ 각 행을 Document로 변환 (key-value처럼 정리)
docs = []
for _, row in df.iterrows():
    line = " | ".join([f"{col.strip()}: {str(row[col]).strip()}" for col in df.columns])
    docs.append(Document(page_content=line))

# ✅ 텍스트 분할
splitter = RecursiveCharacterTextSplitter(chunk_size=300, chunk_overlap=30)
split_docs = splitter.split_documents(docs)

# ✅ 임베딩 및 벡터 저장
embedding = OpenAIEmbeddings(openai_api_key=openai_api_key)
vector_db = FAISS.from_documents(split_docs, embedding)
vector_db.save_local("./vector_db/refund_static")

print("[✅완료] CSV 기반 정적 벡터 DB 저장 완료 → ./vector_db/refund_static")
