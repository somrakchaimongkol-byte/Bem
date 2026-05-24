package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey val sku: String,
    val name: String,
    val category: String,
    val location: String,
    val unit: String,
    val systemStock: Int,
    val physicalCount: Int,
    val lastCountTime: String? = null
)
