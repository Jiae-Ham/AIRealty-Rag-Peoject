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

# ✅ CSV 파일 로드
df = pd.read_csv("kar_realty_terms.csv")

# ✅ 용어 + 설명 결합 → 문서화
docs = []
for _, row in df.iterrows():
    keyword = str(row["keyword"]).strip()
    description = str(row["description"]).strip()
    if keyword and description:
        content = f"{keyword}: {description}"
        docs.append(Document(page_content=content))

# ✅ 텍스트 분할 (너무 긴 문서 방지)
splitter = RecursiveCharacterTextSplitter(chunk_size=300, chunk_overlap=30)
split_docs = splitter.split_documents(docs)

# ✅ 임베딩 생성 + 벡터 저장
embedding = OpenAIEmbeddings(openai_api_key=openai_api_key)
vector_db = FAISS.from_documents(split_docs, embedding)
vector_db.save_local("./vector_db/terms")

print("[✅완료] 용어사전 벡터 DB 저장됨 → ./vector_db/terms")
