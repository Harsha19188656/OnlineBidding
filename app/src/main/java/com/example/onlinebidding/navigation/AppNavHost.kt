package com.example.onlinebidding.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import com.example.onlinebidding.products.LaptopList
import com.example.onlinebidding.products.laptopItems
import com.example.onlinebidding.screens.splash.*
import com.example.onlinebidding.screens.login.*
import com.example.onlinebidding.screens.welcome.Welcome
import com.example.onlinebidding.screens.interest.InterestSelection
import com.example.onlinebidding.screens.dashboard.*
import com.example.onlinebidding.screens.profile.*
import com.example.onlinebidding.screens.trending.TrendingAuctions
import com.example.onlinebidding.screens.flash.FlashAuctions
import com.example.onlinebidding.screens.search.Search
import com.example.onlinebidding.screens.products.*
import com.example.onlinebidding.screens.products.TabletProduct
import com.example.onlinebidding.screens.products.TabletSeller
import com.example.onlinebidding.products.TabletListScreen
import com.example.onlinebidding.ui.viewmodel.AuthViewModel
import com.example.onlinebidding.R

/* ---------------- ROUTES ---------------- */

const val LAPTOP_LIST = "laptop_list"
const val DELL_XPS_15_DETAILS = "dell_xps_15_details"
const val LAPTOP_CREDITS = "laptop_credits"
const val LAPTOP_PAYMENT_METHOD = "laptop_payment_method"
const val LAPTOP_PAYMENT_PROCESS = "laptop_payment_processing"
const val LAPTOP_PAYMENT_SUCCESS = "laptop_payment_success"
const val MOBILE_CREDITS = "mobile_credits"
const val MOBILE_PAYMENT_METHOD = "mobile_payment_method"
const val MOBILE_PAYMENT_PROCESS = "mobile_payment_processing"
const val MOBILE_PAYMENT_SUCCESS = "mobile_payment_success"
const val COMPUTER_CREDITS = "computer_credits"
const val COMPUTER_PAYMENT_METHOD = "computer_payment_method"
const val COMPUTER_PAYMENT_PROCESS = "computer_payment_processing"
const val COMPUTER_PAYMENT_SUCCESS = "computer_payment_success"
const val MONITOR_CREDITS = "monitor_credits"
const val MONITOR_PAYMENT_METHOD = "monitor_payment_method"
const val MONITOR_PAYMENT_PROCESS = "monitor_payment_processing"
const val MONITOR_PAYMENT_SUCCESS = "monitor_payment_success"
const val TABLET_CREDITS = "tablet_credits"
const val TABLET_PAYMENT_METHOD = "tablet_payment_method"
const val TABLET_PAYMENT_PROCESS = "tablet_payment_processing"
const val TABLET_PAYMENT_SUCCESS = "tablet_payment_success"

