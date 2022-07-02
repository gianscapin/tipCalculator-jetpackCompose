package com.gscapin.tipapp

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gscapin.tipapp.components.InputField
import com.gscapin.tipapp.ui.theme.TipAppTheme
import com.gscapin.tipapp.widgets.RoundIconButton

class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalComposeUiApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TipAppTheme {
                MyApp {
                    //TopHeader(200.0)
                    MainContent()
                }
            }
        }
    }
}

@Composable
fun MyApp(content: @Composable () -> Unit) {

    TipAppTheme {
        Surface(color = MaterialTheme.colors.background) {
            content()
        }
    }
}

@Composable
fun TopHeader(totalTipPerPerson: Double = 0.0) {
    androidx.compose.material.Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
            .height(150.dp)
            .clip(RoundedCornerShape(CornerSize(12.dp))),
        color = Color(0xFFB8CCD8)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(text = "Total Por Persona", style = MaterialTheme.typography.h6)

            val total = "%.2f".format(totalTipPerPerson)
            Text(
                text = "$$total", style = MaterialTheme.typography.h4,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@ExperimentalComposeUiApi
@Preview
@Composable
fun MainContent() {

    val splitByState = remember {
        mutableStateOf(1)
    }

    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }

    Column(modifier = Modifier.padding(all = 12.dp)) {
        BillForm(splitByState = splitByState, totalPerPerson = totalPerPersonState){}
    }
}

@ExperimentalComposeUiApi
@Composable
fun BillForm(
    modifier: Modifier = Modifier,
    splitByState: MutableState<Int>,
    totalPerPerson: MutableState<Double>,
    onValueChange: (String) -> Unit = {}
) {
    val totalBillState = remember {
        mutableStateOf("")
    }

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotBlank()
    }


    val keyBoardController = LocalSoftwareKeyboardController.current


    val sliderPositionState = remember {
        mutableStateOf(1f)
    }


    val tipPorcentage = (sliderPositionState.value * 100).toInt()



    TopHeader(totalPerPerson.value)
    androidx.compose.material.Surface(
        modifier = Modifier
            .padding(2.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(
            CornerSize(8.dp)
        ),
        border = BorderStroke(width = 1.dp, color = Color.LightGray)
    ) {
        Column(
            modifier = Modifier.padding(6.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            InputField(
                valueState = totalBillState,
                labelId = "Ingrese total",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions

                    onValueChange(totalBillState.value.trim())
                    keyBoardController?.hide()
                })
            if (validState) {
                Row(modifier = Modifier.padding(3.dp), horizontalArrangement = Arrangement.Start) {
                    Text(
                        text = "Separar en",
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                    Spacer(modifier = Modifier.width(120.dp))
                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {

                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                if(splitByState.value>1){
                                    splitByState.value--
                                    totalPerPerson.value = tipPerPerson(totalBillState.value.toDouble(), tipPorcentage, splitByState.value)
                                }
                            })

                        Text(
                            text = "${splitByState.value}", modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                        )

                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                splitByState.value++
                                totalPerPerson.value = tipPerPerson(totalBillState.value.toDouble(), tipPorcentage, splitByState.value)
                            })
                    }
                }


                Row(modifier = Modifier.padding(horizontal = 3.dp, vertical = 12.dp)) {
                    Text(text = "Propina", modifier = Modifier.align(Alignment.CenterVertically))

                    Spacer(modifier = Modifier.width(200.dp))

                    Text(text = "${calculateTotalTip(bill = totalBillState.value.toDouble(), percentaje = tipPorcentage)}", modifier = Modifier.align(Alignment.CenterVertically))
                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "$tipPorcentage %")

                    Spacer(modifier = Modifier.height(14.dp))

                    Slider(value = sliderPositionState.value, onValueChange = { newVal ->
                        sliderPositionState.value = newVal
                        totalPerPerson.value = tipPerPerson(totalBillState.value.toDouble(), tipPorcentage, splitByState.value)
                    }, modifier = Modifier.padding(start = 16.dp, end = 16.dp), steps = 19)
                }




            } else {
                Box() {

                }
            }
        }
    }
}

fun calculateTotalTip(bill: Double, percentaje: Int): Double {
    return if(bill>1 && bill.toString().isNotEmpty())
            (bill * percentaje) / 100 else 0.0
}

fun tipPerPerson(bill:Double, percentaje: Int, split: Int): Double{
    val totalTip = calculateTotalTip(bill, percentaje) + bill

    return if(split>=1)
        (totalTip / split) else 0.0
}
