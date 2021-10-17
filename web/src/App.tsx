import React from 'react'
import { connect, ConnectedProps } from 'react-redux'
import { Redirect, Route, Switch } from 'react-router-dom'
import { bindActionCreators } from 'redux'
import { routes } from 'routes'
import { useTheme } from 'hooks'
import {
  createTheme,
  createStyles,
  makeStyles,
  Theme,
  ThemeProvider
} from '@material-ui/core/styles'
import { CssBaseline, IconButton } from '@material-ui/core'
import { ProviderContext, SnackbarProvider, VariantType } from 'notistack'
import { AppDispatch, AppState } from 'store'
import { CloseRounded } from '@material-ui/icons'
import { changeSettingsAction } from 'store/settings/actions'
import { NavBar } from 'components/NavBar'

const useStyles = makeStyles((theme: Theme) =>
  createStyles({
    content: {
      height: '100vh',
      padding: '128px 64px',
      [theme.breakpoints.down('xs')]: {
        padding: '128px 8px'
      }
    }
  })
)

export const getNotifier = (
  variant: VariantType,
  { enqueueSnackbar, closeSnackbar }: ProviderContext
) => {
  return (msg: string) =>
    enqueueSnackbar(msg, {
      variant,
      action: key => (
        <IconButton onClick={() => closeSnackbar(key)} aria-label="Close notification">
          <CloseRounded />
        </IconButton>
      )
    })
}

type AppProps = PropsFromRedux

const App = ({ settings, changeSettings }: AppProps) => {
  const classes = useStyles()
  const [theme, toggleTheme] = useTheme(settings.themeMode)
  const customTheme = createTheme(theme)

  const changeTheme = () => {
    changeSettings({ ...settings, themeMode: theme.palette!.type! === 'dark' ? 'light' : 'dark' })
    toggleTheme()
  }

  return (
    <div>
      <ThemeProvider theme={customTheme}>
        <SnackbarProvider
          maxSnack={3}
          anchorOrigin={{
            vertical: 'bottom',
            horizontal: 'right'
          }}
        >
          <CssBaseline />
          <NavBar setTheme={changeTheme} />
          <main className={classes.content}>
            <Switch>
              {routes.map(route => (
                <Route
                  key={route.path}
                  path={route.path}
                  component={route.component}
                  exact={route.exact}
                />
              ))}
              <Redirect to="/statistic" path="/" exact />
            </Switch>
          </main>
        </SnackbarProvider>
      </ThemeProvider>
    </div>
  )
}

const mapStateToProps = (state: AppState) => ({
  settings: state.settings.settings
})

const mapDispatchToProps = (dispatch: AppDispatch) => ({
  changeSettings: bindActionCreators(changeSettingsAction, dispatch)
})

const connector = connect(mapStateToProps, mapDispatchToProps)

type PropsFromRedux = ConnectedProps<typeof connector>

export default connector(App)
