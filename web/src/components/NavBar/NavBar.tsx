import React from 'react'
import AppBar from '@material-ui/core/AppBar'
import Toolbar from '@material-ui/core/Toolbar'
import Typography from '@material-ui/core/Typography'
import { Link } from 'react-router-dom'
import { ReactComponent as Logo } from 'images/logo.svg'
import { LinearProgress } from '@material-ui/core'
import { AppState } from 'store'
import { connect, ConnectedProps } from 'react-redux'
import { IconsBar } from './IconsBar'
import { useStyles } from './styles'

interface NavBarProps extends PropsFromRedux {
  setTheme: () => void
}

const NavBar = ({ setTheme, isTopLinearProgressShowing }: NavBarProps) => {
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
        {isTopLinearProgressShowing && <LinearProgress color="secondary" />}
      </AppBar>
    </div>
  )
}

const mapStateToProps = (state: AppState) => ({
  isTopLinearProgressShowing: state.settings.settings.isTopLinearProgressShowing
})

const mapDispatchToProps = () => ({})

const connector = connect(mapStateToProps, mapDispatchToProps)

type PropsFromRedux = ConnectedProps<typeof connector>

export default connector(NavBar)
