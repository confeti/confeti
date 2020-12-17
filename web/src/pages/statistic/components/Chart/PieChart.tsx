import React from 'react'
import { IPieChart, IPieChartData } from 'types'
import { PieSvgProps, ResponsivePie } from '@nivo/pie'

interface PieChartProps
  extends Omit<
    Pick<
      PieSvgProps<IPieChartData>,
      | 'legends'
      | 'fill'
      | 'id'
      | 'value'
      | 'data'
      | 'onClick'
      | 'onMouseMove'
      | 'onMouseEnter'
      | 'onMouseLeave'
      | 'borderWidth'
      | 'isInteractive'
      | 'tooltip'
      | 'borderColor'
      | 'defs'
      | 'valueFormat'
      | 'colors'
      | 'margin'
      | 'sortByValue'
      | 'innerRadius'
      | 'padAngle'
      | 'cornerRadius'
      | 'startAngle'
      | 'endAngle'
      | 'fit'
      | 'theme'
      | 'enableRadialLabels'
      | 'radialLabel'
      | 'radialLabelsSkipAngle'
      | 'radialLabelsTextXOffset'
      | 'radialLabelsTextColor'
      | 'radialLabelsLinkOffset'
      | 'radialLabelsLinkDiagonalLength'
      | 'radialLabelsLinkHorizontalLength'
      | 'radialLabelsLinkStrokeWidth'
      | 'radialLabelsLinkColor'
      | 'enableSliceLabels'
      | 'sliceLabel'
      | 'sliceLabelsRadiusOffset'
      | 'sliceLabelsSkipAngle'
      | 'sliceLabelsTextColor'
      | 'role'
      | 'layers'
    >,
    'data'
  > {
  chartData: IPieChart
}

const PieChart = ({ chartData, ...props }: PieChartProps) => {
  return (
    <ResponsivePie
      data={chartData.data}
      margin={{ top: 40, right: 80, bottom: 80, left: 80 }}
      innerRadius={0.5}
      padAngle={0.7}
      cornerRadius={3}
      colors={{ scheme: 'set3' }}
      borderWidth={1}
      borderColor={{ from: 'color', modifiers: [['darker', 0.2]] }}
      radialLabelsSkipAngle={10}
      radialLabelsTextColor={props.theme.textColor ? props.theme.textColor : '#333333'}
      radialLabelsLinkColor={{ from: 'color' }}
      sliceLabelsSkipAngle={10}
      sliceLabelsTextColor="#333333"
      legends={[
        {
          anchor: 'bottom',
          direction: 'row',
          justify: false,
          translateX: 0,
          translateY: 56,
          itemsSpacing: 0,
          itemWidth: 100,
          itemHeight: 18,
          itemTextColor: props.theme.textColor ? props.theme.textColor : '#999',
          itemDirection: 'left-to-right',
          itemOpacity: 1,
          symbolSize: 18,
          symbolShape: 'circle'
        }
      ]}
      {...props}
    />
  )
}

export default PieChart
