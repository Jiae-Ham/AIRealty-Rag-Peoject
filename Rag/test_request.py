import requests

url = "http://localhost:8000/analyze"  # 서버 실행 중인지 확인!
file_path = "register.json"  # 업로드할 등기부 JSON 파일

with open(file_path, "rb") as f:
    files = {"file": (file_path, f, "application/json")}
    response = requests.post(url, files=files)

print("[📄 분석 결과]")
print(response.json())
