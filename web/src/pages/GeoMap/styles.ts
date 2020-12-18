import { makeStyles } from '@material-ui/core'

export const useStyles = makeStyles(() => ({
  root: {
    marginRight: 400,
    marginLeft: 400,
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
    justifyContent: 'center',
    marginRight: 20
  },
  keplerBtnLink: {
    color: 'inherit',
    textDecoration: 'none'
  },
  keplerBtn: {
    display: 'flex',
    justifyContent: 'center'
  }
}))
