package com.rdireito.ridelight.common.extension

import android.support.annotation.IdRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentTransaction
import timber.log.Timber

@Suppress("UNCHECKED_CAST")
fun <F : Fragment> FragmentActivity.findFragmentWithType(tag: String): F? {
    return (supportFragmentManager.findFragmentByTag(tag) as F?)
        .also { if (it == null) Timber.e("Fragment not found. TAG: $tag") }
}

fun FragmentManager.commitTransactions(func: FragmentManager.(FragmentTransaction) -> Unit) {
    val transaction = beginTransaction()
    func(transaction)
    transaction.commit()
}

fun FragmentTransaction.attachFragment(fragment: Fragment, @IdRes content: Int, tag: String) {
    if (fragment.isDetached) {
        attach(fragment)
    } else if (!fragment.isAdded) {
        add(content, fragment, tag)
    }
}
