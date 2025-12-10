package com.kontranik.easycycle.ui.shared

import androidx.compose.foundation.gestures.FlingBehavior
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.CompositingStrategy
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NumberPicker(
    modifier: Modifier = Modifier,
    list: List<String>,
    fontSize: TextUnit,
    state: LazyListState,
    flingBehavior: FlingBehavior
) {

    Box(
        modifier = modifier
    )
    {
        val shownCount = 3 + 1
        val height = with(LocalDensity.current) {
            fontSize.toDp()
        }
        LazyColumn(
            state = state,
            flingBehavior = flingBehavior,
            modifier = Modifier
                .padding(5.dp)
                .height(height * shownCount)
                .graphicsLayer { compositingStrategy = CompositingStrategy.Offscreen }
                .drawWithContent {
                    drawContent()
                    drawRect(
                        brush = Brush.verticalGradient(
                            0f to Color.Transparent,
                            0.5f to Color.Black,
                            1f to Color.Transparent
                        ),
                        blendMode = BlendMode.DstIn
                    )
                }) {
            items(list.size) { index ->
            //if first item then add end empty slot in the beginning so the first item can be scrolled to center of screen
                if(index == 0){
                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = " ", fontSize = fontSize
                    )
                } else {
                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = list[index], fontSize = fontSize
                    )
                }
                //if end of list then create a empty slot so the last item can scroll to center of screen
                if(index == list.size-1){
                    Text(
                        modifier = Modifier.padding(5.dp),
                        text = " ", fontSize = fontSize
                    )
                }

            }
        }
    }
}

@Preview
@Composable
private fun NumberPickerPreview() {
    val lazyListState = rememberLazyListState(initialFirstVisibleItemIndex = 5)
    var values = remember {
        (0..20).map { it.toString() }
    }

    Surface() {
        NumberPicker(
            modifier = Modifier,
            list = values,
            fontSize = 32.sp,
            state = lazyListState,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = lazyListState)
        )
    }

}