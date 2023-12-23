package com.ankit.news.utils

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import java.io.Serializable

object FragmentExtensions {

    enum class FragmentStackMethod { ADD, REPLACE }

    fun FragmentManager.openFragment(
        containerId: Int,
        fragment: Fragment,
        fragmentStackMethod: FragmentStackMethod = FragmentStackMethod.ADD,
        addToBackStack: Boolean = false,
        tag: String? = null
    ) {
        beginTransaction().apply {
            when (fragmentStackMethod) {
                FragmentStackMethod.ADD -> add(containerId, fragment, tag)
                FragmentStackMethod.REPLACE -> replace(containerId, fragment, tag)
            }
            if (addToBackStack){
                addToBackStack(tag)
            }
            commit()
        }

    }

    @Suppress("UNCHECKED_CAST", "DEPRECATION")
    fun <T: Serializable?> Bundle.getSerializableObject(key: String, mClass: Class<T>): T? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) getSerializable(key, mClass)
        else getSerializable(key) as? T
    }
}