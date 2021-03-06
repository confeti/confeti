import { makeStyles } from '@material-ui/core'
import { colors } from 'theme'

export const useStyles = makeStyles(() => ({
  emptyContent: {
    display: 'flex',
    flexDirection: 'column',
    justifyContent: 'center',
    alignItems: 'center',
    height: '100%'
  },
  image: {
    width: '172px',
    height: '230px',
    fill: colors.secondaryLight
  },
  info: {
    fontSize: '30px',
    fontWeight: 500,
    marginBottom: '28px'
  }
}))
