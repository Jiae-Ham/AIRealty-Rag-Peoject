"""
main.py – /analyze 엔드포인트
────────────────────────────────────────
• 업로드 JSON → 임시 저장 → 벡터 DB 생성
• 정적·동적 벡터 DB 로드 → RAG 컨텍스트 구성
• UID(TS+파일해시) 프롬프트로 캐시 무효화
• 디버깅: similarity_search 결과 미리보기
"""

import os, shutil, tempfile, hashlib
from datetime import datetime
from pathlib import Path
from typing import List

from fastapi import FastAPI, UploadFile, File
from dotenv import load_dotenv
from langchain.globals import set_llm_cache
from langchain_openai import ChatOpenAI
from langchain_core.documents import Document

from build_register_vector_db import build_register_vector_db
from query_engine               import load_vector_db
from multi_question_prompt      import multi_question_prompt


# ─────────────────────── 환경 / 캐시 비활성화 ───────────────────────
load_dotenv()
openai_api_key = os.getenv("OPENAI_API_KEY") or ""
set_llm_cache(None)                     # LangChain LLM 캐시 끄기

# ────────────────────────── FastAPI ──────────────────────────
app = FastAPI()


# ──────────────────────── 디버깅 helper ────────────────────────
def preview(name: str, docs: List[Document], n: int | None = None):
    if n is None:          # n을 생략하면 전체
        n = len(docs)
    print(f"\n📝 {name} top-{n}")
    for i, d in enumerate(docs[:n], 1):
        print(f"{i}. {d.page_content[:120]} …")


# ──────────────────────── 엔드포인트 ────────────────────────
@app.post("/analyze")
async def analyze_register(file: UploadFile = File(...)):
    # 1. 업로드 JSON 저장 (임시 폴더)
    tmp_dir  = Path(tempfile.mkdtemp())
    json_fp  = tmp_dir / file.filename
    with json_fp.open("wb") as f:
        shutil.copyfileobj(file.file, f)

    # 2. 벡터 DB 경로: <파일명>.<타임스탬프>  (항상 새로 생성 → 캐시無)
    ts       = datetime.now().strftime("%Y%m%d_%H%M%S")
    vec_dir  = Path("./vector_db") / f"{file.filename}.{ts}"
    vec_dir.mkdir(parents=True, exist_ok=True)

    # 3. 벡터 DB 생성
    build_register_vector_db(str(json_fp), str(vec_dir))

    # 4. 벡터 DB 로드
    register_db = load_vector_db(str(vec_dir))
    refund_db   = load_vector_db("./vector_db/refund_static")
    terms_db    = load_vector_db("./vector_db/terms")

    # 5. similarity_search
    reg_hits  = register_db.similarity_search("등기요약 생성", k=40)
    ref_hits  = refund_db  .similarity_search("환급 기준",     k=3)
    term_hits = terms_db   .similarity_search("등기 용어 해설", k=3)

    # 5-b. 디버깅 미리보기 (콘솔)
# 5-b. 디버깅 미리보기 (콘솔)
    preview("등기요약", reg_hits,  n=len(reg_hits))   # ← 전체 출력
    preview("환급기준", ref_hits,  n=len(ref_hits))
    preview("용어해설", term_hits, n=len(term_hits))


    # 6. RAG 컨텍스트
    ctx = "\n".join([d.page_content for d in (*reg_hits, *ref_hits, *term_hits)])

    # 7. UID(해시·타임스탬프)로 LLM 프롬프트 캐시 완전 무효화
    file_hash = hashlib.md5(json_fp.read_bytes()).hexdigest()[:8]
    uid       = f"<<{file_hash}|{ts}>>"

    # 8. LLM 호출
    llm = ChatOpenAI(
        openai_api_key=openai_api_key,
        model="gpt-4",
        temperature=0,
    )
    answer = (multi_question_prompt | llm).invoke({
        "uid": uid,
        "등기요약": ctx,
    })

    return {"uid": uid, "result": answer}
