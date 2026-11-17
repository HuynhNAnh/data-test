# Lab 9 — Room Database và REST API

Bài lab nằm trong module `app`, package `vn.udn.ute.lab9_data`. Phần Room bám theo 10 bước ở trang 4–11 của bài giảng. Phần REST ở trang 12–13 dùng API mẫu chạy trên máy tính để thay cho địa chỉ minh họa trong PDF.

## 1. Mở và chạy app

1. Mở **thư mục gốc** của project trong Android Studio, nơi có `settings.gradle.kts`.
2. Trong **SDK Manager**, cài Android SDK 37, Build Tools 36.0.0 và Platform-Tools. Cấu hình project dùng Gradle 9.5.0, AGP 9.3.3 và JDK 25 cho Gradle daemon. Chọn JDK 25 tại **Settings → Build, Execution, Deployment → Build Tools → Gradle** nếu IDE chưa tự nhận.
3. Chạy **Sync Project with Gradle Files**. Lần đầu cần mạng để tải Gradle và các thư viện mới.
4. Chọn cấu hình **app** và bản **debug**. Chọn máy ảo hoặc cắm điện thoại Android 7.0 trở lên đã bật **USB debugging**, rồi bấm **Run**.

Có thể kiểm tra build từ PowerShell ở thư mục gốc. Terminal cần trỏ `JAVA_HOME` tới JDK 25 và nhận được vị trí Android SDK:

```powershell
.\gradlew.bat :app:assembleDebug :app:testDebugUnitTest
```

## 2. Thử Room trước

1. Bấm **Thêm sinh viên**. Nhập họ tên, MSSV `SV001`, GPA `3.50` và bấm **Lưu**. Email có thể để trống. Danh sách cập nhật ngay.
2. Thử thêm lại `SV001`: app báo MSSV đã tồn tại và giữ nguyên dữ liệu. Thử lưu GPA ngoài khoảng 0–10: hộp thoại vẫn mở để sửa.
3. Dừng app rồi mở lại: `SV001` vẫn còn. Bấm **Xóa** và xác nhận: hàng biến mất.
4. Nếu muốn nhìn bảng thật, khi app debug đang chạy trên thiết bị API 26 trở lên, mở **View → Tool Windows → App Inspection → Database Inspector** rồi xem `ute_student_database.db` → `students_table`.

Luồng đọc: `StudentDao` phát `Flow` → Repository → ViewModel chuyển thành `LiveData` → Fragment quan sát bằng `viewLifecycleOwner` → `ListAdapter` cập nhật danh sách. Khi thêm hoặc xóa, ViewModel gọi hàm `suspend` của Room qua Repository. DAO có hàm cập nhật để minh họa `@Update`; giao diện bài lab tập trung vào xem, thêm và xóa.

## 3. Thử Retrofit và kiểm tra ngoại tuyến

Phần này cần Python 3. Mở PowerShell tại thư mục gốc, chạy lệnh sau rồi giữ cửa sổ mở:

```powershell
python .\tools\mock_student_api.py
```

Nếu dùng Python Launcher trên Windows, có thể thay `python` bằng `py -3`. Ở cửa sổ PowerShell khác, chạy:

```powershell
curl.exe http://127.0.0.1:8765/api/v1/students
adb devices
adb reverse tcp:8765 tcp:8765
```

Nếu chưa có `adb` trong PATH, dùng `adb.exe` tại thư mục `platform-tools` của Android SDK. Nếu có nhiều thiết bị, dùng `adb -s SERIAL reverse tcp:8765 tcp:8765` với serial từ `adb devices`. Chạy lại `adb reverse` nếu rút cáp hoặc khởi động lại máy ảo.

1. Trong app debug, bấm **Tải từ API**. Hai dòng mẫu `API001` và `API002` xuất hiện.
2. Bấm tải lần hai: các MSSV đã có trong Room được bỏ qua để giữ dữ liệu đang lưu trên máy.
3. Tắt API bằng Ctrl+C rồi bấm tải lại: app báo không kết nối được, danh sách đã lưu vẫn còn. Đóng và mở lại app để kiểm tra dữ liệu đã được ghi xuống đĩa.

API mẫu chỉ minh họa `GET /api/v1/students`. Thêm và xóa trên app chỉ thay đổi Room. Nếu xóa một dòng mẫu rồi tải lại, dòng đó sẽ được nhập lại từ API. Cấu hình HTTP localhost và nút tải chỉ dành cho bản debug.

## Nếu gặp lỗi

- **Gradle Sync không tải được KSP hoặc thư viện:** kiểm tra mạng, tắt Gradle Offline Mode nếu đang bật, rồi Sync lại.
- **Không tìm thấy `FragmentStudentListBinding`:** kiểm tra Sync/Build có lỗi trước đó không. View Binding sinh lớp này từ `fragment_student_list.xml`; không cần tự tạo lớp.
- **Thiết bị hiện `unauthorized`:** mở khóa điện thoại, chấp nhận hộp thoại USB debugging, rồi chạy lại `adb devices`.
- **Nút tải báo lỗi kết nối:** kiểm tra `curl.exe` trên máy tính trả về JSON và `adb reverse --list` có cổng `8765`.
