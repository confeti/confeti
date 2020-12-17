import { makeStyles, Theme } from '@material-ui/core'

export const useStyles = makeStyles((theme: Theme) => ({
  root: {
    position: 'relative',
    height: '100%',
    width: '100%'
  },
  progress: {
    position: 'absolute',
    top: '50%',
    left: '50%',
    zIndex: theme.zIndex.drawer + 2
  },
  backdrop: {
    color: theme.palette.type === 'light' ? '#fff' : '#000'
  },
  backdropZindex: {
    zIndex: theme.zIndex.drawer + 1
  },
  backdropLocal: {
    position: 'absolute'
  }
}))