@Composable
fun AppNavHost() {

    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.uiState.collectAsState()
    val context = LocalContext.current

    var userName by remember { mutableStateOf("User") }
    var userEmail by remember { mutableStateOf("user@email.com") }
    val laptopCredits = remember { mutableStateMapOf<Int, Boolean>() }
    val mobileCredits = remember { mutableStateMapOf<Int, Boolean>() }
    val computerCredits = remember { mutableStateMapOf<Int, Boolean>() }
    val monitorCredits = remember { mutableStateMapOf<Int, Boolean>() }
    val tabletCredits = remember { mutableStateMapOf<Int, Boolean>() }

    LaunchedEffect(authState.token) {
        if (authState.token != null) {
            userEmail = authState.email ?: userEmail
            navController.navigate("create_profile") {
                popUpTo("login") { inclusive = true }
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {

        /* ---------- SPLASH FLOW ---------- */

        composable("splash") {
            SplashScreen {
                navController.navigate("get_started") {
                    popUpTo("splash") { inclusive = true }
                }
            }
        }

        composable("get_started") {
            GetStartedScreen {
                navController.navigate("branding")
            }
        }

        composable("branding") {
            BrandingCarousel(
                onComplete = {
                    navController.navigate("login") {
                        popUpTo("branding") { inclusive = true }
                    }
                }
            )
        }

        /* ---------- AUTH ---------- */

        composable("login") {
            LoginPage(
                onLogin = { email, password ->
                    authViewModel.login(email, password)
                },
                onForgotPassword = { navController.navigate("forgot") },
                onSignUp = { navController.navigate("create_account") },
                onGoogleSignUp = {},
                isLoading = authState.loading,
                errorMessage = authState.error ?: ""
            )
        }

        composable("create_account") {
            CreateAccount(
                onAccountCreated = {
                    navController.navigate("welcome") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("forgot") {
            ForgotPassword(
                onSendOTP = { navController.navigate("otp") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("otp") {
            OTPVerification(
                onVerify = { navController.navigate("otp_success") },
                onBack = { navController.popBackStack() }
            )
        }

        composable("otp_success") {
            OTPSuccess {
                navController.navigate("login") {
                    popUpTo("login") { inclusive = true }
                }
            }
        }

        composable("create_profile") {
            CreateProfile { name, email, _ ->
                userName = name
                userEmail = email
                navController.navigate("welcome")
            }
        }

        composable("welcome") {
            Welcome(
                email = userEmail,
                onContinue = { navController.navigate("interest") }
            )
        }

        /* ---------- INTEREST ---------- */

        composable("interest") {
            InterestSelection(
                onComplete = {
                    navController.navigate("dashboard") {
                        popUpTo("interest") { inclusive = true }
                    }
                }
            )
        }

        /* ---------- DASHBOARD ---------- */

        composable("dashboard") {
            MainDashboard(
                userName = userName,
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        /* ---------- LAPTOP LIST ---------- */

        composable(LAPTOP_LIST) {
            LaptopList(
                navController = navController,
                onBack = { navController.popBackStack() },
                creditsPurchased = laptopCredits,
                onCreditsClick = { index ->
                    navController.navigate("$LAPTOP_CREDITS/$index")
                },
                onBidClick = { index ->
                    if (laptopCredits[index] == true) {
                        navController.navigate("laptop_details/$index")
                    } else {
                        Toast.makeText(
                            context,
                            "Please buy credits to bid on ${laptopItems.getOrNull(index)?.name ?: "this laptop"}",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate("$LAPTOP_CREDITS/$index")
                    }
                }
            )
        }

        /* ---------- LAPTOP DETAILS ---------- */

        composable("laptop_details/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            AuctionDetailsScreen(
                laptopIndex = index,
                onBack = { navController.popBackStack() },
                onPlaceBid = { /* Handle bid */ }
            )
        }

        /* ---------- LAPTOP CREDIT FLOW ---------- */
        composable("$LAPTOP_CREDITS/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val laptop = laptopItems.getOrElse(index) { laptopItems[0] }
            CreditsScreen(
                itemName = laptop.name,
                onBack = { navController.popBackStack() },
                onPay = { navController.navigate("$LAPTOP_PAYMENT_METHOD/$index") }
            )
        }

        composable("$LAPTOP_PAYMENT_METHOD/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            PaymentMethodScreen(
                amount = 100,
                onBack = { navController.popBackStack() },
                onPay = { method ->
                    navController.navigate("$LAPTOP_PAYMENT_PROCESS/$index/$method")
                }
            )
        }

        composable("$LAPTOP_PAYMENT_PROCESS/{index}/{method}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val method = backStackEntry.arguments?.getString("method") ?: "phonepe"
            PaymentProcessingScreen(
                method = method,
                onFinished = {
                    navController.navigate("$LAPTOP_PAYMENT_SUCCESS/$index/$method") {
                        popUpTo("$LAPTOP_PAYMENT_METHOD/$index") { inclusive = false }
                    }
                }
            )
        }

        composable("$LAPTOP_PAYMENT_SUCCESS/{index}/{method}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val laptop = laptopItems.getOrElse(index) { laptopItems[0] }
            LaunchedEffect(index) {
                laptopCredits[index] = true
            }
            PaymentSuccessScreen(
                itemName = laptop.name,
                onStartBid = {
                    navController.navigate("laptop_details/$index") {
                        popUpTo(LAPTOP_LIST) { inclusive = false }
                    }
                }
            )
        }

        /* ---------- MOBILE CREDIT FLOW ---------- */
        composable("$MOBILE_CREDITS/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val mobile = mobiles.getOrElse(index) { mobiles[0] }
            CreditsScreen(
                itemName = mobile.name,
                onBack = { navController.popBackStack() },
                onPay = { navController.navigate("$MOBILE_PAYMENT_METHOD/$index") }
            )
        }

        composable("$MOBILE_PAYMENT_METHOD/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            PaymentMethodScreen(
                amount = 100,
                onBack = { navController.popBackStack() },
                onPay = { method ->
                    navController.navigate("$MOBILE_PAYMENT_PROCESS/$index/$method")
                }
            )
        }

        composable("$MOBILE_PAYMENT_PROCESS/{index}/{method}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val method = backStackEntry.arguments?.getString("method") ?: "phonepe"
            PaymentProcessingScreen(
                method = method,
                onFinished = {
                    navController.navigate("$MOBILE_PAYMENT_SUCCESS/$index/$method") {
                        popUpTo("$MOBILE_PAYMENT_METHOD/$index") { inclusive = false }
                    }
                }
            )
        }

        composable("$MOBILE_PAYMENT_SUCCESS/{index}/{method}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val mobile = mobiles.getOrElse(index) { mobiles[0] }
            LaunchedEffect(index) {
                mobileCredits[index] = true
            }
            PaymentSuccessScreen(
                itemName = mobile.name,
                onStartBid = {
                    navController.navigate("mobile_details/$index") {
                        popUpTo("mobile_list") { inclusive = false }
                    }
                }
            )
        }

        /* ---------- COMPUTER CREDIT FLOW ---------- */
        composable("$COMPUTER_CREDITS/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val computer = computerList.getOrElse(index) { computerList[0] }
            CreditsScreen(
                itemName = computer.name,
                onBack = { navController.popBackStack() },
                onPay = { navController.navigate("$COMPUTER_PAYMENT_METHOD/$index") }
            )
        }

        composable("$COMPUTER_PAYMENT_METHOD/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            PaymentMethodScreen(
                amount = 100,
                onBack = { navController.popBackStack() },
                onPay = { method ->
                    navController.navigate("$COMPUTER_PAYMENT_PROCESS/$index/$method")
                }
            )
        }

        composable("$COMPUTER_PAYMENT_PROCESS/{index}/{method}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val method = backStackEntry.arguments?.getString("method") ?: "phonepe"
            PaymentProcessingScreen(
                method = method,
                onFinished = {
                    navController.navigate("$COMPUTER_PAYMENT_SUCCESS/$index/$method") {
                        popUpTo("$COMPUTER_PAYMENT_METHOD/$index") { inclusive = false }
                    }
                }
            )
        }

        composable("$COMPUTER_PAYMENT_SUCCESS/{index}/{method}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val computer = computerList.getOrElse(index) { computerList[0] }
            LaunchedEffect(index) {
                computerCredits[index] = true
            }
            PaymentSuccessScreen(
                itemName = computer.name,
                onStartBid = {
                    navController.navigate("computer_details/$index") {
                        popUpTo("computer_list") { inclusive = false }
                    }
                }
            )
        }

        /* ---------- MONITOR CREDIT FLOW ---------- */
        composable("$MONITOR_CREDITS/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val monitor = monitorList.getOrElse(index) { monitorList[0] }
            CreditsScreen(
                itemName = monitor.name,
                onBack = { navController.popBackStack() },
                onPay = { navController.navigate("$MONITOR_PAYMENT_METHOD/$index") }
            )
        }

        composable("$MONITOR_PAYMENT_METHOD/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            PaymentMethodScreen(
                amount = 100,
                onBack = { navController.popBackStack() },
                onPay = { method ->
                    navController.navigate("$MONITOR_PAYMENT_PROCESS/$index/$method")
                }
            )
        }

        composable("$MONITOR_PAYMENT_PROCESS/{index}/{method}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val method = backStackEntry.arguments?.getString("method") ?: "phonepe"
            PaymentProcessingScreen(
                method = method,
                onFinished = {
                    navController.navigate("$MONITOR_PAYMENT_SUCCESS/$index/$method") {
                        popUpTo("$MONITOR_PAYMENT_METHOD/$index") { inclusive = false }
                    }
                }
            )
        }

        composable("$MONITOR_PAYMENT_SUCCESS/{index}/{method}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val monitor = monitorList.getOrElse(index) { monitorList[0] }
            LaunchedEffect(index) {
                monitorCredits[index] = true
            }
            PaymentSuccessScreen(
                itemName = monitor.name,
                onStartBid = {
                    navController.navigate("monitor_details/$index") {
                        popUpTo("monitor_list") { inclusive = false }
                    }
                }
            )
        }

        /* ---------- TABLET CREDIT FLOW ---------- */
        composable("$TABLET_CREDITS/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val tablets = listOf(
                TabletProduct(
                    name = "iPad Pro 12.9\" M2",
                    imageRes = R.drawable.ic_ipadtablet,
                    imageResList = listOf(R.drawable.ic_ipadtablet, R.drawable.ic_ipadtablet),
                    currentBid = 85000,
                    seller = TabletSeller(
                        name = "Apple Store Elite",
                        verified = true,
                        rating = 5.0
                    ),
                    endTime = 30,
                    registeredBidders = 26,
                    specs = mapOf(
                        "Display" to "12.9\" Liquid",
                        "Camera" to "12MP Wide",
                        "Processor" to "M2",
                        "Storage" to "512GB"
                    ),
                    condition = "Excellent",
                    conditionDetails = "Premium condition with Magic Keyboard and Apple Pencil included."
                ),
                TabletProduct(
                    name = "Samsung Galaxy Tab S9 Ultra",
                    imageRes = R.drawable.ic_samsungtablet,
                    imageResList = listOf(R.drawable.ic_samsungtablet, R.drawable.ic_samsungtablet),
                    currentBid = 72000,
                    seller = TabletSeller(
                        name = "TabletZone",
                        verified = true,
                        rating = 4.8
                    ),
                    endTime = 30,
                    registeredBidders = 20,
                    specs = mapOf(
                        "Display" to "14.6\" Dynamic",
                        "Stylus" to "Included",
                        "Storage" to "256GB",
                        "Connectivity" to "WiFi"
                    ),
                    condition = "Very Good",
                    conditionDetails = "Includes S Pen and Book Cover Keyboard. Perfect for work & creativity."
                ),
                TabletProduct(
                    name = "Microsoft Surface Pro 9",
                    imageRes = R.drawable.ic_surfacetablet,
                    imageResList = listOf(R.drawable.ic_surfacetablet, R.drawable.ic_surfacetablet),
                    currentBid = 58000,
                    seller = TabletSeller(
                        name = "Microsoft Store",
                        verified = true,
                        rating = 4.6
                    ),
                    endTime = 30,
                    registeredBidders = 16,
                    specs = mapOf(
                        "Processor" to "Intel Core i7",
                        "Storage" to "512GB SSD",
                        "Display" to "13\" PixelSense"
                    ),
                    condition = "New",
                    conditionDetails = "Versatile 2-in-1 tablet perfect for productivity."
                )
            )
            val tablet = tablets.getOrElse(index) { tablets[0] }
            CreditsScreen(
                itemName = tablet.name,
                onBack = { navController.popBackStack() },
                onPay = { navController.navigate("$TABLET_PAYMENT_METHOD/$index") }
            )
        }

        composable("$TABLET_PAYMENT_METHOD/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            PaymentMethodScreen(
                amount = 100,
                onBack = { navController.popBackStack() },
                onPay = { method ->
                    navController.navigate("$TABLET_PAYMENT_PROCESS/$index/$method")
                }
            )
        }

        composable("$TABLET_PAYMENT_PROCESS/{index}/{method}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val method = backStackEntry.arguments?.getString("method") ?: "phonepe"
            PaymentProcessingScreen(
                method = method,
                onFinished = {
                    navController.navigate("$TABLET_PAYMENT_SUCCESS/$index/$method") {
                        popUpTo("$TABLET_PAYMENT_METHOD/$index") { inclusive = false }
                    }
                }
            )
        }

        composable("$TABLET_PAYMENT_SUCCESS/{index}/{method}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val tablets = listOf(
                TabletProduct(
                    name = "iPad Pro 12.9\" M2",
                    imageRes = R.drawable.ic_ipadtablet,
                    imageResList = listOf(R.drawable.ic_ipadtablet, R.drawable.ic_ipadtablet),
                    currentBid = 85000,
                    seller = TabletSeller(
                        name = "Apple Store Elite",
                        verified = true,
                        rating = 5.0
                    ),
                    endTime = 30,
                    registeredBidders = 26,
                    specs = mapOf(
                        "Display" to "12.9\" Liquid",
                        "Camera" to "12MP Wide",
                        "Processor" to "M2",
                        "Storage" to "512GB"
                    ),
                    condition = "Excellent",
                    conditionDetails = "Premium condition with Magic Keyboard and Apple Pencil included."
                ),
                TabletProduct(
                    name = "Samsung Galaxy Tab S9 Ultra",
                    imageRes = R.drawable.ic_samsungtablet,
                    imageResList = listOf(R.drawable.ic_samsungtablet, R.drawable.ic_samsungtablet),
                    currentBid = 72000,
                    seller = TabletSeller(
                        name = "TabletZone",
                        verified = true,
                        rating = 4.8
                    ),
                    endTime = 30,
                    registeredBidders = 20,
                    specs = mapOf(
                        "Display" to "14.6\" Dynamic",
                        "Stylus" to "Included",
                        "Storage" to "256GB",
                        "Connectivity" to "WiFi"
                    ),
                    condition = "Very Good",
                    conditionDetails = "Includes S Pen and Book Cover Keyboard. Perfect for work & creativity."
                ),
                TabletProduct(
                    name = "Microsoft Surface Pro 9",
                    imageRes = R.drawable.ic_surfacetablet,
                    imageResList = listOf(R.drawable.ic_surfacetablet, R.drawable.ic_surfacetablet),
                    currentBid = 58000,
                    seller = TabletSeller(
                        name = "Microsoft Store",
                        verified = true,
                        rating = 4.6
                    ),
                    endTime = 30,
                    registeredBidders = 16,
                    specs = mapOf(
                        "Processor" to "Intel Core i7",
                        "Storage" to "512GB SSD",
                        "Display" to "13\" PixelSense"
                    ),
                    condition = "New",
                    conditionDetails = "Versatile 2-in-1 tablet perfect for productivity."
                )
            )
            val tablet = tablets.getOrElse(index) { tablets[0] }
            LaunchedEffect(index) {
                tabletCredits[index] = true
            }
            PaymentSuccessScreen(
                itemName = tablet.name,
                onStartBid = {
                    navController.navigate("tablet_details/$index") {
                        popUpTo("tablet_list") { inclusive = false }
                    }
                }
            )
        }

        /* ---------- DELL XPS DETAILS ---------- */

        composable(DELL_XPS_15_DETAILS) {
            DellXps15AuctionDetails(
                onBack = { navController.popBackStack() }
            )
        }

        /* ---------- MOBILE LIST ---------- */

        composable("mobile_list") {
            MobileList(
                navController = navController,
                onBack = { navController.popBackStack() },
                creditsPurchased = mobileCredits,
                onCreditsClick = { index ->
                    navController.navigate("$MOBILE_CREDITS/$index")
                },
                onBidClick = { index ->
                    if (mobileCredits[index] == true) {
                        navController.navigate("mobile_details/$index")
                    } else {
                        Toast.makeText(
                            context,
                            "Please buy credits to bid on ${mobiles.getOrNull(index)?.name ?: "this mobile"}",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate("$MOBILE_CREDITS/$index")
                    }
                }
            )
        }

        /* ---------- MOBILE DETAILS ---------- */

        composable("mobile_details/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            MobileDetails(
                mobileIndex = index,
                onBack = { navController.popBackStack() },
                onPlaceBid = { /* Handle bid */ }
            )
        }

        /* ---------- COMPUTER LIST ---------- */

        composable("computer_list") {
            ComputerList(
                navController = navController,
                onBack = { navController.popBackStack() },
                creditsPurchased = computerCredits,
                onCreditsClick = { index ->
                    navController.navigate("$COMPUTER_CREDITS/$index")
                },
                onBidClick = { index ->
                    if (computerCredits[index] == true) {
                        navController.navigate("computer_details/$index")
                    } else {
                        Toast.makeText(
                            context,
                            "Please buy credits to bid on ${computerList.getOrNull(index)?.name ?: "this computer"}",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate("$COMPUTER_CREDITS/$index")
                    }
                }
            )
        }

        /* ---------- COMPUTER DETAILS ---------- */

        composable("computer_details/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            ComputerDetailsScreen(
                computerIndex = index,
                onBack = { navController.popBackStack() },
                onPlaceBid = { /* Handle bid */ }
            )
        }

        /* ---------- MONITOR LIST ---------- */

        composable("monitor_list") {
            MonitorList(
                navController = navController,
                onBack = { navController.popBackStack() },
                creditsPurchased = monitorCredits,
                onCreditsClick = { index ->
                    navController.navigate("$MONITOR_CREDITS/$index")
                },
                onBidClick = { index ->
                    if (monitorCredits[index] == true) {
                        navController.navigate("monitor_details/$index")
                    } else {
                        Toast.makeText(
                            context,
                            "Please buy credits to bid on ${monitorList.getOrNull(index)?.name ?: "this monitor"}",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate("$MONITOR_CREDITS/$index")
                    }
                }
            )
        }

        /* ---------- MONITOR DETAILS ---------- */

        composable("monitor_details/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            MonitorDetailsScreen(
                monitorIndex = index,
                onBack = { navController.popBackStack() },
                onPlaceBid = { /* Handle bid */ }
            )
        }

        /* ---------- TABLET LIST ---------- */

        composable("tablet_list") {
            val tablets = listOf(
                com.example.onlinebidding.model.Product(
                    id = 1,
                    name = "iPad Pro 12.9\" M2",
                    imageRes = R.drawable.ic_ipadtablet,
                    currentBid = 85000,
                    seller = com.example.onlinebidding.model.Seller("Apple Store", 4.9),
                    specs = mapOf("Display" to "12.9\" Retina XDR", "Storage" to "512GB")
                ),
                com.example.onlinebidding.model.Product(
                    id = 2,
                    name = "Samsung Galaxy Tab S9",
                    imageRes = R.drawable.ic_samsungtablet,
                    currentBid = 72000,
                    seller = com.example.onlinebidding.model.Seller("Samsung Official", 4.8),
                    specs = mapOf("Processor" to "Snapdragon 8 Gen 2", "Storage" to "256GB")
                ),
                com.example.onlinebidding.model.Product(
                    id = 3,
                    name = "Microsoft Surface Pro 9",
                    imageRes = R.drawable.ic_surfacetablet,
                    currentBid = 58000,
                    seller = com.example.onlinebidding.model.Seller("Microsoft Store", 4.6),
                    specs = mapOf("Processor" to "Intel Core i7", "Storage" to "512GB SSD")
                )
            )
            TabletListScreen(
                onBack = { navController.popBackStack() },
                creditsPurchased = tabletCredits,
                onCreditsClick = { product, index ->
                    navController.navigate("$TABLET_CREDITS/$index")
                },
                onBidClick = { product, index ->
                    if (tabletCredits[index] == true) {
                        navController.navigate("tablet_details/$index")
                    } else {
                        Toast.makeText(
                            context,
                            "Please buy credits to bid on ${product.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate("$TABLET_CREDITS/$index")
                    }
                },
                onViewClick = { product, index ->
                    val idx = tablets.indexOfFirst { it.id == product.id }
                    navController.navigate("tablet_details/${if (idx >= 0) idx else 0}")
                }
            )
        }

        /* ---------- TABLET DETAILS ---------- */

        composable("tablet_details/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val tablets = listOf(
                TabletProduct(
                    name = "iPad Pro 12.9\" M2",
                    imageRes = R.drawable.ic_ipadtablet,
                    currentBid = 85000,
                    seller = TabletSeller(
                        name = "Apple Store Elite",
                        verified = true,
                        rating = 5.0
                    ),
                    endTime = 30,
                    registeredBidders = 26,
                    specs = mapOf(
                        "Display" to "12.9\" Liquid",
                        "Camera" to "12MP Wide",
                        "Processor" to "M2",
                        "Storage" to "512GB"
                    ),
                    condition = "Excellent",
                    conditionDetails = "Premium condition with Magic Keyboard and Apple Pencil included."
                ),
                TabletProduct(
                    name = "Samsung Galaxy Tab S9 Ultra",
                    imageRes = R.drawable.ic_samsungtablet,
                    currentBid = 72000,
                    seller = TabletSeller(
                        name = "TabletZone",
                        verified = true,
                        rating = 4.8
                    ),
                    endTime = 30,
                    registeredBidders = 20,
                    specs = mapOf(
                        "Display" to "14.6\" Dynamic",
                        "Stylus" to "Included",
                        "Storage" to "256GB",
                        "Connectivity" to "WiFi"
                    ),
                    condition = "Very Good",
                    conditionDetails = "Includes S Pen and Book Cover Keyboard. Perfect for work & creativity."
                ),
                TabletProduct(
                    name = "Microsoft Surface Pro 9",
                    imageRes = R.drawable.ic_surfacetablet,
                    currentBid = 58000,
                    seller = TabletSeller(
                        name = "Microsoft Store",
                        verified = true,
                        rating = 4.6
                    ),
                    endTime = 30,
                    registeredBidders = 16,
                    specs = mapOf(
                        "Processor" to "Intel Core i7",
                        "Storage" to "512GB SSD",
                        "Display" to "13\" PixelSense"
                    ),
                    condition = "New",
                    conditionDetails = "Versatile 2-in-1 tablet perfect for productivity."
                )
            )
            Tabletsscreen(
                product = tablets.getOrElse(index) { tablets[0] },
                onBack = { navController.popBackStack() },
                onPlaceBid = { /* Handle bid */ }
            )
        }

        /* ---------- PROFILE ---------- */

        composable("profile") {
            ProfileScreen(
                userData = UserData(
                    name = userName,
                    email = userEmail,
                    phone = "9999999999",
                    totalBids = 18,
                    wins = 3,
                    credits = 120
                ),
                onBack = { navController.popBackStack() }
            )
        }

        /* ---------- EXTRA ---------- */

        composable("trending_auctions") {
            TrendingAuctions(onBack = { navController.popBackStack() })
        }

        composable("flash") {
            FlashAuctions(onBack = { navController.popBackStack() })
        }

        composable("search") {
            Search(onBack = { navController.popBackStack() })
        }
    }
}