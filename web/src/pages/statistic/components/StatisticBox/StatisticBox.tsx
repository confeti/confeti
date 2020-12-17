import React, { useState } from 'react'
import { Box } from '@material-ui/core'
import { Wrapper } from 'store/wrapper'
import { ChartType, IChart } from 'types'
import { useStyles } from './styles'
import { Chart } from '../Chart'

interface StatisticBoxProps {
  defaultChartType?: ChartType
  children?(
    data:
      | {
          setChartData: React.Dispatch<React.SetStateAction<Wrapper<IChart>>>
          setChartType: React.Dispatch<React.SetStateAction<ChartType>>
        }
      | undefined
  ): React.ReactNode
}

const StatisticBox = ({ defaultChartType, children }: StatisticBoxProps) => {
  const classes = useStyles()
  const [chartData, setChartData] = useState<Wrapper<IChart>>({ isFetching: false })
  const [chartType, setChartType] = useState<ChartType>(
    defaultChartType !== undefined ? defaultChartType : ChartType.BAR
  )

  const childrenToolsData = { setChartData, setChartType }

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
