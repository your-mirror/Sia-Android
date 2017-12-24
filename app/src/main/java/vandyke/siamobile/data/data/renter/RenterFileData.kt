/*
 * Copyright (c) 2017 Nicholas van Dyke. All rights reserved.
 */

package vandyke.siamobile.data.data.renter

import vandyke.siamobile.data.local.Node
import java.math.BigDecimal

data class RenterFileData(val siapath: String = "",
                val localpath: String = "",
                val filesize: BigDecimal = BigDecimal.ZERO, // bytes
                val available: Boolean = false,
                val renewing: Boolean = false,
                val redundancy: Int = 0,
                val uploadedbytes: Long = 0,
                val uploadprogress: Int = 0,
                val expiration: Long = 0) : Node(siapath)