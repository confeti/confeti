import React, { useState } from 'react'
import { IconButton, Tooltip, useTheme } from '@material-ui/core'
import { Brightness4Rounded, Brightness7Rounded } from '@material-ui/icons'

interface IconsBarProps {
  setTheme: () => void
}

const IconsBar: React.FC<IconsBarProps> = ({ setTheme }: IconsBarProps) => {
  const theme = useTheme()
  const [lightTheme, setLightTheme] = useState(theme.palette.type === 'light')

  const toggleTheme = () => {
    setLightTheme(!lightTheme)
    setTheme()
  }

  return (
    <div>
      <Tooltip title="Toggle light/dark theme">
        <IconButton color="inherit" onClick={toggleTheme}>
          {(lightTheme && <Brightness4Rounded />) || <Brightness7Rounded />}
        </IconButton>
      </Tooltip>
    </div>
  )
}

export default IconsBar
