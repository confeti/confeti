import React, { useState } from 'react'
import { Box } from '@material-ui/core'
import { ChartType, IChart } from 'types'
import { useStyles } from './styles'
import { Chart } from '../Chart'

interface StatisticBoxProps {
  defaultChartType?: ChartType
  children?(
    data:
      | {
          chartData: IChart
          chartType: ChartType
          setChartData: React.Dispatch<React.SetStateAction<IChart>>
          setChartType: React.Dispatch<React.SetStateAction<ChartType>>
        }
      | undefined
  ): React.ReactNode
}

const StatisticBox = ({ defaultChartType, children }: StatisticBoxProps) => {
  const classes = useStyles()
  const [chartData, setChartData] = useState<IChart>()
  const [chartType, setChartType] = useState<ChartType>(
    defaultChartType !== undefined ? defaultChartType : ChartType.BAR
  )

  const childrenToolsData = { chartData, chartType, setChartData, setChartType }

  return (
    <Box>
      <Box className={classes.tools}>{children(childrenToolsData)}</Box>
      <Box className={classes.chart}>
        <Chart chartData={chartData} chartType={chartType} />
      </Box>
    </Box>
  )
}

export default StatisticBox
