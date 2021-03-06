/*
 * Copyright (c) 2017 Nicholas van Dyke. All rights reserved.
 */

package com.vandyke.sia.data.remote

import com.vandyke.sia.data.models.consensus.ConsensusData
import com.vandyke.sia.data.models.gateway.GatewayData
import com.vandyke.sia.data.models.gateway.GatewayPeerData
import com.vandyke.sia.data.models.renter.*
import com.vandyke.sia.data.models.txpool.FeeData
import com.vandyke.sia.data.models.wallet.*
import com.vandyke.sia.util.HASTINGS_PER_SC
import com.vandyke.sia.util.UNCONFIRMED_TX_TIMESTAMP
import io.reactivex.Completable
import io.reactivex.Single
import java.math.BigDecimal
import java.util.concurrent.atomic.AtomicInteger

/** This class attempts to simulate the API endpoints and internal behavior of the Sia node.
 * It's far from exact, but enough that it can be used as a replacement when testing. */
class MockSiaApi : SiaApiInterface {
    // obviously using a nonce when setting up internal values won't give very reproducible tests. Should do some other way.
    // maybe throw in some real data or something
    // maybe have some factory functions that initialize to often-used values, like with a wallet already created
    private val counter = AtomicInteger()
    private val nonce
        get() = counter.getAndIncrement()
    /* the fields are left public so that they can be modified for testing particular things if needed */
    var password = ""
    /* wallet stuff */
    var unlocked = false
    var encrypted = false
    var rescanning = false
    var confirmedSiacoinBalance = BigDecimal("100") * HASTINGS_PER_SC
    var unconfirmedIncomingSiacoins = BigDecimal("20") * HASTINGS_PER_SC // should use txs to calculate these SC values
    var unconfirmedOutgoingSiacoins = BigDecimal("5") * HASTINGS_PER_SC
    var dustThreshold = BigDecimal("100")
    var siacoinClaimBalance = BigDecimal.ZERO
    var siafundBalance = 0
    var seed = ""
    var addresses = listOf("address1", "address2", "address3")
    var confirmedTxs: MutableList<TransactionData> = MutableList(7, { index ->
        val inputs = listOf(TransactionInputData(walletaddress = nonce % 2 == 0, value = BigDecimal(nonce * 2) * HASTINGS_PER_SC))
        val outputs = listOf(TransactionOutputData(walletaddress = nonce % 2 == 1, value = BigDecimal(nonce) * HASTINGS_PER_SC))
        TransactionData(nonce.toString(), BigDecimal(nonce * 10), BigDecimal(nonce * 100), inputs, outputs)
    })
    var unconfirmedTxs: MutableList<TransactionData> = MutableList(2, { index ->
        val inputs = listOf(TransactionInputData(walletaddress = nonce % 2 == 1, value = BigDecimal(nonce) * HASTINGS_PER_SC))
        val outputs = listOf(TransactionOutputData(walletaddress = nonce % 2 == 0, value = BigDecimal(nonce * 3) * HASTINGS_PER_SC))
        TransactionData(nonce.toString(), BigDecimal(nonce * 10), UNCONFIRMED_TX_TIMESTAMP, inputs, outputs)
    })

    private val files = mutableListOf(
            RenterFileData("legos/brick/picture.jpg", "eh", BigDecimal("156743"), true, false, 2.0, 663453, 100, 1235534),
            RenterFileData("legos/brick/manual", "eh", BigDecimal("156743"), true, false, 2.0, 663453, 100, 1235534),
            RenterFileData("legos/brick/blueprint.b", "eh", BigDecimal("156743"), true, false, 2.0, 663453, 100, 1235534),
            RenterFileData("legos/brick/draft.txt", "eh", BigDecimal("156743"), true, false, 2.0, 663453, 100, 1235534),
            RenterFileData("legos/brick/ad.doc", "eh", BigDecimal("156743"), true, false, 2.0, 663453, 100, 1235534),
            RenterFileData("legos/brick/writeup.txt", "eh", BigDecimal("156743"), true, false, 2.0, 663453, 100, 1235534),
            RenterFileData("legos/brick/buyers.db", "eh", BigDecimal("156743"), true, false, 2.0, 663453, 100, 1235534),
            RenterFileData("legos/brick/listing.html", "eh", BigDecimal("156743"), true, false, 2.0, 663453, 100, 1235534),
            RenterFileData("legos/brick/colors.rgb", "eh", BigDecimal("156743"), true, false, 2.0, 663453, 100, 1235534),
            RenterFileData("legos/block/picture.jpg", "eh", BigDecimal("156743"), true, false, 2.0, 663453, 100, 1235534),
            RenterFileData("legos/block/blueprint", "eh", BigDecimal("156743"), true, false, 2.0, 663453, 100, 1235534),
            RenterFileData("legos/block/vector.svg", "eh", BigDecimal("156743"), true, false, 2.0, 663453, 100, 1235534),
            RenterFileData("legos/block/colors.rgb", "eh", BigDecimal("156743"), true, false, 2.0, 663453, 100, 1235534),
            RenterFileData("legos/blue/brick/picture.jpg", "eh", BigDecimal("156743"), true, false, 2.0, 663453, 100, 1235534),
            RenterFileData("my/name/is/nick/and/this/is/my/story.txt", "eh", BigDecimal("156743"), true, false, 2.0, 663453, 100, 1235534)
    )

    override fun daemonStop(): Completable {
        TODO("not implemented")
    }

