/*
 * Copyright (c) 2017 Nicholas van Dyke. All rights reserved.
 */

package com.vandyke.sia.util

import android.arch.lifecycle.LifecycleOwner
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.LiveDataReactiveStreams
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

fun Completable.io() = this.subscribeOn(Schedulers.io())!!

fun <T> Single<T>.io() = this.subscribeOn(Schedulers.io())!!

fun <T> Flowable<T>.io() = this.subscribeOn(Schedulers.io())!!

fun <T> Observable<T>.io() = this.subscribeOn(Schedulers.io())!!


fun Completable.main() = this.observeOn(AndroidSchedulers.mainThread())!!

fun <T> Single<T>.main() = this.observeOn(AndroidSchedulers.mainThread())!!

fun <T> Flowable<T>.main() = this.observeOn(AndroidSchedulers.mainThread())!!

fun <T> Observable<T>.main() = this.observeOn(AndroidSchedulers.mainThread())!!


fun <T> LiveData<T>.toFlowable(lifecycleOwner: LifecycleOwner) : Flowable<T> =
        Flowable.fromPublisher(LiveDataReactiveStreams.toPublisher(lifecycleOwner, this))

fun <T> Flowable<T>.toLiveData(): LiveData<T> = LiveDataReactiveStreams.fromPublisher(this)


fun NonNullLiveData<Int>.increment() {
    this.value = this.value + 1
}

fun NonNullLiveData<Int>.decrementZeroMin() {
    if (this.value > 0)
        this.value = this.value - 1
}