package com.example.smarttrade.di

import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.example.smarttrade.util.SMART_TRADE_PREFERENCE
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val encryptedSharedPreferences = module {

    single {
        val masterKeyAlias = MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC)
        EncryptedSharedPreferences.create(
            SMART_TRADE_PREFERENCE,
            masterKeyAlias,
            androidContext(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM)
    }
}
