package com.example.onlinebidding.navigation

import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.onlinebidding.screens.splash.SplashScreen
import com.example.onlinebidding.screens.splash.GetStartedScreen
import com.example.onlinebidding.screens.splash.BrandingCarousel
import com.example.onlinebidding.screens.login.*
import com.example.onlinebidding.screens.welcome.Welcome
import com.example.onlinebidding.screens.interest.InterestSelection
import com.example.onlinebidding.screens.products.*
import com.example.onlinebidding.screens.dashboard.*
import com.example.onlinebidding.screens.profile.*
import com.example.onlinebidding.screens.trending.TrendingAuctions
import com.example.onlinebidding.screens.flash.FlashAuctions
import com.example.onlinebidding.screens.search.Search
import com.example.onlinebidding.ui.viewmodel.AuthViewModel

@Composable
fun AppNavHost() {

    val navController = rememberNavController()

    // frontend-only auth
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.uiState.collectAsState()

    var userName by remember { mutableStateOf("User") }
    var userEmail by remember { mutableStateOf("user@email.com") }

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

        composable("interest") {
            InterestSelection(
                onComplete = { navController.navigate("notification") },
                onNavigateToLaptopList = { navController.navigate("laptop_list") },
                onNavigateToMobileList = { navController.navigate("mobile_list") },
                onNavigateToComputerList = {},
                onNavigateToMonitorList = {},
                onNavigateToTabletList = {}
            )
        }

        composable("notification") {
            NotificationPermission {
                navController.navigate("dashboard_loading") {
                    popUpTo("notification") { inclusive = true }
                }
            }
        }

        composable("dashboard_loading") {
            DashboardLoading {
                navController.navigate("dashboard") {
                    popUpTo("dashboard_loading") { inclusive = true }
                }
            }
        }

        composable("dashboard") {
            MainDashboard(
                userName = userName,
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }

        // ✅ EXISTING
        composable("laptop_list") {
            LaptopList(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        // ✅ ADDED FOR MOBILES (ONLY REQUIRED CHANGE)
        composable("mobile_list") {
            MobileList(
                navController = navController,
                onBack = { navController.popBackStack() }
            )
        }

        composable(AUCTION_DETAILS_ROUTE) {
            AuctionDetailsScreen(
                onBack = { navController.popBackStack() }
            )
        }

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
