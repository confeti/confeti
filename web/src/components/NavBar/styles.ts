import { Theme, makeStyles } from '@material-ui/core'

export const useStyles = makeStyles((theme: Theme) => ({
  menuButton: {
    marginRight: theme.spacing(2),
    width: 35,
    height: 35
  },
  titleBar: {
    fontSize: '24px',
    fontWeight: 'bold',
    flexGrow: 1
  },
  link: {
    color: 'inherit',
    textDecoration: 'none'
  }
}))
