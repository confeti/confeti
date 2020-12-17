import React, { useEffect, useState } from 'react'
import { useSnackbar } from 'notistack'
import { getNotifier } from 'App'
import { Wrapper } from 'store/wrapper'
import clsx from 'clsx'
import { Backdrop, Box, CircularProgress } from '@material-ui/core'
import { AppDispatch, AppState } from 'store'
import { bindActionCreators } from 'redux'
import { changeSettingsAction } from 'store/settings/actions'
import { connect, ConnectedProps } from 'react-redux'
import { useStyles } from './styles'

export enum LoadingType {
  CIRCULAR,
  LINEAR
}

export enum BackdropType {
  GLOBAL,
  LOCAL
}

interface LoadingWrapperProps extends PropsFromRedux {
  type: LoadingType
  deps: Wrapper<any>[]
  showError?: boolean
  backdrop?: BackdropType
  children?: React.ReactNode
  className?: string
}

const LoadingWrapper = ({
  type,
  deps,
  showError,
  settings,
  changeSettings,
  backdrop,
  className,
  children
}: LoadingWrapperProps) => {
  const classes = useStyles()
  const snackbarContext = useSnackbar()
  const [isFetching, setFetching] = useState(false)
  const [errors, setErrors] = useState<string[]>()

  useEffect(() => {
    const fetching = deps.some(v => v.isFetching)
    if (type === LoadingType.LINEAR && settings.isTopLinearProgressShowing !== fetching) {
      changeSettings({
        ...settings,
        isTopLinearProgressShowing: fetching
      })
    } else {
      setFetching(fetching)
    }
    setErrors(deps.map(v => v.error).filter(e => e !== undefined))
    // eslint-disable-next-line
  }, [deps])

  if (showError && errors.length > 0) {
    errors.forEach(err => {
      getNotifier('error', snackbarContext)(err)
    })
    return <>{children}</>
  }

  return (
    <Box className={clsx(className, classes.root)}>
      {children}
      <Backdrop
        className={clsx(classes.backdrop, {
          [classes.backdropZindex]: backdrop !== undefined,
          [classes.backdropLocal]: backdrop === BackdropType.LOCAL
        })}
        invisible={backdrop === undefined}
        open={isFetching}
      >
        {type === LoadingType.CIRCULAR && <CircularProgress color="secondary" />}
      </Backdrop>
    </Box>
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

export default connector(LoadingWrapper)
