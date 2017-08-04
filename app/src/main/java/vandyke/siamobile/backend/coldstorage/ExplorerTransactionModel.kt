package vandyke.siamobile.backend.coldstorage

data class ExplorerTransactionModel(val id: String = "",
                                    val height: Long = 0,
                                    val rawtransaction: RawTransactionModel = RawTransactionModel(),
                                    val siacoininputoutputs: ArrayList<SiacoinOutputModel> = ArrayList())