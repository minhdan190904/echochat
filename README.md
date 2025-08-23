# 📱 EchoChat – Chat App Real-Time  

Ứng dụng chat 1-1 **real-time** với đầy đủ tính năng: nhắn tin, gửi ảnh, gợi ý AI, gọi audio/video, bảo mật với JWT.  

---

## 👤 Thông tin dự án
- **Thời gian**: 3/2025 – 6/2025

---

![Image](https://github.com/user-attachments/assets/fec88a21-981b-4db6-b70c-fdecf605039f)

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

---

## Các tính năng sắp tới
- [ ] Chat nhóm (Group chat)  
- [ ] Dark Mode  

---

## 👨‍💻 Tác giả
- **Trần Minh Đan**
