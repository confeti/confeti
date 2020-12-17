import { makeStyles, Theme } from '@material-ui/core'

export const useStyles = makeStyles((theme: Theme) => ({
  tools: {
    marginRight: '60%',
    [theme.breakpoints.down('sm')]: {
      margin: 0
    }
  },
  statisticSelect: {
    marginBottom: 70
  },
  statisticTextField: {
    fontSize: 38,
    fontWeight: 500
  }
}))
