import { useTheme } from '@material-ui/core'
import { ResponsiveBar } from '@nivo/bar'
import React from 'react'
import { colors } from 'theme'

interface HomeProps {}

const data = [
  {
    country: 'AD',
    'hot dog': 119,
    burger: 148,
    sandwich: 71,
    kebab: 21,
    fries: 17,
    donut: 53
  },
  {
    country: 'AE',
    'hot dog': 191,
    burger: 87,
    sandwich: 125,
    kebab: 143,
    fries: 89,
    donut: 1
  },
  {
    country: 'AF',
    'hot dog': 15,
    burger: 114,
    sandwich: 74,
    kebab: 181,
    fries: 20,
    donut: 35
  },
  {
    country: 'AG',
    'hot dog': 156,
    burger: 49,
    sandwich: 87,
    kebab: 17,
    fries: 115,
    donut: 156
  },
  {
    country: 'AI',
    'hot dog': 185,
    burger: 172,
    sandwich: 56,
    kebab: 158,
    fries: 57,
    donut: 9
  },
  {
    country: 'AL',
    'hot dog': 148,
    burger: 182,
    sandwich: 27,
    kebab: 147,
    fries: 14,
    donut: 97
  },
  {
    country: 'AM',
    'hot dog': 197,
    burger: 114,
    sandwich: 32,
    kebab: 161,
    fries: 130,
    donut: 133
  }
]

const Home: React.FC = () => {
  const theme = useTheme()
  return (
    <div style={{ height: '100%', width: '100%' }}>
      <ResponsiveBar
        theme={{
          textColor: theme.palette.type === 'dark' ? colors.textDark : colors.textLight,
          tooltip: {
            container: {
              background: theme.palette.type === 'dark' ? '#000000' : '#ffffff'
            }
          }
        }}
        data={data}
        keys={['hot dog', 'burger', 'sandwich', 'kebab', 'fries', 'donut']}
        indexBy="country"
        margin={{ top: 50, right: 130, bottom: 50, left: 60 }}
        padding={0.3}
        valueScale={{ type: 'linear' }}
        indexScale={{ type: 'band', round: true }}
        colors={{ scheme: 'set3' }}
        borderColor={{ from: 'color', modifiers: [['darker', 1.6]] }}
        axisTop={null}
        axisRight={null}
        axisBottom={{
          tickSize: 5,
          tickPadding: 5,
          tickRotation: 0,
          legend: 'country',
          legendPosition: 'middle',
          legendOffset: 32
        }}
        axisLeft={{
          tickSize: 5,
          tickPadding: 5,
          tickRotation: 0,
          legend: 'food',
          legendPosition: 'middle',
          legendOffset: -40
        }}
        labelSkipWidth={12}
        labelSkipHeight={12}
        labelTextColor={{ from: 'color', modifiers: [['darker', 1.6]] }}
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
      />
    </div>
  )
}

export default Home
