# 📚 GoLearn – Android LMS

**GoLearn** is a lightweight, Firebase-backed LMS Android app designed for managing classes, assignments, and communication between students and teachers — all in real time.

Built by a solo developer as a **semester project**, it reflects the challenges, creativity, and dedication of one man and one keyboard.

---

## 🚀 Features

### 🧑‍🎓 Student Capabilities:
- View all enrolled classes
- Submit assignments with file upload
- Participate in comment threads
- View personal profile & info

### 👨‍🏫 Teacher Capabilities:
- Create/manage class rooms
- Post assignments with deadlines and file uploads
- View student submissions and assign grades
- Comment and interact with students in-class

### 🔐 Authentication:
- Email/Password signup
- Google Sign-In integration

---

## 🛠️ Setup Instructions

> ✅ Required: A Firebase project with **Realtime Database**, **Authentication**, and **Storage** enabled.

### 📦 Firebase Setup

1. Create a project at [Firebase Console](https://console.firebase.google.com)
2. Enable:
    - **Email/Password Auth**
    - **Google Sign-In**
    - **Realtime Database** (in test mode for development)
    - **Firebase Storage**
3. Download `google-services.json` from Firebase Console
4. Place it in the `app/` directory

### 🧰 Local Setup

```bash
git clone https://github.com/your-username/GoLearn.git
