package com.anytypeio.anytype.core_ui.features.fields

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.anytypeio.anytype.core_ui.R
import com.anytypeio.anytype.core_ui.common.DefaultPreviews
import com.anytypeio.anytype.core_ui.views.Relations1

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FieldTypeCheckbox(
    modifier: Modifier = Modifier,
    title: String,
    isCheck: Boolean,
    isLocal: Boolean,
    onFieldClick: () -> Unit,
    onAddToCurrentTypeClick: () -> Unit,
    onRemoveFromObjectClick: () -> Unit,
) {
    val isMenuExpanded = remember { mutableStateOf(false) }
    val defaultModifier = modifier
        .combinedClickable(
            onClick = { onFieldClick()},
            onLongClick = {
                if (isLocal) isMenuExpanded.value = true
            }
        )
        .fillMaxWidth()
        .border(
            width = 1.dp,
            color = colorResource(id = R.color.shape_secondary),
            shape = RoundedCornerShape(12.dp)
        )
        .padding(vertical = 16.dp)
        .padding(horizontal = 16.dp)

    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val halfScreenWidth = screenWidth / 2 - 32.dp

    Row(
        modifier = defaultModifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .widthIn(max = halfScreenWidth)
                .wrapContentHeight()
                .padding(vertical = 2.dp)
        ) {
            Text(
                text = title,
                style = Relations1,
                color = colorResource(id = R.color.text_secondary),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        Box(
            modifier = Modifier.widthIn(max = halfScreenWidth)
        ) {
            if (isCheck) {
                Image(
                    painter = painterResource(id = R.drawable.ic_checkbox_checked),
                    contentDescription = "Checkbox",
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_checkbox_default),
                    contentDescription = "Checkbox",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        FieldItemDropDownMenu(
            showMenu = isMenuExpanded.value,
            onDismissRequest = {
                isMenuExpanded.value = false
            },
            onAddToCurrentTypeClick = {
                isMenuExpanded.value = false
                onAddToCurrentTypeClick()
            },
            onRemoveFromObjectClick = {
                isMenuExpanded.value = false
                onRemoveFromObjectClick()
            }
        )
    }
}


@DefaultPreviews
@Composable
fun FieldTypeCheckboxPreview() {
    FieldTypeCheckbox(
        title = "Creation date",
        isCheck = false,
        isLocal = true,
        onRemoveFromObjectClick = {},
        onAddToCurrentTypeClick = {},
        onFieldClick = {}
    )
}