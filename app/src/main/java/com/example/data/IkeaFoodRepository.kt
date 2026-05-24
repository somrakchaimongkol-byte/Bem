package com.example.data

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class IkeaFoodRepository(private val dao: IkeaFoodDao) {

    val allProducts: Flow<List<ProductEntity>> = dao.getAllProducts()
    val allLocations: Flow<List<LocationEntity>> = dao.getAllLocations()

    suspend fun insertProduct(product: ProductEntity) {
        dao.insertProduct(product)
    }

    suspend fun deleteProductBySku(sku: String) {
        dao.deleteProductBySku(sku)
    }

    suspend fun insertLocation(locationName: String) {
        dao.insertLocation(LocationEntity(name = locationName))
    }

    suspend fun deleteLocation(locationName: String) {
        dao.deleteLocation(LocationEntity(name = locationName))
    }

    suspend fun updatePhysicalCount(sku: String, physicalCount: Int) {
        val timeStamp = SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(Date())
        dao.updatePhysicalCount(sku, physicalCount, timeStamp)
    }

    suspend fun commitAllCounts() {
        dao.commitAllCounts()
    }

    suspend fun prepopulateIfEmpty() {
        // Read the products from Flow
        val currentProducts = dao.getAllProducts().first()
        val currentLocations = dao.getAllLocations().first()

        if (currentLocations.isEmpty()) {
            val defaultLocs = listOf(
                LocationEntity("A01"),
                LocationEntity("A02"),
                LocationEntity("B01"),
                LocationEntity("Freezer"),
                LocationEntity("Chocolate Zone"),
                LocationEntity("Meatball Zone"),
                LocationEntity("Bakery Shelf")
            )
            dao.insertLocations(defaultLocs)
        }

        if (currentProducts.isEmpty()) {
            val defaultProducts = mutableListOf(
                ProductEntity(
                    sku = "102.345.67",
                    name = "Chocolate Dark 70%",
                    category = "Chocolate",
                    location = "Chocolate Zone",
                    unit = "pcs",
                    systemStock = 150,
                    physicalCount = 150
                ),
                ProductEntity(
                    sku = "204.556.12",
                    name = "Swedish Meatball 1kg",
                    category = "Meatball",
                    location = "Freezer",
                    unit = "pack",
                    systemStock = 85,
                    physicalCount = 85
                ),
                ProductEntity(
                    sku = "301.223.44",
                    name = "Chicken Ball 500g",
                    category = "Meatball",
                    location = "Freezer",
                    unit = "pack",
                    systemStock = 40,
                    physicalCount = 40
                ),
                ProductEntity(
                    sku = "405.667.88",
                    name = "Salmon Fillet",
                    category = "Frozen Food",
                    location = "Freezer",
                    unit = "pack",
                    systemStock = 25,
                    physicalCount = 25
                ),
                ProductEntity(
                    sku = "502.112.33",
                    name = "Hotdog Bun (10pcs)",
                    category = "Bakery",
                    location = "Bakery Shelf",
                    unit = "pack",
                    systemStock = 120,
                    physicalCount = 120
                ),
                ProductEntity(
                    sku = "602.889.01",
                    name = "Lingonberry Jam",
                    category = "Sauce",
                    location = "A01",
                    unit = "pcs",
                    systemStock = 65,
                    physicalCount = 65
                ),
                ProductEntity(
                    sku = "704.551.22",
                    name = "Elderflower Syrup",
                    category = "Beverage",
                    location = "A02",
                    unit = "pcs",
                    systemStock = 30,
                    physicalCount = 30
                ),
                ProductEntity(
                    sku = "102.345.68",
                    name = "Chocolate Milk",
                    category = "Chocolate",
                    location = "Chocolate Zone",
                    unit = "pcs",
                    systemStock = 200,
                    physicalCount = 200
                ),
                ProductEntity(
                    sku = "102.345.69",
                    name = "Chocolate Almond",
                    category = "Chocolate",
                    location = "Chocolate Zone",
                    unit = "pcs",
                    systemStock = 12,
                    physicalCount = 12
                ),
                ProductEntity(
                    sku = "805.112.00",
                    name = "Mustard Mild",
                    category = "Sauce",
                    location = "A01",
                    unit = "pcs",
                    systemStock = 90,
                    physicalCount = 90
                )
            )

            // Add generated list items like the web mockup
            val categories = listOf("Chocolate", "Meatball", "Frozen Food", "Bakery", "Sauce", "Beverage")
            val sampleLocations = listOf("A01", "A02", "B01", "Freezer", "Chocolate Zone", "Meatball Zone", "Bakery Shelf")
            for (i in 1..20) {
                val cat = categories[i % categories.size]
                val loc = sampleLocations[i % sampleLocations.size]
                defaultProducts.add(
                    ProductEntity(
                        sku = "SKU-00$i",
                        name = "Sample Food Product $i",
                        category = cat,
                        location = loc,
                        unit = "pcs",
                        systemStock = (15..95).random(),
                        physicalCount = 0
                    )
                )
            }

            dao.insertProducts(defaultProducts)
        }
    }
}
