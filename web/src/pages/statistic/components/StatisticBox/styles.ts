import { makeStyles, Theme } from '@material-ui/core'

export const useStyles = makeStyles((theme: Theme) => ({
  tools: {
    minHeight: 550,
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    padding: '40px 40px',
    [theme.breakpoints.down('sm')]: {
      padding: 0,
      minHeight: 0,
      marginBottom: 70
    }
  },
  chart: {
    position: 'fixed',
    top: 256,
    right: 0,
    '--innerWidth': 'calc(100% - 80px)',
    width: 'calc(var(--innerWidth) * 0.60)',
    '--innerHeight': 'calc(100% - 60px)',
    height: 'calc(var(--innerHeight) * 0.6)',
    zIndex: 10,
    overflow: 'hidden',
    [theme.breakpoints.down('sm')]: {
      position: 'relative',
      top: 'auto',
      right: 'auto',
      width: 'auto',
      height: 460,
      zIndex: 0
    }
  }
}))
