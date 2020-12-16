import React from 'react'
import { useTheme } from '@material-ui/core'
import { Wrapper } from 'store/wrapper'
import { colors } from 'theme'
import { ChartType, IBarChart, IPieChart } from 'types'
import { BarChartOutlined } from '@material-ui/icons'
import LoadingWrapper, { BackdropType, LoadingType } from 'components/LoadingWrapper/LoadingWrapper'
import { EmptyContent } from 'components/EmptyContent'
import BarChart from './BarChart'
import PieChart from './PieChart'

interface ChartProps {
  chartData: Wrapper<IPieChart | IBarChart>
  chartType: ChartType
}

const Chart = ({ chartData, chartType }: ChartProps) => {
  const theme = useTheme()

  const chartTheme = {
    textColor: theme.palette.type === 'dark' ? colors.textDark : colors.textLight,
    tooltip: {
      container: {
        background: theme.palette.type === 'dark' ? '#000000' : '#ffffff'
      }
    }
  }

  return (
    <LoadingWrapper type={LoadingType.LINEAR} deps={[chartData]} backdrop={BackdropType.GLOBAL}>
      {chartData.value && chartData.value.data.length > 0 ? (
        <>
          {chartType === ChartType.BAR ? (
            <BarChart chartData={chartData.value as IBarChart} theme={chartTheme} />
          ) : (
            <PieChart chartData={chartData.value as IPieChart} theme={chartTheme} />
          )}
        </>
      ) : (
        <EmptyContent Icon={BarChartOutlined} info="No chart" />
      )}
    </LoadingWrapper>
  )
}

export default Chart
