package com.anytypeio.anytype.ui.onboarding.screens.signup

import android.os.Build
import android.os.Build.VERSION_CODES
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.anytypeio.anytype.R
import com.anytypeio.anytype.core_models.Id
import com.anytypeio.anytype.core_ui.ColorBackgroundField
import com.anytypeio.anytype.core_ui.ColorButtonRegular
import com.anytypeio.anytype.core_ui.OnBoardingTextPrimaryColor
import com.anytypeio.anytype.core_ui.OnBoardingTextSecondaryColor
import com.anytypeio.anytype.core_ui.extensions.conditional
import com.anytypeio.anytype.core_ui.foundation.noRippleClickable
import com.anytypeio.anytype.core_ui.views.BodyRegular
import com.anytypeio.anytype.core_ui.views.ButtonSize
import com.anytypeio.anytype.core_ui.views.Caption1Regular
import com.anytypeio.anytype.core_ui.views.HeadlineHeading
import com.anytypeio.anytype.core_ui.views.HeadlineOnBoardingDescription
import com.anytypeio.anytype.core_ui.views.OnBoardingButtonPrimary
import com.anytypeio.anytype.core_ui.views.OnBoardingButtonSecondary
import com.anytypeio.anytype.presentation.onboarding.signup.OnboardingMnemonicViewModel
import com.anytypeio.anytype.ui.onboarding.MnemonicPhraseWidget
import com.anytypeio.anytype.ui.onboarding.MnemonicStub

@Composable
fun MnemonicPhraseScreenWrapper(
    space: Id,
    startingObject: Id?,
    viewModel: OnboardingMnemonicViewModel,
    copyMnemonicToClipboard: (String) -> Unit,
    vm: OnboardingMnemonicViewModel,
    mnemonicColorPalette: List<Color>
) {
    val state = viewModel.state.collectAsStateWithLifecycle().value
    MnemonicPhraseScreen(
        state = state,
        reviewMnemonic = { viewModel.openMnemonic() },
        onCheckLaterClicked = {
            vm.onCheckLaterClicked(
                space = space,
                startingObject = startingObject
            )
        },
        copyMnemonicToClipboard = copyMnemonicToClipboard,
        mnemonicColorPalette = mnemonicColorPalette,
        onGoToAppClicked = {
            vm.onGoToTheAppClicked(
                space = space,
                startingObject = startingObject
            )
        }
    )
}



@Preview
@Composable
fun PreviewMnemonicPhraseScreen() {
    val fakeMnemonic = "One Two Three Four Five Six Seven Eight Nine Ten Eleven Twelve"
    MnemonicPhraseScreen(
        state = OnboardingMnemonicViewModel.State.MnemonicOpened(fakeMnemonic),
        reviewMnemonic = { /*TODO*/ },
        onCheckLaterClicked = { /*TODO*/ },
        copyMnemonicToClipboard = {},
        mnemonicColorPalette = emptyList(),
        onGoToAppClicked = {}
    )
}

@Preview
@Composable
fun PreviewMnemonicPhraseScreen2() {
    val fakeMnemonic = "One Two Three Four Five Six Seven Eight Nine Ten Eleven Twelve"
    MnemonicPhraseScreen(
        state = OnboardingMnemonicViewModel.State.Mnemonic(fakeMnemonic),
        reviewMnemonic = { /*TODO*/ },
        onCheckLaterClicked = { /*TODO*/ },
        copyMnemonicToClipboard = {},
        mnemonicColorPalette = emptyList(),
        onGoToAppClicked = {}
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MnemonicPhraseScreen(
    state: OnboardingMnemonicViewModel.State,
    reviewMnemonic: () -> Unit,
    onCheckLaterClicked: () -> Unit,
    onGoToAppClicked: () -> Unit,
    copyMnemonicToClipboard: (String) -> Unit,
    mnemonicColorPalette: List<Color>
) {
    val showWhatIsRecoveryPhraseDialog = remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.Top
        ) {
            Spacer(modifier = Modifier.height(148.dp))
            MnemonicTitle()
            MnemonicDescription()
            ReadMoreButton(showWhatIsRecoveryPhraseDialog)
            MnemonicPhrase(
                state = state,
                copyMnemonicToClipboard = copyMnemonicToClipboard,
                mnemonicColorPalette = mnemonicColorPalette
            )
        }
        MnemonicButtons(
            modifier = Modifier.align(Alignment.BottomCenter),
            openMnemonic = reviewMnemonic,
            onCheckLaterClicked = onCheckLaterClicked,
            onGoToAppClicked = onGoToAppClicked,
            state = state
        )
    }
    if (showWhatIsRecoveryPhraseDialog.value) {
        ModalBottomSheet(
            containerColor = Color(0xFF1F1E1D),
            onDismissRequest = {
                showWhatIsRecoveryPhraseDialog.value = false
            },
            content = { WhatIsRecoveryPhraseScreen() },
            dragHandle = {
                // Do nothing
            },
            sheetState = SheetState(
                skipPartiallyExpanded = true,
                density = LocalDensity.current
            )
        )
    }
}

