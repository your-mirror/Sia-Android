/*
 * Copyright (c) 2017 Nicholas van Dyke. All rights reserved.
 */

package com.vandyke.sia.data.models.wallet

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class SeedsData @JsonCreator constructor(@JsonProperty(value = "primaryseed")
                                              val primaryseed: String = "",
                                              @JsonProperty(value = "addressesremaining")
                                              val addressesremaining: Int = 0,
                                              @JsonProperty(value = "allseeds")
                                              val allseeds: List<String> = listOf())