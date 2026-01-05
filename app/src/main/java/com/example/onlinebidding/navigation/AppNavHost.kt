package com.example.onlinebidding.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.onlinebidding.screens.splash.*
import com.example.onlinebidding.screens.login.*
import com.example.onlinebidding.screens.login.dashboard.ProfileScreen
import com.example.onlinebidding.screens.login.dashboard.UserData
import com.example.onlinebidding.screens.welcome.Welcome
import com.example.onlinebidding.screens.interest.InterestSelection
import com.example.onlinebidding.screens.dashboard.*
import com.example.onlinebidding.screens.trending.TrendingAuctions
import com.example.onlinebidding.screens.flash.FlashAuctions
import com.example.onlinebidding.screens.login.dashboard.Search
import com.example.onlinebidding.screens.products.*
import com.example.onlinebidding.screens.products.LaptopList
import com.example.onlinebidding.screens.products.TabletListScreen
import com.example.onlinebidding.screens.products.MobileProduct
import com.example.onlinebidding.screens.products.Product as ProductDetails
import com.example.onlinebidding.screens.products.Seller as SellerDetails
import com.example.onlinebidding.screens.products.CreditsScreen
import com.example.onlinebidding.screens.products.PaymentMethodScreen
import com.example.onlinebidding.screens.products.PaymentProcessingScreen
import com.example.onlinebidding.screens.products.PaymentSuccessScreen
import com.example.onlinebidding.screens.products.UPIEntryScreen
import com.example.onlinebidding.screens.products.BidCommentsScreen
import com.example.onlinebidding.screens.products.CreditsState
import com.example.onlinebidding.screens.products.MobileAuctionDetailScreen
import com.example.onlinebidding.screens.products.ComputerAuctionDetailScreen
import com.example.onlinebidding.screens.products.MonitorAuctionDetailScreen
import com.example.onlinebidding.screens.products.TabletAuctionDetailScreen
import com.example.onlinebidding.screens.products.AuctionWinnerScreen
import com.example.onlinebidding.screens.admin.AdminDashboard
import com.example.onlinebidding.screens.admin.AdminProductList
import com.example.onlinebidding.screens.admin.AdminProductForm
import com.example.onlinebidding.screens.admin.AdminLaptopList
import com.example.onlinebidding.screens.admin.AdminMobileList
import com.example.onlinebidding.screens.admin.AdminComputerList
import com.example.onlinebidding.screens.admin.AdminMonitorList
import com.example.onlinebidding.screens.admin.AdminTabletList
import com.example.onlinebidding.ui.viewmodel.AuthViewModel
import com.example.onlinebidding.utils.getProductPrice
import com.example.onlinebidding.utils.calculateCredits
import com.example.onlinebidding.utils.getGoogleSignInClient
import com.example.onlinebidding.utils.rememberGoogleSignInLauncher
import androidx.compose.ui.platform.LocalContext
import com.example.onlinebidding.R

/* ---------------- ROUTES ---------------- */

const val LAPTOP_LIST = "laptop_list"
const val DELL_XPS_15_DETAILS = "dell_xps_15_details"

