package com.example.bonapp.data.remote

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreSettings

fun initializeFirebase(context: Context) {
    FirebaseApp.initializeApp(context)
}

