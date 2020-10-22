import React from 'react'
import AppBar from '@material-ui/core/AppBar'
import Toolbar from '@material-ui/core/Toolbar'
import Typography from '@material-ui/core/Typography'
import { Link } from 'react-router-dom'
import { ReactComponent as Logo } from 'images/logo.svg'
import { IconsBar } from './IconsBar'
import { useStyles } from './styles'

interface NavBarProps {
  setTheme: () => void
}

const NavBar: React.FC<NavBarProps> = ({ setTheme }: NavBarProps) => {
  const classes = useStyles()

  return (
    <div>
      <AppBar color="default">
        <Toolbar>
          <Link to="/" className={classes.link}>
            <Logo className={classes.menuButton} />
          </Link>
          <Typography className={classes.titleBar} color="inherit">
            <Link to="/" className={classes.link}>
              Confeti
            </Link>
          </Typography>
          <IconsBar setTheme={setTheme} />
        </Toolbar>
      </AppBar>
    </div>
  )
}

export default NavBar
