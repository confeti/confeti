import React from 'react'
import { Box, useTheme } from '@material-ui/core'
import { colors } from 'theme'
import { ChartType, IBarChart, IChart, ILineChart, IPieChart } from 'types'
import { BarChartOutlined } from '@material-ui/icons'
import { EmptyContent } from 'components/EmptyContent'
import BarChart from './BarChart'
import PieChart from './PieChart'
import { useStyles } from './styles'
import LineChart from './LineChart'

interface ChartProps {
  chartData: IChart
  chartType: ChartType
}

const Chart = ({ chartData, chartType }: ChartProps) => {
  const theme = useTheme()
  const classes = useStyles()

  const chartTheme = {
    textColor: theme.palette.type === 'dark' ? colors.textDark : colors.textLight,
    tooltip: {
      container: {
        background: theme.palette.type === 'dark' ? '#000000' : '#ffffff'
      }
    }
  }

  return (
    <Box className={classes.root}>
      {chartData && chartData.data.length > 0 ? (
        <>
          {chartType === ChartType.BAR && (
            <BarChart chartData={chartData as IBarChart} theme={chartTheme} />
          )}
          {chartType === ChartType.PIE && (
            <PieChart chartData={chartData as IPieChart} theme={chartTheme} />
          )}
          {chartType === ChartType.LINE && (
            <LineChart chartData={chartData as ILineChart} theme={chartTheme} />
          )}
        </>
      ) : (
        <EmptyContent Icon={BarChartOutlined} info="No chart" />
      )}
    </Box>
  )
}

export default Chart
