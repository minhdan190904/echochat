# 📱 EchoChat – Chat App Real-Time  

Ứng dụng chat 1-1 **real-time** với đầy đủ tính năng: nhắn tin, gửi ảnh, gợi ý AI, gọi audio/video, bảo mật với JWT.  

---

## 👤 Thông tin dự án
- **Thời gian**: 3/2025 – 6/2025  
- **Số lượng thành viên**: 1 (Indie Dev)  

---

## 🏗️ Kiến trúc hệ thống
```
Android Client  <---- WebSocket ---->  Spring Boot Server
        ↑                                   ↓
   Firebase AI                        MySQL Database
   FCM Notification                   Spring Security + JWT
   ZegoCloud SDK (Call)
```

---

## 📲 Android (Client)
- **Ngôn ngữ & UI**: Kotlin, XML, Material Design  
- **Design Pattern**: MVVM, Dependency Injection (Hilt)  
- **Networking**: Retrofit  
- **Data & Storage**: Room Database  
- **Notification**: FCM  
- **Asynchronous**: Kotlin Coroutines  
- **Realtime**: WebSocket client  
- **AI Integration**: Firebase AI (gợi ý tin nhắn thông minh trong chat 1-1)  
- **Media**: Glide (load ảnh), ZegoCloud SDK (Audio/Video Call)  

### ✨ Features (Client)
- Chat 1-1 real-time  
- Gửi/nhận ảnh trong hội thoại  
- Gợi ý tin nhắn bằng AI  
- Gửi lời mời kết bạn  
- Cuộc gọi audio/video  
- Lưu cục bộ các đoạn chat  

---

## 🌐 Backend (Server)
- **Framework**: Spring Boot (Java)  
- **Realtime**: WebSocket server  
- **Database**: MySQL  
- **Authentication**: Spring Security + JWT  

### ✨ Features (Server)
- Đăng nhập/đăng ký người dùng với Spring Security + JWT  
- Xử lý lời mời kết bạn  
- Quản lý phiên chat, lưu trữ tin nhắn  
- Tích hợp bảo mật JWT cho API  
- WebSocket cho phép gửi tin nhắn real-time  

---

## 🛠️ Cài đặt & chạy thử

### Yêu cầu
- Android Studio Flamingo trở lên  
- JDK 17+, MySQL 8+  
- Firebase Project (FCM + AI)  
- Tài khoản [ZegoCloud](https://www.zegocloud.com/) để gọi video/audio  

### Clone repo
```bash
git clone https://github.com/minhdan190904/echochat.git
```

### Client
- Mở project `Android` trong Android Studio  
- Thêm file `google-services.json` vào `app/`  

### Server
```bash
cd echochat-server
./mvnw spring-boot:run
```

---

## 📸 Screenshots
*(Kéo ảnh vào phần Issues của GitHub → copy link → dán vào đây)*  

Ví dụ:  
```md
<img src="https://user-images.githubusercontent.com/12345678/abcdef.png" width="300"/>
```

---

## 📚 Công nghệ chính
- **Android**: Kotlin, Jetpack, Retrofit, Room, Coroutines  
- **Backend**: Spring Boot, MySQL, WebSocket, Spring Security + JWT  
- **AI**: Firebase AI  
- **Media/Call**: ZegoCloud SDK  

---

## 🚀 Roadmap
- [ ] Chat nhóm (Group chat)  
- [ ] Dark Mode  
- [ ] Triển khai Docker cho server  
- [ ] CI/CD pipeline (GitHub Actions)  

---

## 👨‍💻 Tác giả
- **Nguyễn Minh Đan** – *Indie Android Developer*  
