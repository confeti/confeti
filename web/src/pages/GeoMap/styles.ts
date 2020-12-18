import { makeStyles, Theme } from '@material-ui/core'

export const useStyles = makeStyles((theme: Theme) => ({
  root: {
    width: '100%',
    height: '100%',
    display: 'flex',
    justifyContent: 'center'
  },
  option: {
    display: 'flex',
    alignItems: 'center'
  },
  avatarOption: {
    width: 30,
    height: 30
  },
  optionText: {
    marginLeft: 10,
    fontSize: 16,
    fontWeight: 400
  },
  chip: {
    marginRight: 5
  },
  formField: {
    marginBottom: 40
  },
  btns: {
    display: 'flex',
    flexDirection: 'row',
    justifyContent: 'center'
  },
  applyBtn: {
    display: 'flex',
    justifyContent: 'center'
  },
  tools: {
    marginRight: 400,
    marginLeft: 400,
    marginBottom: 40,
    [theme.breakpoints.down('md')]: {
      marginRight: 0,
      marginLeft: 0
    }
  }
}))
