# WaroongPintar 🍲📱

**WaroongPintar** is a **Point of Sale (POS)** Android tablet application designed for small and medium businesses (SMEs/UMKM).  
It is built using **Kotlin + Jetpack Compose + MVVM + Room** with **Firebase** integration for account and subscription management.

---

## ✨ Key Features (MVP)
- 🔐 **Account & Subscription Management**
  - Login via **Firebase Auth**
  - Flat subscription fee **IDR 199K/month per store** (manual payment, admin-verified)
  - Subscription status stored in **Firestore**
- 🧾 **Transaction Management**
  - Transactions stored locally using **Room Database**
  - Synced to **Firestore** for backup & monitoring
- 📊 **Tablet-Friendly UI**
  - Runs only on tablets (≥ 600dp)
  - **Two-pane layout**: product list + cart/transaction view
- 💾 **Offline-first**
  - Core data (products, stock, transactions) stored locally
  - Firestore is used only for subscription & transaction backup

---

## 🛠️ Tech Stack
- **Android**: Kotlin, Jetpack Compose, MVVM, Navigation, Room
- **Firebase**: 
  - Auth → login/register
  - Firestore → subscription & transactions
- **Architecture**: MVVM + Repository
- **Others**: WorkManager (sync), Coroutines, Flow

---

## 📂 Project Structure
```bash
app/
└── src/main/java/com/nesher/waroongpintar/
├── data/
│ ├── local/ (Room entities, DAO, Database)
│ ├── remote/ (Firestore services, subscription API)
│ └── repository/ (combine local + remote sources)
├── ui/
│ ├── screen/ (Compose screens: login, home, cart)
│ └── navigation/ (NavGraph)
└── common/ (utils, constants)
```

---

### Developers

- Reynaldo Napitupulu - Lead Developer - voldarex.napitupulu@gmail.com
