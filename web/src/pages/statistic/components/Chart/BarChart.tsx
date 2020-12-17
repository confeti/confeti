import React from 'react'
import { BarSvgProps, ResponsiveBar } from '@nivo/bar'
import { IBarChart } from 'types'

interface BarChartProps extends Omit<BarSvgProps, 'data'> {
  chartData: IBarChart
}

const BarChart = ({ chartData, ...props }: BarChartProps) => (
  <ResponsiveBar
    data={chartData.data}
    keys={Object.keys(chartData.data[0]).filter(key => key !== chartData.indexBy)}
    indexBy={chartData.indexBy}
    margin={{ top: 50, right: 130, bottom: 50, left: 60 }}
    padding={0.3}
    valueScale={{ type: 'linear' }}
    indexScale={{ type: 'band', round: true }}
    colors={{ scheme: 'set3' }}
    borderColor={{ from: 'color', modifiers: [['darker', 1.6]] }}
    axisTop={null}
    axisRight={null}
    groupMode="grouped"
    axisBottom={{
      tickSize: 5,
      tickPadding: 5,
      tickRotation: 0,
      legend: chartData.legendX !== undefined ? chartData.legendX : chartData.indexBy,
      legendPosition: 'middle',
      legendOffset: 32
    }}
    axisLeft={{
      tickSize: 5,
      tickPadding: 5,
      tickRotation: 0,
      legend: chartData.legendY !== undefined ? chartData.legendY : '',
      legendPosition: 'middle',
      legendOffset: -40
    }}
    labelSkipWidth={12}
    labelSkipHeight={12}
    labelTextColor={{ from: 'color', modifiers: [['darker', 3]] }}
    legends={[
      {
        dataFrom: 'keys',
        anchor: 'bottom-right',
        direction: 'column',
        justify: false,
        translateX: 120,
        translateY: 0,
        itemsSpacing: 2,
        itemWidth: 100,
        itemHeight: 20,
        itemDirection: 'left-to-right',
        itemOpacity: 0.85,
        symbolSize: 20,
        effects: [
          {
            on: 'hover',
            style: {
              itemOpacity: 1
            }
          }
        ]
      }
    ]}
    animate
    motionStiffness={90}
    motionDamping={15}
    {...props}
  />
)

export default BarChart
