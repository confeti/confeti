import React, { useState } from 'react'
import { IconButton, Tooltip, useTheme } from '@material-ui/core'
import {
  BarChartOutlined,
  Brightness4Rounded,
  Brightness7Rounded,
  LocationOnRounded
} from '@material-ui/icons'
import { historyPush } from 'utils'
import { useHistory } from 'react-router'

interface IconsBarProps {
  setTheme: () => void
}

const IconsBar: React.FC<IconsBarProps> = ({ setTheme }: IconsBarProps) => {
  const theme = useTheme()
  const [lightTheme, setLightTheme] = useState(theme.palette.type === 'light')
  const history = useHistory()

  const toggleTheme = () => {
    setLightTheme(!lightTheme)
    setTheme()
  }

  return (
    <div>
      <Tooltip title="Stat">
        <IconButton color="inherit" onClick={() => historyPush(history, '/statistic')}>
          <BarChartOutlined />
        </IconButton>
      </Tooltip>
      <Tooltip title="Geo map">
        <IconButton color="inherit" onClick={() => historyPush(history, '/map')}>
          <LocationOnRounded />
        </IconButton>
      </Tooltip>
      <Tooltip title="Toggle light/dark theme">
        <IconButton color="inherit" onClick={toggleTheme}>
          {(lightTheme && <Brightness4Rounded />) || <Brightness7Rounded />}
        </IconButton>
      </Tooltip>
    </div>
  )
}

export default IconsBar
