import json
from http.server import BaseHTTPRequestHandler, ThreadingHTTPServer


STUDENTS = [
    {
        "id": "demo-01",
        "full_name": "Nguyễn Minh Anh",
        "student_code": "API001",
        "email": "minhanh@example.org",
        "gpa": 3.65,
    },
    {
        "id": "demo-02",
        "full_name": "Trần Gia Huy",
        "student_code": "API002",
        "email": "giahuy@example.org",
        "gpa": 3.42,
    },
]


class StudentHandler(BaseHTTPRequestHandler):
    def do_GET(self):
        if self.path != "/api/v1/students":
            self.send_error(404)
            return

        body = json.dumps(STUDENTS, ensure_ascii=False).encode("utf-8")
        self.send_response(200)
        self.send_header("Content-Type", "application/json; charset=utf-8")
        self.send_header("Content-Length", str(len(body)))
        self.end_headers()
        self.wfile.write(body)


if __name__ == "__main__":
    with ThreadingHTTPServer(("127.0.0.1", 8765), StudentHandler) as server:
        print("GET http://127.0.0.1:8765/api/v1/students", flush=True)
        try:
            server.serve_forever()
        except KeyboardInterrupt:
            pass
