"""
build_register_vector_db.py
───────────────────────────
• register.json → 문서 추출(말소/번호 라인 제거)
• FAISS 벡터 DB 생성 & 저장
"""

import os, json, re, sys
from typing import List

from dotenv import load_dotenv
from langchain_core.documents import Document
from langchain_text_splitters import RecursiveCharacterTextSplitter
from langchain_openai import OpenAIEmbeddings
from langchain_community.vectorstores import FAISS


# ────────────────────────────────────────────────────────────
# 1. 환경 / OpenAI 키
# ────────────────────────────────────────────────────────────
load_dotenv()
openai_api_key = os.getenv("OPENAI_API_KEY")
if not openai_api_key:
    sys.exit("❌  OPENAI_API_KEY 가 .env 에 없습니다.")

# ────────────────────────────────────────────────────────────
# 2. 말소·번호 라인 필터 util
# ────────────────────────────────────────────────────────────
# 번호만 적힌 줄: &2&, &(전 5)& …
NUM_LINE_RE = re.compile(r"^\s*&?\s*\(?(전)?\s*\d+\)?\s*&?\s*$")
# 말소된 기록: & … &
AMP_BLOCK_RE = re.compile(r"&[^&]*&")

def extract_valid_lines(block: str) -> List[str]:
    """
    resDetailList 의 resContents 덩어리(str) → 의미 있는 한 줄 문자열 리스트
    ① 공백 줄 스킵
    ② 번호 전용 줄 스킵
    ③ &…& 토막 삭제
    ④ 파이프·공백 정리 후 남은 라인을 공백으로 이어 단일 문자열로 반환
    """
    parts: List[str] = []
    for line in block.splitlines():
        if not line.strip():
            continue
        if NUM_LINE_RE.match(line):
            continue
        stripped = AMP_BLOCK_RE.sub("", line)                 # ➊ 말소 토막 제거
        cleaned = stripped.strip().lstrip("|").rstrip("|").strip()  # ➋ 파이프·공백 정리
        if cleaned:
            parts.append(cleaned)
    # ➌ 줄바꿈 없이 연결
    return [" ".join(parts)] if parts else []


OWNER_RE   = re.compile(r"소유자\s+([가-힣]{2,4})")           # → 홍길동
RECEIPT_RE = re.compile(r"\|\s*(\d{4})년(\d{1,2})월(\d{1,2})일")  # 첫 날짜 = 접수일

def _ymd(g):                   # ("2012","10","24") → date(2012,10,24)
    from datetime import date
    return date(*map(int, g))

def extract_documents(root: dict) -> List[Document]:
    docs: List[Document] = []

    latest_owner = None
    latest_date  = None      # datetime.date

    for entry in root.get("resRegisterEntriesList", []):
        for section in entry.get("resRegistrationHisList", []):
            sec_type = section.get("resType", "")  # "표제부"/"갑구"/"을구"
            for content in section.get("resContentsList", []):
                segments: List[str] = []

                for detail in content.get("resDetailList", []):
                    raw = detail.get("resContents", "")
                    if not raw:
                        continue
                    segments += extract_valid_lines(raw)     # 모든 라인 누적

                if not segments:
                    continue

                combined = f"[{sec_type}] " + " | ".join(segments)
                docs.append(Document(page_content=combined))

                # ── 최신 소유권이전 추적 (갑구만 대상) ──────────────────
                if sec_type == "갑구" and "소유권이전" in combined:
                    m_date  = RECEIPT_RE.search(combined)
                    m_owner = OWNER_RE .search(combined)
                    if m_date and m_owner:
                        rec_dt = _ymd(m_date.groups())
                        if latest_date is None or rec_dt > latest_date:
                            latest_date  = rec_dt
                            latest_owner = m_owner.group(1)

    # ── 최종 소유자 요약 라인 삽입 ───────────────────────────────────
    if latest_owner and latest_date:
        summary = f"[최종소유주] : {latest_owner}"
        docs.append(Document(page_content=summary))

    print(f"✅  추출 문서 {len(docs)}건 (요약 포함)")
    return docs

# ────────────────────────────────────────────────────────────
# 4. 벡터 DB 생성 / 저장
# ────────────────────────────────────────────────────────────
def build_register_vector_db(json_path: str, save_path: str):
    print(f"[▶] JSON 로드: {json_path}")
    with open(json_path, encoding="utf-8") as f:
        data = json.load(f).get("data", {})

    docs = extract_documents(data)
    if not docs:
        print("⚠️  문서가 없어 벡터 DB 생략")
        return

    splitter = RecursiveCharacterTextSplitter(chunk_size=300, chunk_overlap=30)
    chunks = splitter.split_documents(docs)
    print(f"➡️  임베딩 chunk {len(chunks)}개")

    vec = FAISS.from_documents(chunks, OpenAIEmbeddings(openai_api_key=openai_api_key))
    vec.save_local(save_path)
    print(f"🎉  저장 완료: {save_path}")


# ────────────────────────────────────────────────────────────
# 5. CLI 실행 지원
# ────────────────────────────────────────────────────────────
if __name__ == "__main__":
    build_register_vector_db("register.json", "./vector_db/register")
