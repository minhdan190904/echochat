# 📱 EchoChat – Chat App Real-Time  

Ứng dụng chat 1-1 **real-time** với đầy đủ tính năng: nhắn tin, gửi ảnh, gợi ý AI, gọi audio/video, bảo mật với JWT.  

---

## 👤 Thông tin dự án
- **Thời gian**: 3/2025 – 6/2025

---

## 🖼️ Screenshots (3 ảnh mỗi dòng)

<!-- Gợi ý: đặt ảnh vào thư mục /assets/screenshots và thay src bên dưới.
     style="max-width:100%; height:auto" giúp responsive, width="30%" để mỗi dòng 3 ảnh. -->

<div align="center">
  <!-- Row 1 -->
  <img src="https://github.com/user-attachments/assets/fec88a21-981b-4db6-b70c-fdecf605039f" width="30%" style="max-width:100%; height:auto;" alt="Chat list">
  <img src="assets/screenshots/screenshot_02.png" width="30%" style="max-width:100%; height:auto;" alt="Chat detail">
  <img src="assets/screenshots/screenshot_03.png" width="30%" style="max-width:100%; height:auto;" alt="Send image">
  <br/>
  <!-- Row 2 -->
  <img src="assets/screenshots/screenshot_04.png" width="30%" style="max-width:100%; height:auto;" alt="AI suggestion">
  <img src="assets/screenshots/screenshot_05.png" width="30%" style="max-width:100%; height:auto;" alt="Voice call">
  <img src="assets/screenshots/screenshot_06.png" width="30%" style="max-width:100%; height:auto;" alt="Video call">
  <br/>
  <!-- Row 3 -->
  <img src="assets/screenshots/screenshot_07.png" width="30%" style="max-width:100%; height:auto;" alt="Contacts">
  <img src="assets/screenshots/screenshot_08.png" width="30%" style="max-width:100%; height:auto;" alt="Requests">
  <img src="assets/screenshots/screenshot_09.png" width="30%" style="max-width:100%; height:auto;" alt="Settings">
</div>

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

---

## Các tính năng sắp tới
- [ ] Chat nhóm (Group chat)  
- [ ] Dark Mode  

---

## 👨‍💻 Tác giả
- **Trần Minh Đan**
