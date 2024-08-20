<div align="center">

# BonApp: Your Ultimate Recipe Companion 🍳📱

</div>

<div align="center">
  <h2><a href="https://youtu.be/NTjJfiks--c">🎥 App Video Demonstration 🎥</a></h2>
</div>

<div align="center">

  ![bonapp_icon-playstore](https://github.com/user-attachments/assets/51d3cb97-a773-4bf0-8c22-67947f194123)

</div>

## 🌟 Overview

BonApp is an Android application written in Kotlin. The app provides a platform for people to discover, create, and share different recipes. The app is built using Android Compose in order to create a dynamic UI.

## 🚀 Features and Implementation Details

**🛡️ User Authentication**
   - Implemented using Firebase Authentication
   - Supports email/password and Google Sign-In

**🧾 Recipe Management**
   - Create & Upload recipes through a dynamic form
   - Edit or Delete your uploaded recipes
   - Save recipes to Favorites or Planned lists
   - Discover new recipes through feeds

**🔍 Smart Recipe Search**
   - Advanced search functionality with multiple parameters combining tag selection and user input
   - Implemented using Firestore queries and local Room database queries

👤 **User Interactions**
   - Follow users
   - Save, comment, review, and rate functionality for recipes
   - Implemented using Firestore to manage user relationships and interactions

📷 **Image Handling**
   - Upload and display of recipe images
   - Utilizes Firebase Storage for image storage
   - Implements Coil for efficient image loading and caching

📴 **Offline Capabilities**
   - Local caching of recipes and user data
   - Implemented using Room database for seamless offline experience

## 🌟 Implementation Overview
  - **Clean Architecture**: Separated concerns into distinct layers (UI, domain, and data).
  - **Dependency Injection**: BonApp utilizes Hilt for efficient dependency injection, promoting loose coupling and enhancing testability.
  - **Caching & Offline Support**: Room database for local data caching, enabling offline access to previously loaded recipes and improving the overall user experience.
  - **Modular Architecture**: Allows for easy extension and addition of new features without impacting existing functionality.


## 🛠 Tech Stack

- **🧠 Kotlin**: For robust and concise code
- **🏗 Jetpack Compose**: For modern, declarative UI
- **🔥 Firebase**: For real-time database and authentication
- **🗄 Room**: For efficient local data storage
- **🌐 Coroutines & Flow**: For seamless asynchronous operations
- **💉 Hilt**: For dependency injection
- **🖼 Coil**: For efficient image loading


## 📸 Screenshots

<div align="center">
  
<img src="https://github.com/user-attachments/assets/e7b69f65-0fe6-41df-9675-347ec0a64f80" width="250">
<img src="https://github.com/user-attachments/assets/b67ff98b-58b5-47a7-8bfb-48ddfcb9c298" width="250">

</div>

## 🙏 Acknowledgements

- [Firebase](https://firebase.google.com/)
- [Jetpack Compose](https://developer.android.com/jetpack/compose)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)
- [Hilt](https://dagger.dev/hilt/)
- [Coil](https://coil-kt.github.io/coil/)

---

