package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.BodoCalendarViewModel
import com.example.ui.components.AdminPanelDialog
import com.example.ui.components.AdMobBanner
import com.example.ui.components.AppDownloadDialog
import com.example.ui.components.AppSidebarDrawer
import com.example.ui.components.AronaiHeader
import com.example.ui.components.BodoCalendarGrid
import com.example.ui.components.LegalInfoDialog
import com.example.ui.components.LiveHistoryTicker
import com.example.ui.components.LiveMetricsBar
import com.example.ui.components.MonthExplanationDialog
import com.example.ui.components.SendFeedbackEmailDialog
import com.example.ui.components.ThreeDayForecastCard
import com.example.ui.components.UpdateAvailableDialog
import com.example.ui.theme.AronaiGold
import com.example.ui.theme.AronaiGoldLight
import com.example.ui.theme.AronaiNavy
import com.example.ui.theme.AronaiTextSecondary
import com.example.ui.theme.AronaiWarmWhite
import com.example.ui.theme.MyApplicationTheme
import com.google.android.gms.ads.MobileAds
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    // Initialize Google Mobile Ads SDK
    MobileAds.initialize(this) {}

    setContent {
      MyApplicationTheme(darkTheme = true) {
        val viewModel: BodoCalendarViewModel = viewModel()
        val uiState by viewModel.uiState.collectAsStateWithLifecycle()

        val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
        val coroutineScope = rememberCoroutineScope()

        var feedbackCategory by remember { mutableStateOf<String?>(null) }
        var feedbackSubtitle by remember { mutableStateOf("") }
        var showPrivacyDialog by remember { mutableStateOf(false) }
        var showTermsDialog by remember { mutableStateOf(false) }

        ModalNavigationDrawer(
          drawerState = drawerState,
          drawerContent = {
            ModalDrawerSheet(
              drawerContainerColor = Color(0xFF0F172A),
              drawerContentColor = AronaiWarmWhite
            ) {
              AppSidebarDrawer(
                onClose = {
                  coroutineScope.launch { drawerState.close() }
                },
                onOpenFeedback = { category, subtitle ->
                  feedbackCategory = category
                  feedbackSubtitle = subtitle
                },
                onOpenPrivacyPolicy = {
                  showPrivacyDialog = true
                },
                onOpenTerms = {
                  showTermsDialog = true
                }
              )
            }
          }
        ) {
          Scaffold(
            modifier = Modifier
              .fillMaxSize()
              .testTag("bodo_calendar_scaffold"),
            containerColor = AronaiNavy
          ) { innerPadding ->
            Box(
              modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AronaiNavy)
            ) {
              Column(
                modifier = Modifier
                  .fillMaxSize()
                  .verticalScroll(rememberScrollState())
              ) {
                // 1. Cultural Header with Bagurumba Photo & Aronai Theme (with Hamburger Menu)
                AronaiHeader(
                  todayDate = uiState.todayBodoDate,
                  currentRunningMonthEng = uiState.currentRunningMonthEng,
                  onOpenMonthInfo = { viewModel.toggleMonthExplanation(true) },
                  onOpenAdminPanel = { viewModel.toggleAdminDialog(true) },
                  onOpenAppDownload = { viewModel.toggleAppDownloadDialog(true) },
                  onOpenSidebar = {
                    coroutineScope.launch { drawerState.open() }
                  },
                  modifier = Modifier.testTag("aronai_header")
                )

              // 2. Live Today In History / System Research Ticker (with delete message option)
              LiveHistoryTicker(
                messages = uiState.liveMessages,
                currentIndex = uiState.liveTickerIndex,
                onDeleteMessage = { id -> viewModel.deleteMessage(id) },
                onOpenAdminPanel = { viewModel.toggleAdminDialog(true) },
                modifier = Modifier.testTag("live_history_ticker")
              )

              // 3. Three-Day Solar Sync: Today, Tomorrow, Day After (Assamese solar sync in Bodo & English)
              ThreeDayForecastCard(
                todayDate = uiState.todayBodoDate,
                tomorrowDate = uiState.tomorrowBodoDate,
                dayAfterDate = uiState.dayAfterBodoDate,
                todayEvents = uiState.todayEvents,
                tomorrowEvents = uiState.tomorrowEvents,
                dayAfterEvents = uiState.dayAfterEvents,
                onDeleteEvent = { id -> viewModel.deleteMessage(id) },
                modifier = Modifier.testTag("three_day_forecast_card")
              )

              // 4. Monthly Calendar Grid (BODO DATE LARGE, ENGLISH DATE SMALL)
              BodoCalendarGrid(
                displayedYear = uiState.displayedYear,
                displayedMonth = uiState.displayedMonth,
                monthDays = uiState.monthDays,
                selectedDate = uiState.selectedDate,
                onDateSelected = { date -> viewModel.selectDate(date) },
                onPrevMonth = { viewModel.previousMonth() },
                onNextMonth = { viewModel.nextMonth() },
                onGoToToday = { viewModel.goToToday() },
                onOpenMonthInfo = { viewModel.toggleMonthExplanation(true) },
                modifier = Modifier.testTag("bodo_calendar_grid")
              )

              // 5. Google AdMob Banner Ad (User App ID & Banner Unit ID)
              AdMobBanner(
                modifier = Modifier.testTag("admob_banner_ad")
              )

              // 6. Live App Downloads & Active Users Metrics
              LiveMetricsBar(
                modifier = Modifier.testTag("live_metrics_bar")
              )

              // 7. Cultural Footer with Traditional Aronai Banner
              CulturalFooter(modifier = Modifier.testTag("cultural_footer"))

              Spacer(modifier = Modifier.height(24.dp))
            }

            // Dialogs
            if (uiState.showMonthExplanationDialog) {
              MonthExplanationDialog(
                todayDate = uiState.todayBodoDate,
                currentRunningMonthEng = uiState.currentRunningMonthEng,
                onDismiss = { viewModel.toggleMonthExplanation(false) }
              )
            }

            if (uiState.showAdminDialog) {
              AdminPanelDialog(
                onPublish = { title, bodoTitle, cat, content, bodoContent, author ->
                  viewModel.publishAdminAnnouncement(title, bodoTitle, cat, content, bodoContent, author)
                },
                publishStatus = uiState.adminPublishStatus,
                onDismiss = { viewModel.toggleAdminDialog(false) }
              )
            }

            if (uiState.showAppDownloadDialog) {
              AppDownloadDialog(
                onDismiss = { viewModel.toggleAppDownloadDialog(false) }
              )
            }

            // In-App Auto Update Popup Dialog for existing users
            uiState.availableUpdate?.let { updateInfo ->
              UpdateAvailableDialog(
                updateInfo = updateInfo,
                onDismiss = { viewModel.dismissUpdateDialog() }
              )
            }

            // User Support / Feedback Dialog (Chat, Issue, Suggestion sent directly to sboro3235@gmail.com)
            feedbackCategory?.let { category ->
              SendFeedbackEmailDialog(
                category = category,
                subtitle = feedbackSubtitle,
                onDismiss = { feedbackCategory = null }
              )
            }

            // Legal: Privacy Policy
            if (showPrivacyDialog) {
              LegalInfoDialog(
                title = "Privacy Policy",
                content = "1. Local Account Security: Bodo San Calendar stores your account information locally on your device memory. We do not sell or track personal data.\n\n2. Direct Communication: Feedback, issues, and inquiries submitted through this app are delivered directly to the designated support mailbox: sboro3235@gmail.com.\n\n3. Safety: This application does not require any dangerous runtime permissions.",
                onDismiss = { showPrivacyDialog = false }
              )
            }

            // Legal: Terms & Conditions
            if (showTermsDialog) {
              LegalInfoDialog(
                title = "Terms & Conditions",
                content = "1. Cultural Respect: Users agree to respect Bathou traditions and the cultural identity of Bodoland.\n\n2. Calendar Accuracy: Dates are calculated per Assam Solar Calendar / Bodo San principles synchronized to Sankranti transitions.\n\n3. Support Contact: For questions, bug reports, and inquiries, email sboro3235@gmail.com.",
                onDismiss = { showTermsDialog = false }
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
fun CulturalFooter(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 8.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Aronai texture banner strip
    Image(
      painter = painterResource(id = R.drawable.img_aronai_pattern),
      contentDescription = "Aronai Traditional Motif Strip",
      modifier = Modifier
        .fillMaxWidth()
        .height(34.dp),
      contentScale = ContentScale.Crop
    )

    Spacer(modifier = Modifier.height(8.dp))

    Text(
      text = "बड़ो सान केलेंडार • Bodo San Calendar & Cultural Archive",
      color = AronaiGold,
      fontSize = 11.sp,
      fontWeight = FontWeight.Bold
    )

    Spacer(modifier = Modifier.height(3.dp))

    Text(
      text = "Developed by Swrang Swargiary",
      color = AronaiGoldLight,
      fontSize = 11.sp,
      fontWeight = FontWeight.SemiBold
    )

    Text(
      text = "Preserving Bodoland Heritage • Support: sboro3235@gmail.com",
      color = AronaiTextSecondary,
      fontSize = 9.5.sp
    )
  }
}

