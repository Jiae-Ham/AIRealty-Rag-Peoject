from typing import List  # ✅ 추가

def is_fully_ampersanded(text: str) -> bool:
    text = text.strip()
    return text.startswith("&") and text.endswith("&")

def extract_valid_lines_from_detail_list_block(detail_block: str) -> List[str]:
    lines = detail_block.split("\n")

    valid_lines = []
    for line in lines:
        if not line.strip():
            continue
        if is_fully_ampersanded(line):
            continue
        cleaned = line.strip().replace("&", "")
        valid_lines.append(cleaned)

    return valid_lines

if __name__ == "__main__":
    block = "&채권최고액 금5,850,000원&\n&채무자 한민영&\n&천안시 북면 연춘리 123-3 삼원아파트&\n&2-409&\n&근저당권자 교보생명보험주식회사&\n&110111-0014970&\n&서울 종로구 종로1가 1&"
    print("💡 split 결과:")
    lines = block.splitlines()
    for line in lines:
        print("-", line)

    result = extract_valid_lines_from_detail_list_block(block)
    print("\n✅ 유효한 라인만 추출됨:")
    for line in result:
        print(f"- {line}")