@Composable
fun AppNavHost() {

    val navController = rememberNavController()
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.uiState.collectAsState()

    // Update user data when auth state changes
    LaunchedEffect(authState.token, authState.role) {
        val token = authState.token
        if (token != null && token.isNotBlank()) {
            android.util.Log.d("AppNavHost", "Token received: ${token.take(10)}..., Role: ${authState.role}, Email: ${authState.email}")
            // Route based on role
            if (authState.role == "admin") {
                android.util.Log.d("AppNavHost", "Navigating to admin_dashboard")
                navController.navigate(route = "admin_dashboard") {
                    popUpTo(route = "login") { inclusive = true }
                }
            } else {
                android.util.Log.d("AppNavHost", "Navigating to interest")
                navController.navigate(route = "interest") {
                    popUpTo(route = "login") { inclusive = true }
                }
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
                navController.navigate(route = "get_started") {
                    popUpTo(route = "splash") { inclusive = true }
                }
            }
        }

        composable("get_started") {
            GetStartedScreen {
                navController.navigate(route = "branding")
            }
        }

        composable("branding") {
            BrandingCarousel(
                onComplete = {
                    navController.navigate(route = "login") {
                        popUpTo(route = "branding") { inclusive = true }
                    }
                }
            )
        }

        /* ---------- AUTH ---------- */

        composable("login") {
            val context = LocalContext.current
            val googleSignInClient = remember { getGoogleSignInClient(context) }
            
            // Watch for successful Google Sign-In and navigate
            LaunchedEffect(authState.token, authState.role, authState.loading) {
                val token = authState.token
                if (token != null && token.isNotBlank() && !authState.loading) {
                    android.util.Log.d("LoginScreen", "Token detected in login screen, navigating...")
                    if (authState.role == "admin") {
                        navController.navigate(route = "admin_dashboard") {
                            popUpTo(route = "login") { inclusive = true }
                        }
                    } else {
                        navController.navigate(route = "interest") {
                            popUpTo(route = "login") { inclusive = true }
                        }
                    }
                }
            }
            
            val googleSignInLauncher = rememberGoogleSignInLauncher(
                onSignInSuccess = { idToken ->
                    android.util.Log.d("LoginScreen", "Google Sign-In success, calling viewModel")
                    authViewModel.googleSignIn(idToken)
                },
                onSignInError = { error ->
                    android.util.Log.e("LoginScreen", "Google Sign-In error: $error")
                    // Error is already shown via Toast in the helper
                }
            )
            
            LoginPage(
                onLogin = { email, password, loginType ->
                    authViewModel.login(email, password, loginType)
                },
                onForgotPassword = { navController.navigate(route = "forgot") },
                onSignUp = { loginType ->
                    // Navigate to appropriate registration based on login type
                    if (loginType == "Admin") {
                        navController.navigate(route = "create_admin_account")
                    } else {
                        navController.navigate(route = "create_account")
                    }
                },
                onGoogleSignUp = {
                    android.util.Log.d("LoginScreen", "Google Sign-Up button clicked")
                    val signInIntent = googleSignInClient.signInIntent
                    googleSignInLauncher.launch(signInIntent)
                },
                isLoading = authState.loading,
                errorMessage = authState.error ?: ""
            )
        }

        composable("create_account") {
            CreateAccount(
                isAdmin = false,
                onAccountCreated = {
                    navController.navigate(route = "login") {
                        popUpTo(route = "create_account") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("create_admin_account") {
            CreateAccount(
                isAdmin = true,
                onAccountCreated = {
                    // If already logged in as admin (creating from admin dashboard), go back to admin dashboard
                    // Otherwise, go to login
                    if (authState.token != null && authState.role == "admin") {
                        navController.navigate(route = "admin_dashboard") {
                            popUpTo(route = "create_admin_account") { inclusive = true }
                        }
                    } else {
                        navController.navigate(route = "login") {
                            popUpTo(route = "create_admin_account") { inclusive = true }
                        }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("forgot") {
            ForgotPassword(
                onSendOTP = { email ->
                    val encodedEmail = java.net.URLEncoder.encode(email, "UTF-8")
                    navController.navigate(route = "otp/$encodedEmail")
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("otp/{email}") { backStackEntry ->
            val encodedEmail = backStackEntry.arguments?.getString("email") ?: ""
            val email = try {
                java.net.URLDecoder.decode(encodedEmail, "UTF-8")
            } catch (e: Exception) {
                encodedEmail
            }
            OTPVerification(
                email = email,
                onVerify = { resetToken ->
                    val encodedToken = java.net.URLEncoder.encode(resetToken, "UTF-8")
                    navController.navigate(route = "reset_password/$encodedEmail/$encodedToken")
                },
                onBack = { navController.popBackStack() }
            )

        }

        composable("reset_password/{email}/{token}") { backStackEntry ->
            val encodedEmail = backStackEntry.arguments?.getString("email") ?: ""
            val encodedToken = backStackEntry.arguments?.getString("token") ?: ""
            val email = try {
                java.net.URLDecoder.decode(encodedEmail, "UTF-8")
            } catch (e: Exception) {
                encodedEmail
            }
            val token = try {
                java.net.URLDecoder.decode(encodedToken, "UTF-8")
            } catch (e: Exception) {
                encodedToken
            }
            ResetPassword(
                email = email,
                resetToken = token,
                onPasswordReset = {
                    navController.navigate(route = "login") {
                        popUpTo(route = "login") { inclusive = true }
                    }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("otp_success") {
            OTPSuccess {
                navController.navigate(route = "login") {
                    popUpTo(route = "login") { inclusive = true }
                }
            }
        }

        // create_profile and welcome screens are skipped - users go directly to interest after login
        // composable("create_profile") { ... }
        // composable("welcome") { ... }

        /* ---------- ADMIN DASHBOARD ---------- */
        composable("admin_dashboard") {
            AdminDashboard(
                onNavigateToCategory = { route ->
                    navController.navigate(route)
                },
                onCreateAdmin = {
                    navController.navigate("create_admin_account")
                },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }

        /* ---------- ADMIN PRODUCT LISTS ---------- */
        composable("admin_laptop_list") {
            AdminLaptopList(
                navController = navController,
                onBack = { navController.popBackStack() },
                userToken = authState.token
            )
        }

        composable("admin_mobile_list") {
            AdminMobileList(
                navController = navController,
                onBack = { navController.popBackStack() },
                userToken = authState.token
            )
        }

        composable("admin_computer_list") {
            AdminComputerList(
                navController = navController,
                onBack = { navController.popBackStack() },
                userToken = authState.token
            )
        }

        composable("admin_monitor_list") {
            AdminMonitorList(
                navController = navController,
                onBack = { navController.popBackStack() },
                userToken = authState.token
            )
        }

        composable("admin_tablet_list") {
            AdminTabletList(
                navController = navController,
                onBack = { navController.popBackStack() },
                userToken = authState.token
            )
        }

        composable("admin_products") {
            AdminProductList(
                token = authState.token ?: "",
                onBack = { navController.popBackStack() },
                onEditProduct = { productId ->
                    navController.navigate("admin_product_form/$productId")
                },
                onAddProduct = {
                    navController.navigate("admin_product_form")
                }
            )
        }

        composable("admin_product_form") {
            AdminProductForm(
                token = authState.token ?: "",
                productId = null,
                initialProduct = null,
                onBack = { navController.popBackStack() },
                onSave = {
                    navController.popBackStack()
                }
            )
        }

        composable("admin_product_form/{category}") { backStackEntry ->
            val category = backStackEntry.arguments?.getString("category") ?: "laptop"
            val initialProduct = com.example.onlinebidding.api.AdminProductItem(
                id = 0,
                title = "",
                description = null,
                category = category,
                image_url = null,
                specs = null,
                condition_label = null,
                base_price = 0.0,
                auction_id = null,
                start_price = null,
                current_price = null,
                auction_status = null,
                created_at = null
            )

            AdminProductForm(
                token = authState.token ?: "",
                productId = null,
                initialProduct = initialProduct,
                onBack = { navController.popBackStack() },
                onSave = {
                    navController.popBackStack()
                }
            )
        }

        composable("admin_product_form/{productId}") { backStackEntry ->
            val productId = backStackEntry.arguments?.getString("productId")?.toIntOrNull()
            AdminProductForm(
                token = authState.token ?: "",
                productId = productId,
                initialProduct = null,
                onBack = { navController.popBackStack() },
                onSave = {
                    navController.popBackStack()
                }
            )
        }

        /* ---------- INTEREST ---------- */

        composable("interest") {
            InterestSelection(
                onComplete = {
                    navController.navigate(route = "welcome") {
                        popUpTo(route = "interest") { inclusive = true }
                    }
                }
            )
        }

        /* ---------- WELCOME ---------- */

        composable("welcome") {
            Welcome(
                email = authState.email ?: "user@example.com",
                onContinue = {
                    navController.navigate(route = "dashboard") {
                        popUpTo(route = "welcome") { inclusive = true }
                    }
                }
            )
        }

        /* ---------- DASHBOARD ---------- */

        composable("dashboard") {
            MainDashboard(
                userName = authState.name ?: "User",
                onNavigate = { route ->
                    navController.navigate(route = route)
                }
            )
        }

        /* ---------- LAPTOP LIST ---------- */

        composable(LAPTOP_LIST) {
            LaptopList(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        /* ---------- LAPTOP AUCTION DETAIL ---------- */

        composable("auction_detail/{index}/{laptopName}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val laptopName = backStackEntry.arguments?.getString("laptopName") ?: ""
            AuctionDetailsScreen(
                auctionId = null,
                laptopIndex = index,
                laptopName = laptopName,
                onBack = { navController.popBackStack() },
                onPlaceBid = { /* Handled in screen */ },
                navController = navController
            )
        }
        
        composable("auction_detail/{index}/{auctionId}/{laptopName}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val auctionId = backStackEntry.arguments?.getString("auctionId")?.toIntOrNull()
            val laptopName = backStackEntry.arguments?.getString("laptopName") ?: ""
            AuctionDetailsScreen(
                auctionId = auctionId,
                laptopIndex = index,
                laptopName = laptopName,
                onBack = { navController.popBackStack() },
                onPlaceBid = { /* Handled in screen */ },
                navController = navController
            )
        }
        
        // Backward compatibility routes
        composable("auction_detail/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            AuctionDetailsScreen(
                auctionId = null,
                laptopIndex = index,
                laptopName = "",
                onBack = { navController.popBackStack() },
                onPlaceBid = { /* Handled in screen */ },
                navController = navController
            )
        }
        
        composable("auction_detail/{index}/{auctionId}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val auctionId = backStackEntry.arguments?.getString("auctionId")?.toIntOrNull()
            AuctionDetailsScreen(
                auctionId = auctionId,
                laptopIndex = index,
                laptopName = "",
                onBack = { navController.popBackStack() },
                onPlaceBid = { /* Handled in screen */ },
                navController = navController
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
                onBack = { navController.popBackStack() }
            )
        }

        /* ---------- MOBILE AUCTION DETAIL ---------- */

        composable("mobile_auction_detail/{index}/{mobileName}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val mobileName = backStackEntry.arguments?.getString("mobileName") ?: ""
            MobileAuctionDetailScreen(
                mobileIndex = index,
                mobileName = mobileName,
                onBack = { navController.popBackStack() },
                onSpecsClick = { /* Show specs dialog */ },
                onBidClick = { navController.navigate("bid_comments/mobile/$index") }
            )
        }
        
        // Backward compatibility route
        composable("mobile_auction_detail/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            MobileAuctionDetailScreen(
                mobileIndex = index,
                mobileName = "",
                onBack = { navController.popBackStack() },
                onSpecsClick = { /* Show specs dialog */ },
                onBidClick = { navController.navigate("bid_comments/mobile/$index") }
            )
        }

        /* ---------- MOBILE DETAILS ---------- */

        composable("mobile_details/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val mobiles = listOf(
                MobileProduct(
                    name = "iPhone 15 Pro Max",
                    description = "A17 Pro Chip · 512GB · Titanium Design",
                    rating = 4.8,
                    price = "₹1,28,000",
                    image = R.drawable.ic_appleiphone15pro
                ),
                MobileProduct(
                    name = "Samsung Galaxy S24 Ultra",
                    description = "Snapdragon 8 Gen 3 · 256GB · S Pen",
                    rating = 4.6,
                    price = "₹98,000",
                    image = R.drawable.ic_samsunggalaxys24ultra
                ),
                MobileProduct(
                    name = "OnePlus 12 Pro",
                    description = "Snapdragon 8 Gen 3 · 256GB · Fast Charging",
                    rating = 4.9,
                    price = "₹54,000",
                    image = R.drawable.ic_oneplus12pro
                )
            )
            MobileDetails(
                product = mobiles.getOrElse(index) { mobiles[0] },
                navController = navController,
                index = index,
                onBack = { navController.popBackStack() }
            )
        }

        /* ---------- COMPUTER LIST ---------- */

        composable("computer_list") {
            ComputerList(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        /* ---------- COMPUTER AUCTION DETAIL ---------- */

        composable("computer_auction_detail/{index}/{computerName}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val computerName = backStackEntry.arguments?.getString("computerName") ?: ""
            ComputerAuctionDetailScreen(
                computerIndex = index,
                computerName = computerName,
                onBack = { navController.popBackStack() },
                onSpecsClick = { /* Show specs dialog */ },
                onBidClick = { navController.navigate("bid_comments/computer/$index") }
            )
        }
        
        // Backward compatibility route
        composable("computer_auction_detail/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            ComputerAuctionDetailScreen(
                computerIndex = index,
                computerName = "",
                onBack = { navController.popBackStack() },
                onSpecsClick = { /* Show specs dialog */ },
                onBidClick = { navController.navigate("bid_comments/computer/$index") }
            )
        }

        /* ---------- COMPUTER DETAILS ---------- */

        composable("computer_details/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val computers = listOf(
                ProductDetails(
                    name = "Custom Gaming PC RTX",
                    imageRes = R.drawable.ic_pcgamming,
                    currentBid = 285000,
                    seller = SellerDetails(
                        name = "TechStore Pro",
                        verified = true,
                        rating = 4.9
                    ),
                    endTime = 3600,
                    registeredBidders = 45,
                    specs = mapOf(
                        "RAM" to "64GB DDR5",
                        "Storage" to "2TB NVMe + 4TB HDD",
                        "GPU" to "RTX 4090"
                    ),
                    condition = "New",
                    conditionDetails = "Brand new custom gaming PC with top-tier components."
                ),
                ProductDetails(
                    name = "Mac Studio M2 Ultra",
                    imageRes = R.drawable.ic_macstudio,
                    currentBid = 310000,
                    seller = SellerDetails(
                        name = "Apple Authorized",
                        verified = true,
                        rating = 5.0
                    ),
                    endTime = 7200,
                    registeredBidders = 32,
                    specs = mapOf(
                        "Memory" to "192GB Unified",
                        "Storage" to "4TB SSD",
                        "Processor" to "M2 Ultra"
                    ),
                    condition = "New",
                    conditionDetails = "Sealed box, never opened. Full warranty included."
                ),
                ProductDetails(
                    name = "Workstation HP Z8 G5",
                    imageRes = R.drawable.ic_hp_z5,
                    currentBid = 195000,
                    seller = SellerDetails(
                        name = "Enterprise Solutions",
                        verified = true,
                        rating = 4.7
                    ),
                    endTime = 5400,
                    registeredBidders = 28,
                    specs = mapOf(
                        "RAM" to "128GB ECC DDR5",
                        "Storage" to "4TB NVMe RAID",
                        "CPU" to "Xeon Processor"
                    ),
                    condition = "Used - Excellent",
                    conditionDetails = "Professional workstation in excellent condition."
                )
            )
            ComputerDetailsScreen(
                product = computers.getOrElse(index) { computers[0] },
                onBack = { navController.popBackStack() }
            )
        }

        /* ---------- MONITOR LIST ---------- */

        composable("monitor_list") {
            MonitorList(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        /* ---------- MONITOR AUCTION DETAIL ---------- */

        composable("monitor_auction_detail/{index}/{monitorName}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val monitorName = backStackEntry.arguments?.getString("monitorName") ?: ""
            MonitorAuctionDetailScreen(
                monitorIndex = index,
                monitorName = monitorName,
                onBack = { navController.popBackStack() },
                onSpecsClick = { /* Show specs dialog */ },
                onBidClick = { navController.navigate("bid_comments/monitor/$index") }
            )
        }
        
        // Backward compatibility route
        composable("monitor_auction_detail/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            MonitorAuctionDetailScreen(
                monitorIndex = index,
                monitorName = "",
                onBack = { navController.popBackStack() },
                onSpecsClick = { /* Show specs dialog */ },
                onBidClick = { navController.navigate("bid_comments/monitor/$index") }
            )
        }

        /* ---------- MONITOR DETAILS ---------- */

        composable("monitor_details/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val monitors = listOf(
                ProductDetails(
                    name = "LG UltraGear 27\"",
                    imageRes = R.drawable.ic_monitor_lg,
                    currentBid = 32000,
                    seller = SellerDetails(
                        name = "LG Official Store",
                        verified = true,
                        rating = 4.8
                    ),
                    endTime = 1800,
                    registeredBidders = 18,
                    specs = mapOf(
                        "Refresh Rate" to "144Hz",
                        "Resolution" to "QHD",
                        "Panel" to "IPS"
                    ),
                    condition = "New",
                    conditionDetails = "Brand new gaming monitor with excellent color accuracy."
                ),
                ProductDetails(
                    name = "Samsung Odyssey G7",
                    imageRes = R.drawable.ic_monitor_samsung,
                    currentBid = 45000,
                    seller = SellerDetails(
                        name = "Samsung Store",
                        verified = true,
                        rating = 4.9
                    ),
                    endTime = 2400,
                    registeredBidders = 22,
                    specs = mapOf(
                        "Refresh Rate" to "240Hz",
                        "Resolution" to "QHD Curved",
                        "Panel" to "VA"
                    ),
                    condition = "New",
                    conditionDetails = "Curved gaming monitor with ultra-fast refresh rate."
                ),
                ProductDetails(
                    name = "Dell UltraSharp",
                    imageRes = R.drawable.ic_monitor_dell,
                    currentBid = 52000,
                    seller = SellerDetails(
                        name = "Dell Authorized",
                        verified = true,
                        rating = 4.7
                    ),
                    endTime = 3000,
                    registeredBidders = 15,
                    specs = mapOf(
                        "Resolution" to "4K",
                        "Connectivity" to "USB-C",
                        "Panel" to "IPS"
                    ),
                    condition = "New",
                    conditionDetails = "Professional 4K monitor perfect for content creation."
                )
            )
            MonitorDetailsScreen(
                product = monitors.getOrElse(index) { monitors[0] },
                onBack = { navController.popBackStack() },
                onPlaceBid = { /* Handle bid */ }
            )
        }

        /* ---------- TABLET LIST ---------- */

        composable("tablet_auction_detail/{index}/{tabletName}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val tabletName = backStackEntry.arguments?.getString("tabletName") ?: ""
            TabletAuctionDetailScreen(
                tabletIndex = index,
                tabletName = tabletName,
                onBack = { navController.popBackStack() },
                onSpecsClick = { /* Show specs dialog */ },
                onBidClick = { navController.navigate("bid_comments/tablet/$index") }
            )
        }
        
        // Backward compatibility route
        composable("tablet_auction_detail/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            TabletAuctionDetailScreen(
                tabletIndex = index,
                tabletName = "",
                onBack = { navController.popBackStack() },
                onSpecsClick = { /* Show specs dialog */ },
                onBidClick = { navController.navigate("bid_comments/tablet/$index") }
            )
        }

        composable("tablet_list") {
            val tablets = listOf(
                com.example.onlinebidding.model.Product(
                    id = 1,
                    name = "iPad Pro 12.9\" M2",
                    imageRes = R.drawable.ic_ipadtablet,
                    currentBid = 85000,
                    seller = com.example.onlinebidding.model.Seller("Apple Store", verified = true, rating = 4.9),
                    specs = mapOf("Display" to "12.9\" Retina XDR", "Storage" to "512GB")
                ),
                com.example.onlinebidding.model.Product(
                    id = 2,
                    name = "Samsung Galaxy Tab S9",
                    imageRes = R.drawable.ic_samsungtablet,
                    currentBid = 72000,
                    seller = com.example.onlinebidding.model.Seller("Samsung Official", verified = true, rating = 4.8),
                    specs = mapOf("Processor" to "Snapdragon 8 Gen 2", "Storage" to "256GB")
                ),
                com.example.onlinebidding.model.Product(
                    id = 3,
                    name = "Microsoft Surface Pro 9",
                    imageRes = R.drawable.ic_surfacetablet,
                    currentBid = 58000,
                    seller = com.example.onlinebidding.model.Seller("Microsoft Store", verified = true, rating = 4.6),
                    specs = mapOf("Processor" to "Intel Core i7", "Storage" to "512GB SSD")
                )
            )
            TabletListScreen(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        /* ---------- TABLET DETAILS ---------- */

        composable("tablet_details/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val tablets = listOf(
                ProductDetails(
                    name = "iPad Pro 12.9\" M2",
                    imageRes = R.drawable.ic_ipadtablet,
                    currentBid = 85000,
                    seller = SellerDetails(
                        name = "Apple Store",
                        verified = true,
                        rating = 4.9
                    ),
                    endTime = 3600,
                    registeredBidders = 26,
                    specs = mapOf(
                        "Display" to "12.9\" Retina XDR",
                        "Storage" to "512GB",
                        "Processor" to "M2 Chip"
                    ),
                    condition = "New",
                    conditionDetails = "Latest iPad Pro with M2 chip and stunning display."
                ),
                ProductDetails(
                    name = "Samsung Galaxy Tab S9",
                    imageRes = R.drawable.ic_samsungtablet,
                    currentBid = 72000,
                    seller = SellerDetails(
                        name = "Samsung Official",
                        verified = true,
                        rating = 4.8
                    ),
                    endTime = 4200,
                    registeredBidders = 18,
                    specs = mapOf(
                        "Processor" to "Snapdragon 8 Gen 2",
                        "Storage" to "256GB",
                        "Display" to "11\" AMOLED"
                    ),
                    condition = "New",
                    conditionDetails = "Premium Android tablet with S Pen included."
                ),
                ProductDetails(
                    name = "Microsoft Surface Pro 9",
                    imageRes = R.drawable.ic_surfacetablet,
                    currentBid = 58000,
                    seller = SellerDetails(
                        name = "Microsoft Store",
                        verified = true,
                        rating = 4.6
                    ),
                    endTime = 4800,
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
                navController = navController,
                index = index,
                onBack = { navController.popBackStack() }
            )
        }

        /* ---------- PROFILE ---------- */

        composable("profile") {
            ProfileScreen(
                userData = UserData(
                    name = authState.name ?: "User",
                    email = authState.email ?: "user@email.com",
                    phone = authState.phone,
                    totalBids = 18,
                    wins = 3,
                    credits = 120
                ),
                onBack = { navController.popBackStack() },
                onLogout = {
                    authViewModel.logout()
                    navController.navigate("login") {
                        popUpTo("login") { inclusive = true }
                    }
                }
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

        /* ---------- CREDITS PAYMENT FLOW ---------- */

        composable("credits/{type}/{index}/{itemName}") { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "laptop"
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val itemName = backStackEntry.arguments?.getString("itemName") ?: ""

            // Get product price and calculate credits dynamically
            val productPrice = getProductPrice(type, index)
            val requiredCredits = calculateCredits(productPrice)
            val creditPrice = 10
            val totalCost = requiredCredits * creditPrice

            CreditsScreen(
                itemName = itemName,
                requiredCredits = requiredCredits,
                creditPrice = creditPrice,
                onBack = { navController.popBackStack() },
                onPay = {
                    navController.navigate("payment_method/$totalCost/$itemName/$type/$index")
                }
            )
        }

        composable("payment_method/{amount}/{itemName}/{type}/{index}") { backStackEntry ->
            val amount = backStackEntry.arguments?.getString("amount")?.toIntOrNull() ?: 100
            val itemName = backStackEntry.arguments?.getString("itemName") ?: ""
            val type = backStackEntry.arguments?.getString("type") ?: "laptop"
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0

            PaymentMethodScreen(
                amount = amount,
                onBack = { navController.popBackStack() },
                onPay = { method ->
                    navController.navigate("upi_entry/$method/$amount/$itemName/$type/$index")
                }
            )
        }

        composable("upi_entry/{method}/{amount}/{itemName}/{type}/{index}") { backStackEntry ->
            val method = backStackEntry.arguments?.getString("method") ?: "phonepe"
            val amount = backStackEntry.arguments?.getString("amount")?.toIntOrNull() ?: 100
            val itemName = backStackEntry.arguments?.getString("itemName") ?: ""
            val type = backStackEntry.arguments?.getString("type") ?: "laptop"
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0

            UPIEntryScreen(
                paymentMethod = method,
                amount = amount,
                onBack = { navController.popBackStack() },
                onProceed = { upiId ->
                    navController.navigate("payment_processing/$method/$itemName/$type/$index")
                },
                auctionId = null, // Credits payment - no auction_id
                userId = 25, // TODO: Get from logged in user session
                saveToDatabase = true // ✅ Automatically save payment
            )
        }

        composable("payment_processing/{method}/{itemName}/{type}/{index}") { backStackEntry ->
            val method = backStackEntry.arguments?.getString("method") ?: "phonepe"
            val itemName = backStackEntry.arguments?.getString("itemName") ?: ""
            val type = backStackEntry.arguments?.getString("type") ?: "laptop"
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0

            PaymentProcessingScreen(
                method = method,
                onFinished = {
                    // Mark credits as paid
                    val itemId = when (type) {
                        "laptop" -> "laptop_$index"
                        "monitor" -> "monitor_$index"
                        "computer" -> "computer_$index"
                        else -> "${type}_$index"
                    }
                    CreditsState.setCreditsPaid(itemId)
                    navController.navigate("payment_success/$itemName/$type/$index") {
                        // Pop back to the list screen, removing all payment flow screens
                        when (type) {
                            "laptop" -> popUpTo(route = "laptop_list") { inclusive = false }
                            "monitor" -> popUpTo(route = "monitor_list") { inclusive = false }
                            "computer" -> popUpTo(route = "computer_list") { inclusive = false }
                            "mobile" -> popUpTo(route = "mobile_list") { inclusive = false }
                            "tablet" -> popUpTo(route = "tablet_list") { inclusive = false }
                            else -> popUpTo(route = "dashboard") { inclusive = false }
                        }
                    }
                }
            )
        }

        composable("payment_success/{itemName}/{type}/{index}") { backStackEntry ->
            val itemName = backStackEntry.arguments?.getString("itemName") ?: ""
            val type = backStackEntry.arguments?.getString("type") ?: "laptop"
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0

            PaymentSuccessScreen(
                itemName = itemName,
                onStartBid = {
                    when (type) {
                        "laptop" -> navController.navigate("auction_detail/$index") {
                            popUpTo(route = "laptop_list") { inclusive = false }
                        }
                        "monitor" -> navController.navigate("monitor_auction_detail/$index/$itemName") {
                            popUpTo(route = "monitor_list") { inclusive = false }
                        }
                        "computer" -> navController.navigate("computer_auction_detail/$index") {
                            popUpTo(route = "computer_list") { inclusive = false }
                        }
                        "mobile" -> navController.navigate("mobile_auction_detail/$index") {
                            popUpTo(route = "mobile_list") { inclusive = false }
                        }
                        "tablet" -> navController.navigate("tablet_auction_detail/$index") {
                            popUpTo(route = "tablet_list") { inclusive = false }
                        }
                        else -> navController.popBackStack()
                    }
                }
            )
        }

        /* ---------- BID COMMENTS ---------- */

        composable("bid_comments/{index}") { backStackEntry ->
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val auctionId = backStackEntry.arguments?.getString("auctionId")?.toIntOrNull()
            val laptops = listOf(
                "MacBook Pro 16\" M3 Max",
                "Dell XPS 15 OLED",
                "ASUS ROG Zephyrus G16"
            )
            BidCommentsScreen(
                itemName = laptops.getOrElse(index) { laptops[0] },
                laptopIndex = index,
                auctionId = auctionId,
                onBack = { navController.popBackStack() },
                onAddBid = { /* Handle add bid */ },
                onTimeUp = { winnerName, winnerBid ->
                    val auctionIdParam = if (auctionId != null) "/$auctionId" else ""
                    navController.navigate("auction_winner/${laptops.getOrElse(index) { laptops[0] }}/${java.net.URLEncoder.encode(winnerName, "UTF-8")}/$winnerBid$auctionIdParam")
                }
            )
        }

        composable("bid_comments/{type}/{index}") { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "laptop"
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val auctionId = backStackEntry.arguments?.getString("auctionId")?.toIntOrNull()
            val itemNames = when (type) {
                "mobile" -> listOf("iPhone 15 Pro Max", "Samsung Galaxy S24 Ultra", "OnePlus 12 Pro")
                "tablet" -> listOf("iPad Pro 12.9\" M2", "Samsung Galaxy Tab S9", "Microsoft Surface Pro 9")
                "computer" -> listOf("Custom Gaming PC RTX", "Mac Studio M2 Ultra", "HP Z8 G5 Workstation")
                "monitor" -> listOf("LG UltraGear 27\"", "Samsung Odyssey G7", "Dell UltraSharp")
                else -> listOf("MacBook Pro 16\" M3 Max", "Dell XPS 15 OLED", "ASUS ROG Zephyrus G16")
            }
            BidCommentsScreen(
                itemName = itemNames.getOrElse(index) { itemNames[0] },
                laptopIndex = index,
                deviceType = type,
                auctionId = auctionId,
                onBack = { navController.popBackStack() },
                onAddBid = { /* Handle add bid */ },
                onTimeUp = { winnerName, winnerBid ->
                    val auctionIdParam = if (auctionId != null) "/$auctionId" else ""
                    navController.navigate("auction_winner/${itemNames.getOrElse(index) { itemNames[0] }}/${java.net.URLEncoder.encode(winnerName, "UTF-8")}/$winnerBid$auctionIdParam")
                }
            )
        }

        composable("bid_comments/{type}/{index}/{auctionId}") { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "laptop"
            val index = backStackEntry.arguments?.getString("index")?.toIntOrNull() ?: 0
            val auctionId = backStackEntry.arguments?.getString("auctionId")?.toIntOrNull()
            val itemNames = when (type) {
                "mobile" -> listOf("iPhone 15 Pro Max", "Samsung Galaxy S24 Ultra", "OnePlus 12 Pro")
                "tablet" -> listOf("iPad Pro 12.9\" M2", "Samsung Galaxy Tab S9", "Microsoft Surface Pro 9")
                "computer" -> listOf("Custom Gaming PC RTX", "Mac Studio M2 Ultra", "HP Z8 G5 Workstation")
                "monitor" -> listOf("LG UltraGear 27\"", "Samsung Odyssey G7", "Dell UltraSharp")
                else -> listOf("MacBook Pro 16\" M3 Max", "Dell XPS 15 OLED", "ASUS ROG Zephyrus G16")
            }
            BidCommentsScreen(
                itemName = itemNames.getOrElse(index) { itemNames[0] },
                laptopIndex = index,
                deviceType = type,
                auctionId = auctionId,
                onBack = { navController.popBackStack() },
                onAddBid = { /* Handle add bid */ },
                onTimeUp = { winnerName, winnerBid ->
                    val auctionIdParam = if (auctionId != null) "/$auctionId" else ""
                    navController.navigate("auction_winner/${itemNames.getOrElse(index) { itemNames[0] }}/${java.net.URLEncoder.encode(winnerName, "UTF-8")}/$winnerBid$auctionIdParam")
                }
            )
        }

        /* ---------- AUCTION WINNER ---------- */

        composable("auction_winner/{itemName}/{winnerName}/{winningBid}") { backStackEntry ->
            val itemName = backStackEntry.arguments?.getString("itemName") ?: ""
            val winnerName = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("winnerName") ?: "", "UTF-8")
            val winningBid = backStackEntry.arguments?.getString("winningBid") ?: "₹0"

            // Extract amount from winningBid (remove ₹ and commas)
            val amount = winningBid.replace("₹", "").replace(",", "").toIntOrNull() ?: 0

            AuctionWinnerScreen(
                itemName = itemName,
                winnerName = winnerName,
                winningBid = winningBid,
                onProceedToPayment = {
                    navController.navigate("last_payment/$amount")
                }
            )
        }

        composable("auction_winner/{itemName}/{winnerName}/{winningBid}/{auctionId}") { backStackEntry ->
            val itemName = backStackEntry.arguments?.getString("itemName") ?: ""
            val winnerName = java.net.URLDecoder.decode(backStackEntry.arguments?.getString("winnerName") ?: "", "UTF-8")
            val winningBid = backStackEntry.arguments?.getString("winningBid") ?: "₹0"
            val auctionId = backStackEntry.arguments?.getString("auctionId")?.toIntOrNull()

            // Extract amount from winningBid (remove ₹ and commas)
            val amount = winningBid.replace("₹", "").replace(",", "").toIntOrNull() ?: 0

            AuctionWinnerScreen(
                itemName = itemName,
                winnerName = winnerName,
                winningBid = winningBid,
                onProceedToPayment = {
                    navController.navigate("last_payment/$amount/$auctionId")
                }
            )
        }

        /* ---------- LAST PAYMENT (PAYMENT METHOD) ---------- */

        composable("last_payment/{amount}") { backStackEntry ->
            val amount = backStackEntry.arguments?.getString("amount")?.toIntOrNull() ?: 0

            LastPaymentScreen(
                onBack = { navController.popBackStack() },
                onPaymentMethodSelected = { method ->
                    navController.navigate("upi_entry_payment/$method/$amount")
                }
            )
        }

        composable("last_payment/{amount}/{auctionId}") { backStackEntry ->
            val amount = backStackEntry.arguments?.getString("amount")?.toIntOrNull() ?: 0
            val auctionId = backStackEntry.arguments?.getString("auctionId")?.toIntOrNull()

            LastPaymentScreen(
                onBack = { navController.popBackStack() },
                onPaymentMethodSelected = { method ->
                    navController.navigate("upi_entry_payment/$method/$amount/$auctionId")
                }
            )
        }

        composable("last_payment") {
            LastPaymentScreen(
                onBack = { navController.popBackStack() },
                onPaymentMethodSelected = { method ->
                    navController.navigate("upi_entry_payment/$method/0")
                }
            )
        }

        /* ---------- UPI ENTRY FOR PAYMENT ---------- */

        composable("upi_entry_payment/{method}/{amount}") { backStackEntry ->
            val method = backStackEntry.arguments?.getString("method") ?: "phonepe"
            val amount = backStackEntry.arguments?.getString("amount")?.toIntOrNull() ?: 0

            UPIEntryScreen(
                paymentMethod = method,
                amount = amount,
                onBack = { navController.popBackStack() },
                onProceed = { upiId ->
                    navController.navigate("payment_success_logout") {
                        popUpTo(route = "last_payment") { inclusive = false }
                    }
                },
                auctionId = null, // No auction_id for this flow
                userId = 25, // TODO: Get from logged in user session
                saveToDatabase = true // ✅ Automatically save payment
            )
        }

        composable("upi_entry_payment/{method}/{amount}/{auctionId}") { backStackEntry ->
            val method = backStackEntry.arguments?.getString("method") ?: "phonepe"
            val amount = backStackEntry.arguments?.getString("amount")?.toIntOrNull() ?: 0
            val auctionId = backStackEntry.arguments?.getString("auctionId")?.toIntOrNull()

            UPIEntryScreen(
                paymentMethod = method,
                amount = amount,
                onBack = { navController.popBackStack() },
                onProceed = { upiId ->
                    navController.navigate("payment_success_logout") {
                        popUpTo(route = "last_payment") { inclusive = false }
                    }
                },
                auctionId = auctionId, // ✅ Pass auction_id for auction winner payments
                userId = 25, // TODO: Get from logged in user session
                saveToDatabase = true // ✅ Automatically save payment
            )
        }

        composable("upi_entry_payment/{method}") { backStackEntry ->
            val method = backStackEntry.arguments?.getString("method") ?: "phonepe"

            UPIEntryScreen(
                paymentMethod = method,
                amount = 0,
                onBack = { navController.popBackStack() },
                onProceed = { upiId ->
                    navController.navigate("payment_success_logout") {
                        popUpTo(route = "last_payment") { inclusive = false }
                    }
                },
                auctionId = null,
                userId = 25, // TODO: Get from logged in user session
                saveToDatabase = true // ✅ Automatically save payment
            )
        }

        /* ---------- PAYMENT SUCCESS LOGOUT ---------- */

        composable("payment_success_logout") {
            LogoutScreen(
                onLogout = {
                    navController.navigate("splash") {
                        popUpTo(route = "splash") { inclusive = true }
                    }
                }
            )
        }
    }
}
