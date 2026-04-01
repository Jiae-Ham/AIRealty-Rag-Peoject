# 🏠 AI Realty RAG Project  
**AI 기반 부동산 직거래 보조 플랫폼**

부동산 직거래 환경에서 발생하는 정보 비대칭과 법적 리스크를 완화하기 위해,  
LLM + RAG 기반으로 **챗봇, 권리분석, 매물 추천 기능을 통합한 AI 플랫폼**입니다.

---

## 📌 Overview

최근 부동산 직거래가 증가하면서 다음과 같은 문제가 발생합니다:

- 📉 정보 비대칭 (법률/절차 이해 부족)
- ⚠️ 권리관계 해석의 어려움 (등기부등본 분석)
- 🔍 비효율적인 매물 탐색

이 프로젝트는 이러한 문제를 해결하기 위해 다음 3가지 핵심 기능을 제공합니다:

1. **LLM 기반 부동산 챗봇**
2. **RAG 기반 권리분석 시스템**
3. **콘텐츠 기반 매물 추천 시스템**

---

## 🧠 Key Features

### 1. 💬 LLM Chatbot
- 한국어 특화 LLM 기반 질의응답
- 부동산 계약, 세금, 절차 안내
- Prompt Engineering 적용
  - Role-based Prompt
  - Few-shot Learning
  - Zero-shot Chain-of-Thought

👉 복잡한 행정/법률 질문도 단계적으로 설명 가능

---

### 2. 📄 Rights Analysis (RAG)
- 등기부등본(JSON) 자동 수집 및 전처리
- Vector DB 기반 문서 임베딩
- Retrieval-Augmented Generation 적용

**핵심 기능**
- 근저당, 가압류, 전세권 등 위험 요소 탐지
- 근거 문단 기반 설명 제공
- 사용자 친화적 리스크 요약

👉 “왜 위험한지”를 근거와 함께 설명하는 것이 핵심

---

### 3. 🏘️ Property Recommendation
- 콘텐츠 기반 필터링 (Content-Based Filtering)
- 사용자 선호 벡터 생성

**추천 프로세스**
1. 찜한 매물 기반 선호 벡터 생성
2. 후보 매물 샘플링
3. 유사도 계산 (Cosine Similarity)
4. 상위 매물 추천

👉 실험 결과:  
- Cosine Similarity가 nDCG 성능에서 가장 우수

---

## 🏗️ System Architecture


<img width="258" height="175" alt="image" src="https://github.com/user-attachments/assets/c433f224-c55f-44cb-9e02-5af93c0607bd" />


---

## ⚙️ Tech Stack

### 🔹 AI / ML
- LLM (HyperCLOVA X 기반)
- RAG (LangChain)
- Embedding + Vector DB

### 🔹 Backend
- Python
- FastAPI (or similar)
- REST API

### 🔹 Data
- 등기부등본 API (외부)
- 네이버 부동산 API

### 🔹 Recommendation
- Content-Based Filtering
- Cosine Similarity

---

## 🔄 Pipeline

### 📄 권리분석 흐름
등기부등본 API → JSON 수집 → 전처리 → Vector DB 저장→ Query 생성 → Retriever → Generator → 결과 반환

<img width="249" height="165" alt="image" src="https://github.com/user-attachments/assets/3052b644-394a-4e0e-9db9-14f5611b1bf5" />

### 💬 챗봇 흐름
User Query → Prompt 구성 → LLM → 응답 생성 → 포맷팅

<img width="250" height="110" alt="image" src="https://github.com/user-attachments/assets/fb8bdc50-e192-4650-b1b7-a1593ebb2450" />

### 🏠 추천 흐름
찜 데이터 → 선호 벡터 생성 → 후보 매물 → 유사도 계산 → 추천


---

## 📊 Experiment Results

### ✔ 챗봇 성능
- 기존 LLM 대비 높은:
  - 정확성
  - 사용자 응대성
  - 계산 추론 능력

### ✔ 권리분석
- RAG 적용 시:
  - 정확성 ↑
  - 신뢰성 ↑
  - 근거 기반 설명 가능

### ✔ 추천 시스템

| Metric     | Result |
|-----------|--------|
| Recall@10 | 동일   |
| nDCG@10   | Cosine 우수 |
| ILD@10    | Pearson 우수 |

👉 실서비스 기준 → **Cosine 선택**

---

## 🚀 Contributions

- 부동산 직거래 특화 **통합 AI 플랫폼 설계**
- RAG 기반 **법률 문서 해석 시스템**
- 사용자 신뢰 중심 **추천 시스템 설계**
- 실제 시나리오 기반 성능 검증

---

## 📁 Project Structure
AIRealty-Rag-Project/
│
├── chatbot/
├── rag/
├── recommendation/
├── data/
├── api/
├── utils/
└── README.md

---

## 🔮 Future Work

- 실시간 매물 데이터 확장
- 법률 DB 정교화
- 사용자 행동 기반 추천 (Hybrid 모델)
- UI/UX 개선 및 서비스화

---

## UI
<br>

## 📱 서비스 화면 (UI)

<br>

## 📱 서비스 UI 화면

| 온보딩 | 로그인 | 홈 |
| :---: | :---: | :---: |
| <img src="https://github.com/user-attachments/assets/353b2340-283e-4f38-b777-19f4cdeee012" width="160"> | <img src="https://github.com/user-attachments/assets/a169928d-9e21-44fb-9499-12fc42331f5a" width="160"> | <img src="https://github.com/user-attachments/assets/1503136c-7c9d-4e09-923e-150a5d89dc6a" width="160"> |
| **문서 분석** | **문서 분석-1** | **문서 분석-2** |
| <img src="https://github.com/user-attachments/assets/762ace10-7faa-4fcc-ae22-b7c58b1b4066" width="160"> | <img src="https://github.com/user-attachments/assets/a50a3c1d-8c91-4d0e-b58b-f8cdd55aecea" width="160"> | <img src="https://github.com/user-attachments/assets/b116edc8-35b2-41ed-8701-2dd2ee2890d4" width="160"> |




