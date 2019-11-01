//******************************************************************************
// SCICHART® Copyright SciChart Ltd. 2011-2017. All rights reserved.
//
// Web: http://www.scichart.com
// Support: support@scichart.com
// Sales:   sales@scichart.com
//
// RsiViewModel.kt is part of the SCICHART® Showcases. Permission is hereby granted
// to modify, create derivative works, distribute and publish any part of this source
// code whether for commercial, private or personal use.
//
// The SCICHART® Showcases are distributed in the hope that they will be useful, but
// without any warranty. It is provided "AS IS" without warranty of any kind, either
// expressed or implied.
//******************************************************************************

package com.scichart.scishowcase.viewModels.trader

import android.content.Context
import com.scichart.charting.modifiers.*
import com.scichart.charting.visuals.axes.*
import com.scichart.charting.visuals.renderableSeries.FastLineRenderableSeries
import com.scichart.data.model.DoubleRange
import com.scichart.scishowcase.model.trader.TradeDataPoints
import com.scichart.scishowcase.utils.MovingAverage
import com.scichart.scishowcase.utils.XyDataSeries
import com.scichart.scishowcase.viewModels.ChartViewModel
import java.util.*

class RsiViewModel(context: Context, sharedXRange: DoubleRange, listener: OnAnnotationCreatedListener)
    : BaseChartPaneViewModel(context, AxisBase.DEFAULT_AXIS_ID, listener) {

    private val rsiDataSeries = XyDataSeries<Date, Double>().apply { acceptsUnsortedData = true }

    init {
        xAxes.add(CategoryDateAxis(context).apply {
            visibleRange = sharedXRange
        })
        yAxes.add(NumericAxis(context).apply {
            autoRange = AutoRange.Always
        })

        renderableSeries.add(FastLineRenderableSeries().apply {
            dataSeries = rsiDataSeries
        })
    }

    fun setData(data: TradeDataPoints) {
        rsiDataSeries.clear()

        rsiDataSeries.append(data.xValues, MovingAverage.rsi(data, 12))

        viewportManager.zoomExtentsX()
    }
}