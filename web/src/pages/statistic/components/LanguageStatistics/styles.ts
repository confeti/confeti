import { makeStyles } from '@material-ui/core'

export const useStyles = makeStyles(() => ({
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
  applyBtn: {
    display: 'flex',
    justifyContent: 'center'
  }
}))
