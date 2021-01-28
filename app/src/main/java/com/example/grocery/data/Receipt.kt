package com.example.grocery.data

import java.util.*

data class Receipt (val id: UUID = UUID.randomUUID(),
                    var date: Date = Date(),
                    var title: String = "")