    override fun wallet(): Single<WalletData> {
        return Single.just(
            WalletData(encrypted, unlocked, rescanning, confirmedSiacoinBalance, unconfirmedOutgoingSiacoins,
                    unconfirmedIncomingSiacoins, siafundBalance, siacoinClaimBalance, dustThreshold))
    }

    override fun walletSiacoins(amount: String, destination: String): Completable {
        return Completable.fromAction {
            val input = TransactionInputData(walletaddress = true, value = BigDecimal(amount))
            val output = TransactionOutputData(walletaddress = false, value = BigDecimal(amount))
            unconfirmedTxs.add(TransactionData(nonce.toString(), BigDecimal(nonce),
                    UNCONFIRMED_TX_TIMESTAMP, listOf(input), listOf(output)))
        }
    }

    override fun walletAddress(): Single<AddressData> {
        return Single.fromCallable {
            checkUnlocked()
            AddressData(addresses[0])
        }
    }

    override fun walletAddresses(): Single<AddressesData> {
        return Single.just(AddressesData(addresses))
    }

    override fun walletSeeds(dictionary: String): Single<SeedsData> {
        return Single.fromCallable {
            checkUnlocked()
            SeedsData(seed)
        }
    }

    override fun walletSweepSeed(dictionary: String, seed: String): Completable {
        TODO("not implemented")
    }

    override fun walletTransactions(startHeight: String, endHeight: String): Single<TransactionsData> {
        return Single.fromCallable {
            if (!encrypted)
                TransactionsData()
            else
                TransactionsData(confirmedTxs, unconfirmedTxs)
        }
    }

    override fun walletInit(password: String, dictionary: String, force: Boolean): Single<WalletInitData> {
        return Single.fromCallable {
            if (!force && encrypted)
                throw ExistingWallet()
            this.password = password
            this.seed = "random testing seed"
            encrypted = true
            WalletInitData(this.seed)
        }
    }

    override fun walletInitSeed(password: String, dictionary: String, seed: String, force: Boolean): Completable {
        return Completable.fromAction {
            if (!force && encrypted)
                throw ExistingWallet()
            this.password = password
            this.seed = seed
            encrypted = true
        }
    }

    override fun walletLock(): Completable {
        return Completable.fromAction {
            checkUnlocked()
            unlocked = false
        }
    }

    override fun walletUnlock(password: String): Completable {
        return Completable.fromAction {
            checkEncrypted()
            checkUnlocked(false)
            checkPassword(password)
            unlocked = true
        }
    }

    override fun walletChangePassword(password: String, newPassword: String): Completable {
        return Completable.fromAction {
            checkPassword(password)
            this.password = newPassword
        }
    }

    override fun getScPrice(url: String): Single<ScValueData> {
        return Single.just(ScValueData(BigDecimal("0.07")))
    }

    override fun renter(): Single<RenterData> {
        TODO("not implemented")
    }

    override fun renter(funds: BigDecimal, hosts: Int, period: Int, renewwindow: Int): Completable {
        TODO("not implemented")
    }

    override fun renterContracts(): Single<ContractsData> {
        TODO("not implemented")
    }

    override fun renterDownloads(): Single<DownloadsData> {
        TODO("not implemented")
    }

    override fun renterFiles(): Single<RenterFilesData> {
        return Single.just(RenterFilesData(files))
    }

    override fun renterPrices(): Single<PricesData> {
        TODO("not implemented")
    }

    override fun renterRename(siapath: String, newSiaPath: String): Completable {
        TODO("not implemented")
    }

    override fun renterDelete(siapath: String): Completable {
        return Completable.fromCallable {
            var removed: RenterFileData? = null
            files.forEach {
                if (it.path == siapath) {
                    removed = it
                    return@forEach
                }
            }
            removed?.let { files.remove(it) }
        }
    }

    override fun renterUpload(siapath: String, source: String, dataPieces: Int, parityPieces: Int): Completable {
        return Completable.fromAction {
            files.add(RenterFileData(siapath, "eh", BigDecimal("156743"), true, false,
                    2.0, 663453, 100, 1235534))
        }
    }

    override fun renterDownload(siapath: String, destination: String): Completable {
        TODO("not implemented")
    }

    override fun renterDownloadAsync(siapath: String, destination: String): Completable {
        TODO("not implemented")
    }

    override fun gateway(): Single<GatewayData> {
        return Single.just(GatewayData("536.623.53.8", listOf(
                GatewayPeerData("68.12.543.30", "1.3.1", true),
                GatewayPeerData("20.64.77.12", "1.3.0", true),
                GatewayPeerData("73.74.12.98", "1.6.1", true),
                GatewayPeerData("90.123.74.23", "1.3.1", true)
        )))
    }

    override fun consensus(): Single<ConsensusData> {
        return Single.just(ConsensusData(false, 135371, nonce.toString(), BigDecimal(nonce)))
    }

    override fun txPoolFee(): Single<FeeData> {
        TODO("not implemented")
    }

    private fun checkPassword(password: String) {
        if (password != this.password)
            throw WalletPasswordIncorrect()
    }

    private fun checkUnlocked(desired: Boolean = true) {
        if (unlocked != desired) {
            if (unlocked)
                throw WalletAlreadyUnlocked()
            else
                throw WalletLocked()
        }
    }

    private fun checkEncrypted(desired: Boolean = true) {
        if (encrypted != desired) {
            if (encrypted)
                throw ExistingWallet()
            else
                throw NoWallet()
        }
    }
}