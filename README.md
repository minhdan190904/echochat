# 📱 EchoChat – Chat App Real-Time  

Ứng dụng chat 1-1 **real-time** với đầy đủ tính năng: nhắn tin, gửi ảnh, gợi ý AI, gọi audio/video, bảo mật với JWT.  

---

## 👤 Thông tin dự án
- **Thời gian**: 3/2025 – 6/2025

---

## 🖼️ Screenshots

<div align="center">
 <!-- Row 4 -->
  <img src="https://github.com/user-attachments/assets/f10f9054-574d-48a0-a97c-f6135e0d313f" width="30%" style="max-width:100%; height:auto;" alt="Screenshot 10">
  <img src="https://github.com/user-attachments/assets/7ac9de8d-2ff7-4a28-9dde-1033d8f48369" width="30%" style="max-width:100%; height:auto;" alt="Screenshot 11">
  <br/>
  <!-- Row 1 -->
  <img src="https://github.com/user-attachments/assets/fec88a21-981b-4db6-b70c-fdecf605039f" width="30%" style="max-width:100%; height:auto;" alt="Screenshot 1">
  <img src="https://github.com/user-attachments/assets/aa337eba-9864-43f6-bcf1-8ec0ff9b5649" width="30%" style="max-width:100%; height:auto;" alt="Screenshot 2">
  <img src="https://github.com/user-attachments/assets/6e8af0ed-b768-4137-adb2-9165cbe2379a" width="30%" style="max-width:100%; height:auto;" alt="Screenshot 3">
  <br/>
  <!-- Row 2 -->
  <img src="https://github.com/user-attachments/assets/6f1e6def-d27b-4610-abbb-e5b3045fb2db" width="30%" style="max-width:100%; height:auto;" alt="Screenshot 4">
  <img src="https://github.com/user-attachments/assets/f21c5edc-79b1-465d-ba43-8be46c06dae0" width="30%" style="max-width:100%; height:auto;" alt="Screenshot 5">
  <img src="https://github.com/user-attachments/assets/d7988748-e03e-4446-9b51-564dbb94a631" width="30%" style="max-width:100%; height:auto;" alt="Screenshot 6">
  <br/>
  <!-- Row 3 -->
  <img src="https://github.com/user-attachments/assets/f9941a93-364c-448e-b0cc-46e018ef7240" width="30%" style="max-width:100%; height:auto;" alt="Screenshot 7">
  <img src="https://github.com/user-attachments/assets/07a5af20-ca6c-4d32-b467-e67a51d8e7ba" width="30%" style="max-width:100%; height:auto;" alt="Screenshot 8">
  <img src="https://github.com/user-attachments/assets/ded31325-fa2c-4a4f-8cbc-1e394e621793" width="30%" style="max-width:100%; height:auto;" alt="Screenshot 9">
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