@Composable
private fun ReadMoreButton(showWhatIsRecoveryPhraseDialog: MutableState<Boolean>) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 16.dp)
            .height(24.dp)
            .noRippleClickable {
                showWhatIsRecoveryPhraseDialog.value = true
            }
    ) {
        Text(
            text = stringResource(id = R.string.onboarding_mnemonic_read_more),
            style = BodyRegular.copy(
                color = Color(0xFFDBDAD4)
            ),
            modifier = Modifier.align(Alignment.Center)
        )
    }
}

@Composable
fun MnemonicButtons(
    modifier: Modifier = Modifier,
    openMnemonic: () -> Unit,
    onCheckLaterClicked: () -> Unit,
    onGoToAppClicked: () -> Unit,
    state: OnboardingMnemonicViewModel.State
) {
    Column(modifier.wrapContentHeight()) {
        when (state) {
            is OnboardingMnemonicViewModel.State.MnemonicOpened -> {
                OnBoardingButtonPrimary(
                    text = stringResource(id = R.string.onboarding_enter_my_vault),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, bottom = 24.dp),
                    onClick = { onGoToAppClicked() },
                    size = ButtonSize.Large
                )
            }
            else -> {
                OnBoardingButtonPrimary(
                    text = stringResource(id = R.string.onboarding_tap_to_reveal),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    onClick = {
                        openMnemonic.invoke()
                    }, size = ButtonSize.Large
                )
                OnBoardingButtonSecondary(
                    text = stringResource(id = R.string.onboarding_key_not_now),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            top = 14.dp,
                            end = 16.dp,
                            bottom = 24.dp
                        ),
                    onClick = {
                        onCheckLaterClicked.invoke()
                    },
                    size = ButtonSize.Large,
                    textColor = ColorButtonRegular
                )
            }
        }
    }
}


@Composable
fun MnemonicTitle() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp)
            .wrapContentHeight(), contentAlignment = Alignment.Center
    ) {
        Text(
            modifier = Modifier,
            text = stringResource(R.string.onboarding_this_is_your_key_title),
            style = HeadlineHeading.copy(
                color = OnBoardingTextPrimaryColor
            ),
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun MnemonicPhrase(
    state: OnboardingMnemonicViewModel.State,
    copyMnemonicToClipboard: (String) -> Unit,
    mnemonicColorPalette: List<Color>
) {
    when (state) {
        is OnboardingMnemonicViewModel.State.Idle -> {}
        else -> {
            Column(
                Modifier
                    .wrapContentHeight()
                    .fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .background(color = ColorBackgroundField, shape = RoundedCornerShape(24.dp))
                        .wrapContentHeight()
                ) {
                    if (Build.VERSION.SDK_INT <= VERSION_CODES.R && state is OnboardingMnemonicViewModel.State.Mnemonic) {
                        MnemonicStub()
                    } else {
                        MnemonicPhraseWidget(
                            modifier = Modifier
                                .fillMaxWidth()
                                .conditional(
                                    condition = state is OnboardingMnemonicViewModel.State.Mnemonic,
                                    positive = { blur(15.dp) }
                                )
                                .padding(
                                    start = 16.dp,
                                    top = 16.dp,
                                    end = 16.dp,
                                    bottom = 16.dp
                                ),
                            mnemonic = state.mnemonicPhrase,
                            mnemonicColorPalette = mnemonicColorPalette
                        )
                    }
                }
                if (state is OnboardingMnemonicViewModel.State.MnemonicOpened) {
                    OnBoardingButtonSecondary(
                        text = stringResource(id = R.string.onboarding_key_copy),
                        modifier = Modifier
                            .align(CenterHorizontally)
                            .padding(bottom = 12.dp),
                        onClick = {
                            copyMnemonicToClipboard.invoke(state.mnemonicPhrase)
                        },
                        size = ButtonSize.SmallSecondary,
                        textColor = ColorButtonRegular
                    )
                }
            }

        }
    }
}

@Composable
fun MnemonicDescription() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .wrapContentHeight(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = stringResource(id = R.string.onboarding_key_description),
            style = HeadlineOnBoardingDescription.copy(
                color = OnBoardingTextSecondaryColor,
                textAlign = TextAlign.Center
            )
        )
    }
}