package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.NavigationRailItemDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.IkeaFoodViewModel
import com.example.data.LocationEntity
import com.example.data.ProductEntity
import com.example.ui.theme.IkeaBlue
import com.example.ui.theme.IkeaCobalt
import com.example.ui.theme.IkeaYellow
import com.example.ui.theme.LightBorder
import com.example.ui.theme.LightTextSub
import com.example.ui.theme.StockDanger
import com.example.ui.theme.StockSuccess
import com.example.ui.theme.StockWarning
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun IkeaWmsApp(
    viewModel: IkeaFoodViewModel,
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit
) {
    val context = LocalContext.current
    val currentView by viewModel.currentView.collectAsState()
    val products by viewModel.products.collectAsState()
    val locations by viewModel.locations.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedLocationFilter by viewModel.selectedLocationFilter.collectAsState()
    val selectedCategoryFilter by viewModel.selectedCategoryFilter.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState()

    var showAddProductDialog by remember { mutableStateOf(false) }
    var showAddLocationDialog by remember { mutableStateOf(false) }

    // Floating Barcode Simulator trigger dialog
    var showScanOverlay by remember { mutableStateOf(false) }
    var scannedProductDetails by remember { mutableStateOf<ProductEntity?>(null) }

    // Window size class detection. We can represent screens wider than 720dp as "Expanded"
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isWideScreen = maxWidth > 720.dp
        val scope = rememberCoroutineScope()

        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // IKEA logo badge
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(24.dp))
                                    .background(IkeaYellow)
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "IKEA",
                                    color = IkeaBlue,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Black,
                                    letterSpacing = 0.5.sp
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "Food Inventory",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                                Text(
                                    text = "Warehouse WMS System",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                                )
                            }
                        }
                    },
                    actions = {
                        // Dark mode toggler
                        IconButton(
                            onClick = onToggleTheme,
                            modifier = Modifier.testTag("dark_mode_button")
                        ) {
                            Text(
                                text = if (isDarkTheme) "☀️" else "🌙",
                                fontSize = 20.sp
                            )
                        }

                        // Sync Database option
                        IconButton(
                            onClick = {
                                viewModel.commitAllCounts()
                                scope.launch {
                                    android.widget.Toast.makeText(context, "สต๊อกทั้งหมดถูกซิงค์ข้อมูลแล้ว!", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            },
                            modifier = Modifier.testTag("sync_action_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Sync all counts",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = IkeaBlue,
                        titleContentColor = Color.White,
                        actionIconContentColor = Color.White
                    )
                )
            },
            bottomBar = {
                if (!isWideScreen) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface,
                        tonalElevation = 8.dp,
                        modifier = Modifier.testTag("bottom_nav_bar")
                    ) {
                        NavigationBarItem(
                            selected = currentView == "dashboard",
                            onClick = { viewModel.changeView("dashboard") },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard") },
                            label = { Text(" Dashboard", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = IkeaBlue,
                                selectedTextColor = IkeaBlue,
                                indicatorColor = IkeaYellow
                            )
                        )
                        NavigationBarItem(
                            selected = currentView == "counting",
                            onClick = { viewModel.changeView("counting") },
                            icon = { Icon(Icons.Default.Check, contentDescription = "Counting") },
                            label = { Text("นับสต๊อก", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = IkeaBlue,
                                selectedTextColor = IkeaBlue,
                                indicatorColor = IkeaYellow
                            )
                        )
                        NavigationBarItem(
                            selected = currentView == "inventory",
                            onClick = { viewModel.changeView("inventory") },
                            icon = { Icon(Icons.Default.List, contentDescription = "Inventory") },
                            label = { Text("สินค้า", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = IkeaBlue,
                                selectedTextColor = IkeaBlue,
                                indicatorColor = IkeaYellow
                            )
                        )
                        NavigationBarItem(
                            selected = currentView == "locations",
                            onClick = { viewModel.changeView("locations") },
                            icon = { Icon(Icons.Default.Place, contentDescription = "Locations") },
                            label = { Text("ตำแหน่ง", fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = IkeaBlue,
                                selectedTextColor = IkeaBlue,
                                indicatorColor = IkeaYellow
                            )
                        )
                    }
                }
            },
            floatingActionButton = {
                // Barcode simulation FAB
                FloatingActionButton(
                    onClick = {
                        val allProd = products
                        if (allProd.isNotEmpty()) {
                            val rand = allProd.random()
                            scannedProductDetails = rand
                            showScanOverlay = true
                        } else {
                            // Empty state
                        }
                    },
                    containerColor = IkeaYellow,
                    contentColor = IkeaBlue,
                    modifier = Modifier
                        .padding(bottom = if (isWideScreen) 16.dp else 4.dp)
                        .testTag("scan_simulation_fab")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📷 ", fontSize = 18.sp)
                        Text("จำลองสแกน CSV", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        ) { paddingValues ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                // Sidebar Navigation Rail for Wide Screens (Tablets / Foldables)
                if (isWideScreen) {
                    NavigationRail(
                        containerColor = IkeaBlue,
                        modifier = Modifier
                            .fillMaxHeight()
                            .width(100.dp)
                            .testTag("side_nav_rail"),
                        header = {
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                "WMS",
                                color = IkeaYellow,
                                fontWeight = FontWeight.Black,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    ) {
                        NavigationRailItem(
                            selected = currentView == "dashboard",
                            onClick = { viewModel.changeView("dashboard") },
                            icon = { Icon(Icons.Default.Home, contentDescription = "Dashboard", tint = Color.White) },
                            label = { Text(" Dashboard", fontSize = 12.sp, color = Color.White) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = IkeaBlue,
                                indicatorColor = IkeaYellow
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        NavigationRailItem(
                            selected = currentView == "counting",
                            onClick = { viewModel.changeView("counting") },
                            icon = { Icon(Icons.Default.Check, contentDescription = "Counting", tint = Color.White) },
                            label = { Text("นับสต๊อก", fontSize = 12.sp, color = Color.White) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = IkeaBlue,
                                indicatorColor = IkeaYellow
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        NavigationRailItem(
                            selected = currentView == "inventory",
                            onClick = { viewModel.changeView("inventory") },
                            icon = { Icon(Icons.Default.List, contentDescription = "Inventory", tint = Color.White) },
                            label = { Text("สินค้า", fontSize = 12.sp, color = Color.White) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = IkeaBlue,
                                indicatorColor = IkeaYellow
                            )
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        NavigationRailItem(
                            selected = currentView == "locations",
                            onClick = { viewModel.changeView("locations") },
                            icon = { Icon(Icons.Default.Place, contentDescription = "Locations", tint = Color.White) },
                            label = { Text("คลัง / Locs", fontSize = 12.sp, color = Color.White) },
                            colors = NavigationRailItemDefaults.colors(
                                selectedIconColor = IkeaBlue,
                                indicatorColor = IkeaYellow
                            )
                        )
                    }
                }

                // Main Display Content Screen Switcher
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    when (currentView) {
                        "dashboard" -> DashboardScreen(
                            viewModel = viewModel,
                            products = products,
                            locations = locations
                        )
                        "counting" -> CountingScreen(
                            viewModel = viewModel,
                            products = filteredProducts,
                            distinctLocations = locations.map { it.name },
                            searchQuery = searchQuery,
                            selectedLocationFilter = selectedLocationFilter,
                            selectedCategoryFilter = selectedCategoryFilter,
                            onSave = {
                                viewModel.commitAllCounts()
                                scope.launch {
                                    android.widget.Toast.makeText(context, "บันทึกจำนวนเรียบร้อยแล้ว!", android.widget.Toast.LENGTH_SHORT).show()
                                }
                            }
                        )
                        "inventory" -> InventoryScreen(
                            viewModel = viewModel,
                            products = products,
                            locations = locations.map { it.name },
                            onOpenAddProduct = { showAddProductDialog = true }
                        )
                        "locations" -> LocationsScreen(
                            viewModel = viewModel,
                            locations = locations,
                            products = products,
                            onOpenAddLocation = { showAddLocationDialog = true }
                        )
                    }
                }
            }
        }

        // --- Modals and Dialog Additives ---

        // Scan barcode simulator overlay
        if (showScanOverlay && scannedProductDetails != null) {
            val p = scannedProductDetails!!
            Dialog(onDismissRequest = { showScanOverlay = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "📷 SCAN SIMULATOR SUCCESS",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = IkeaBlue
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        // Barcode placeholder simulation lines
                        Row(horizontalArrangement = Arrangement.Center, modifier = Modifier.fillMaxWidth()) {
                            Text("||||| ||| |||| || | || |||||", fontWeight = FontWeight.Bold, fontSize = 24.sp, letterSpacing = 2.sp)
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "ค้นพบ SKU: ${p.sku}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = p.name,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "ตำแหน่งจัดเก็บ: ${p.location} | หมวดหมู่: ${p.category}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Button(
                            onClick = {
                                viewModel.updateSearchQuery(p.sku)
                                viewModel.changeView("counting")
                                showScanOverlay = false
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IkeaBlue, contentColor = Color.White),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("นำทางไปยังการสแกนสต๊อก", fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = { showScanOverlay = false },
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, contentColor = IkeaBlue),
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("ยกเลิก")
                        }
                    }
                }
            }
        }

        // Add Product Dialog Modal
        if (showAddProductDialog) {
            var skuInput by remember { mutableStateOf("") }
            var nameInput by remember { mutableStateOf("") }
            var categoryInput by remember { mutableStateOf("Meatball") }
            var locationInput by remember { mutableStateOf(locations.firstOrNull()?.name ?: "Freezer") }
            var unitInput by remember { mutableStateOf("pcs") }
            var stockInput by remember { mutableStateOf("") }

            val categories = listOf("Chocolate", "Meatball", "Frozen Food", "Bakery", "Sauce", "Beverage")

            Dialog(onDismissRequest = { showAddProductDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .heightIn(max = 580.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "เพิ่มสินค้าใหม่",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = IkeaBlue
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f, fill = false)
                        ) {
                            item {
                                OutlinedTextField(
                                    value = skuInput,
                                    onValueChange = { skuInput = it },
                                    label = { Text("SKU (รหัสสินค้า เช่น 102.345.67)") },
                                    modifier = Modifier.fillMaxWidth().testTag("add_product_sku_field"),
                                    singleLine = true
                                )
                            }
                            item {
                                OutlinedTextField(
                                    value = nameInput,
                                    onValueChange = { nameInput = it },
                                    label = { Text("ชื่อสินค้า") },
                                    modifier = Modifier.fillMaxWidth().testTag("add_product_name_field"),
                                    singleLine = true
                                )
                            }
                            item {
                                // Category drop down selection simulation style
                                var catExpanded by remember { mutableStateOf(false) }
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = categoryInput,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("หมวดหมู่") },
                                        modifier = Modifier.fillMaxWidth().clickable { catExpanded = true },
                                        trailingIcon = {
                                            IconButton(onClick = { catExpanded = true }) {
                                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Expand Category")
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = catExpanded,
                                        onDismissRequest = { catExpanded = false }
                                    ) {
                                        categories.forEach { cat ->
                                            DropdownMenuItem(
                                                text = { Text(cat) },
                                                onClick = {
                                                    categoryInput = cat
                                                    catExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            item {
                                // Location drop down selection simulation style
                                var locExpanded by remember { mutableStateOf(false) }
                                Box(modifier = Modifier.fillMaxWidth()) {
                                    OutlinedTextField(
                                        value = locationInput,
                                        onValueChange = {},
                                        readOnly = true,
                                        label = { Text("ตำแหน่งจัดเก็บ") },
                                        modifier = Modifier.fillMaxWidth().clickable { locExpanded = true },
                                        trailingIcon = {
                                            IconButton(onClick = { locExpanded = true }) {
                                                Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Expand Location")
                                            }
                                        }
                                    )
                                    DropdownMenu(
                                        expanded = locExpanded,
                                        onDismissRequest = { locExpanded = false }
                                    ) {
                                        locations.forEach { loc ->
                                            DropdownMenuItem(
                                                text = { Text(loc.name) },
                                                onClick = {
                                                    locationInput = loc.name
                                                    locExpanded = false
                                                }
                                            )
                                        }
                                    }
                                }
                            }
                            item {
                                OutlinedTextField(
                                    value = unitInput,
                                    onValueChange = { unitInput = it },
                                    label = { Text("หน่วยนับ เช่น pcs, pack, case") },
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                            item {
                                OutlinedTextField(
                                    value = stockInput,
                                    onValueChange = { stockInput = it },
                                    label = { Text("จำนวนเริ่มต้นในระบบ") },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    modifier = Modifier.fillMaxWidth(),
                                    singleLine = true
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { showAddProductDialog = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                            ) {
                                Text("ยกเลิก")
                            }

                            Button(
                                onClick = {
                                    if (skuInput.isNotBlank() && nameInput.isNotBlank()) {
                                        val stockVal = stockInput.toIntOrNull() ?: 1
                                        viewModel.addProduct(
                                            sku = skuInput,
                                            name = nameInput,
                                            category = categoryInput,
                                            location = locationInput,
                                            unit = unitInput,
                                            systemStock = stockVal
                                        )
                                        showAddProductDialog = false
                                    } else {
                                        scope.launch {
                                            android.widget.Toast.makeText(context, "กรุณากรอกข้อมูลให้ครบถ้วน!", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = IkeaBlue,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("save_product_btn")
                            ) {
                                Text("บันทึก")
                            }
                        }
                    }
                }
            }
        }

        // Add Location Dialog
        if (showAddLocationDialog) {
            var locNameInput by remember { mutableStateOf("") }
            Dialog(onDismissRequest = { showAddLocationDialog = false }) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp)
                    ) {
                        Text(
                            text = "เพิ่มตำแหน่งใหม่",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = IkeaBlue
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        OutlinedTextField(
                            value = locNameInput,
                            onValueChange = { locNameInput = it },
                            label = { Text("ชื่อโซนจัดเก็บ (ตัวอย่าง: C01, Freezer 2)") },
                            modifier = Modifier.fillMaxWidth().testTag("add_location_input_field")
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Button(
                                onClick = { showAddLocationDialog = false },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                            ) {
                                Text("ยกเลิก")
                            }
                            Button(
                                onClick = {
                                    if (locNameInput.isNotBlank()) {
                                        viewModel.addLocation(locNameInput)
                                        showAddLocationDialog = false
                                    } else {
                                        scope.launch {
                                            android.widget.Toast.makeText(context, "กรุณากรอกชื่อตำแหน่ง!", android.widget.Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = IkeaBlue,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("save_location_btn")
                            ) {
                                Text("บันทึก")
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- SUB SCREEN COMPOSABLES ---

@Composable
fun DashboardScreen(
    viewModel: IkeaFoodViewModel,
    products: List<ProductEntity>,
    locations: List<LocationEntity>
) {
    val context = LocalContext.current
    val totalItems = products.size
    val totalLocations = locations.size

    val lowStockCount = products.count { it.systemStock < 20 }
    val countedCount = products.count { it.lastCountTime != null }
    val percentageCounted = if (totalItems > 0) (countedCount * 100) / totalItems else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Dashboard Title and Timestamps
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Dashboard สรุปภาพรวม",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "นับสต๊อก: " + SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date()),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }
        Spacer(modifier = Modifier.height(16.dp))

        // Widget Stats Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(title = "สินค้าทั้งหมด", value = "$totalItems Items", color = IkeaBlue, modifier = Modifier.weight(1f))
            StatCard(title = "คลัง (Loc)", value = "$totalLocations โซน", color = IkeaCobalt, modifier = Modifier.weight(1f))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatCard(title = "นับแล้ววันนี้", value = "$percentageCounted%", color = StockSuccess, modifier = Modifier.weight(1f))
            StatCard(title = "สินค้าสต๊อกต่ำ", value = "$lowStockCount รายการ", color = StockDanger, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Category proportion layout
        Column(modifier = Modifier.weight(1f)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "สัดส่วนสินค้าตามหมวดหมู่",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Arc Doughnut drawing Canvas
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        CategoryDoughnutChart(products = products)
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    // Legend description
                    Column(
                        modifier = Modifier.weight(1.2f),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        val categories = listOf("Chocolate", "Meatball", "Frozen Food", "Bakery", "Sauce", "Beverage")
                        val colors = listOf(IkeaBlue, IkeaYellow, IkeaCobalt, StockSuccess, StockWarning, Color.Magenta)

                        categories.forEachIndexed { index, cat ->
                            val count = products.count { it.category == cat }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(colors[index])
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "$cat: $count ชิ้น",
                                    fontSize = 11.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Products to action lists (Top 5 Low Stock)
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "สินค้าที่ต้องเติมด่วน (Top 5 ต่ำสุด)",
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onBackground
            )
            Spacer(modifier = Modifier.height(6.dp))
            ElevatedCard(
                colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val topLowStock = products.sortedBy { it.systemStock }.take(5)
                    if (topLowStock.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("ไม่มีสินค้าสต๊อกต่ำในระบบ 🎉", color = StockSuccess, fontSize = 14.sp)
                            }
                        }
                    } else {
                        items(topLowStock, key = { it.sku }) { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(item.name, fontWeight = FontWeight.SemiBold, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                    Text("SKU: ${item.sku} | โซน: ${item.location}", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(StockDanger.copy(alpha = 0.15f))
                                        .padding(horizontal = 8.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = "คงเหลือ ${item.systemStock} ${item.unit}",
                                        color = StockDanger,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = modifier.height(76.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Text(title, fontSize = 11.sp, color = LightTextSub, fontWeight = FontWeight.Medium)
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                fontSize = 18.sp,
                color = color,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun CategoryDoughnutChart(products: List<ProductEntity>) {
    val categories = listOf("Chocolate", "Meatball", "Frozen Food", "Bakery", "Sauce", "Beverage")
    val colors = listOf(IkeaBlue, IkeaYellow, IkeaCobalt, StockSuccess, StockWarning, Color.Magenta)

    val total = products.size
    val counts = categories.map { cat -> products.count { it.category == cat } }

    Canvas(modifier = Modifier.fillMaxSize()) {
        val strokeWidth = 14.dp.toPx()
        val diameter = size.minDimension - strokeWidth
        val topLeftOffset = Offset(
            (size.width - diameter) / 2f,
            (size.height - diameter) / 2f
        )
        val arcSize = Size(diameter, diameter)

        if (total == 0) {
            // No items, draw single slate circle
            drawArc(
                color = Color.LightGray,
                startAngle = 0f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = topLeftOffset,
                size = arcSize,
                style = Stroke(width = strokeWidth)
            )
        } else {
            var currentStartAngle = -90f
            counts.forEachIndexed { index, count ->
                if (count > 0) {
                    val sweep = (count.toFloat() / total.toFloat()) * 360f
                    drawArc(
                        color = colors[index],
                        startAngle = currentStartAngle,
                        sweepAngle = sweep,
                        useCenter = false,
                        topLeft = topLeftOffset,
                        size = arcSize,
                        style = Stroke(width = strokeWidth)
                    )
                    currentStartAngle += sweep
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CountingScreen(
    viewModel: IkeaFoodViewModel,
    products: List<ProductEntity>,
    distinctLocations: List<String>,
    searchQuery: String,
    selectedLocationFilter: String?,
    selectedCategoryFilter: String?,
    onSave: () -> Unit
) {
    val context = LocalContext.current
    val categories = listOf("Chocolate", "Meatball", "Frozen Food", "Bakery", "Sauce", "Beverage")

    // Filter selectors state expansion
    var locExpanded by remember { mutableStateOf(false) }
    var catExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "เริ่มตรวจสอบและนับจริง",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            // Sync Save counting and export options
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = { viewModel.shareCsvContent(context) },
                    colors = ButtonDefaults.buttonColors(containerColor = IkeaYellow, contentColor = IkeaBlue),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(40.dp).testTag("export_csv_btn")
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Export CSV", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("ส่งออก CSV", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onSave,
                    colors = ButtonDefaults.buttonColors(containerColor = IkeaBlue, contentColor = Color.White),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(40.dp).testTag("save_all_btn")
                ) {
                    Icon(Icons.Default.Check, contentDescription = "Save counts", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("บันทึกสต๊อก", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
        Spacer(modifier = Modifier.height(12.dp))

        // Search inputs WMS Filters Row
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            placeholder = { Text("ค้นหารหัสสินค้า SKU หรือชื่อสินค้า...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search SKU") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear search")
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = IkeaBlue,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("search_count_input"),
            singleLine = true
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Select Filters for Location and Category
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Location spinner selection
            Box(modifier = Modifier.weight(1f)) {
                Button(
                    onClick = { locExpanded = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = selectedLocationFilter ?: "ทุกตำแหน่ง (All)",
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                DropdownMenu(
                    expanded = locExpanded,
                    onDismissRequest = { locExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("ทุกตำแหน่ง") },
                        onClick = {
                            viewModel.selectLocationFilter(null)
                            locExpanded = false
                        }
                    )
                    distinctLocations.forEach { loc ->
                        DropdownMenuItem(
                            text = { Text(loc) },
                            onClick = {
                                viewModel.selectLocationFilter(loc)
                                locExpanded = false
                            }
                        )
                    }
                }
            }

            // Category filter selection
            Box(modifier = Modifier.weight(1f)) {
                Button(
                    onClick = { catExpanded = true },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.onSurface
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
                ) {
                    Text(
                        text = selectedCategoryFilter ?: "ทุกหมวดหมู่",
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                DropdownMenu(
                    expanded = catExpanded,
                    onDismissRequest = { catExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("ทุกหมวดหมู่") },
                        onClick = {
                            viewModel.selectCategoryFilter(null)
                            catExpanded = false
                        }
                    )
                    categories.forEach { cat ->
                        DropdownMenuItem(
                            text = { Text(cat) },
                            onClick = {
                                viewModel.selectCategoryFilter(cat)
                                catExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // List Header items
        ElevatedCard(
            colors = CardDefaults.elevatedCardColors(
                containerColor = IkeaBlue.copy(alpha = 0.05f)
            ),
            shape = RoundedCornerShape(4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("รายละเอียดสินค้า / SKU", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = IkeaBlue, modifier = Modifier.weight(1.8f))
                Text("ระบบ", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = IkeaBlue, modifier = Modifier.weight(0.7f), textAlign = TextAlign.Center)
                Text("นับจริง(ค้าง)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = IkeaBlue, modifier = Modifier.weight(1.5f), textAlign = TextAlign.Center)
                Text("ต่าง(+/-)", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = IkeaBlue, modifier = Modifier.weight(0.7f), textAlign = TextAlign.End)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Large scrollable lazy list
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (products.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("ไม่พบสินค้าตามตัวเลือกของคุณ 🔎", color = LightTextSub, textAlign = TextAlign.Center)
                    }
                }
            } else {
                items(products, key = { it.sku }) { item ->
                    CountingRow(
                        product = item,
                        onIncrement = { viewModel.incrementPhysicalCount(item) },
                        onDecrement = { viewModel.decrementPhysicalCount(item) },
                        onCountChange = { countStr ->
                            val count = countStr.toIntOrNull() ?: 0
                            viewModel.setPhysicalCountManual(item, count)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CountingRow(
    product: ProductEntity,
    onIncrement: () -> Unit,
    onDecrement: () -> Unit,
    onCountChange: (String) -> Unit
) {
    val diff = product.physicalCount - product.systemStock
    val diffColor = when {
        diff < 0 -> StockDanger
        diff > 0 -> StockSuccess
        else -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    }

    val diffText = when {
        diff > 0 -> "+$diff"
        else -> "$diff"
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("counting_item_row_${product.sku}"),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Info block
                Column(modifier = Modifier.weight(1.8f)) {
                    Text(
                        text = product.name,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(IkeaBlue)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(product.sku, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(IkeaCobalt.copy(alpha = 0.12f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(product.location, color = IkeaBlue, fontWeight = FontWeight.SemiBold, fontSize = 10.sp)
                        }
                    }
                }

                // System stock
                Text(
                    text = "${product.systemStock}\n${product.unit}",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    modifier = Modifier.weight(0.7f),
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )

                // Plus minus interactive counters
                Row(
                    modifier = Modifier.weight(1.5f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(IkeaBlue)
                            .clickable { onDecrement() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("-", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    }

                    OutlinedTextField(
                        value = product.physicalCount.toString(),
                        onValueChange = onCountChange,
                        modifier = Modifier
                            .width(62.dp)
                            .height(48.dp)
                            .padding(horizontal = 4.dp),
                        textStyle = MaterialTheme.typography.bodySmall.copy(
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        ),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                            focusedBorderColor = IkeaBlue,
                            unfocusedBorderColor = LightBorder
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(IkeaBlue)
                            .clickable { onIncrement() },
                        contentAlignment = Alignment.Center
                    ) {
                        Text("+", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    }
                }

                // Diff Result
                Text(
                    text = diffText,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = diffColor,
                    modifier = Modifier.weight(0.7f),
                    textAlign = TextAlign.End
                )
            }

            Spacer(modifier = Modifier.height(6.dp))
            DividerSpacer()
            Spacer(modifier = Modifier.height(4.dp))

            // Last count indicator text with timestamp
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (product.lastCountTime != null) "นับล่าสุดเมื่อ: ${product.lastCountTime}" else "ยังไม่ได้ตรวจสอบนับสต๊อก",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
                Text(
                    text = "หน่วย: ${product.unit}",
                    fontSize = 10.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun DividerSpacer() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
    )
}

@Composable
fun InventoryScreen(
    viewModel: IkeaFoodViewModel,
    products: List<ProductEntity>,
    locations: List<String>,
    onOpenAddProduct: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "รายการสินค้าในฐานข้อมูล",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Button(
                onClick = onOpenAddProduct,
                colors = ButtonDefaults.buttonColors(containerColor = IkeaBlue, contentColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(40.dp).testTag("add_product_action_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("เพิ่มสินค้า", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (products.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxSize().padding(40.dp), contentAlignment = Alignment.Center) {
                        Text("คลังสินค้าว่างเปล่า กรุณาเริ่มปุ่มเพิ่มสินค้า ด้านบน", color = LightTextSub, textAlign = TextAlign.Center)
                    }
                }
            } else {
                items(products, key = { it.sku }) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("inventory_item_row_${item.sku}"),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "SKU: ${item.sku}",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = IkeaBlue
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "•  หมวดหมู่: ${item.category}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(IkeaCobalt.copy(alpha = 0.1f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "ตำแหน่ง: ${item.location}",
                                            fontSize = 9.sp,
                                            color = IkeaBlue,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "สต๊อกระบบ: ${item.systemStock} ${item.unit}",
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                    )
                                }
                            }

                            // Delete Action
                            IconButton(
                                onClick = { viewModel.deleteProduct(item.sku) },
                                modifier = Modifier.testTag("delete_product_button_${item.sku}")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = "Delete product",
                                    tint = StockDanger
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun LocationsScreen(
    viewModel: IkeaFoodViewModel,
    locations: List<LocationEntity>,
    products: List<ProductEntity>,
    onOpenAddLocation: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "ตำแหน่งจัดเก็บทั้งหมด (Locations)",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
            )

            Button(
                onClick = onOpenAddLocation,
                colors = ButtonDefaults.buttonColors(containerColor = IkeaBlue, contentColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.height(40.dp).testTag("add_location_action_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Location", modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("เพิ่มตำแหน่ง", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(locations, key = { it.name }) { item ->
                val associatedCount = products.count { it.location == item.name }
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clickable {
                            // Quick Filter search by this location
                            viewModel.selectLocationFilter(item.name)
                            viewModel.changeView("counting")
                        }
                        .testTag("location_grid_card_${item.name}"),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Box(modifier = Modifier.fillMaxSize().padding(12.dp)) {
                        Column {
                            Text(
                                item.name,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = IkeaBlue
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "$associatedCount สินค้าในโกดัง",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                            )
                        }

                        // Absolute top-right delete trigger
                        IconButton(
                            onClick = { viewModel.deleteLocation(item.name) },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .size(32.dp)
                                .testTag("delete_location_btn_${item.name}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete location",
                                tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}
