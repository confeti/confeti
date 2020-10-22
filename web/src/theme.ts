import { ThemeOptions } from '@material-ui/core/styles/createMuiTheme'
import { IColors } from './types'

// TODO: choose colors
export const colors = {
  bgLight: '#FFFFFF',
  bgDark: '#000000',
  primaryLight: '#FFFFFF',
  primaryDark: '#000000',
  secondaryLight: '#FFFFFF',
  secondaryDark: '#000000'
} as IColors

const customTheme: ThemeOptions = {
  palette: {
    type: 'light',
    background: {
      default: colors.bgLight
    },
    primary: {
      dark: colors.primaryDark,
      light: colors.primaryLight,
      main: colors.primaryLight,
      contrastText: '#fff'
    },
    secondary: {
      dark: colors.secondaryLight,
      light: colors.secondaryLight,
      main: colors.secondaryLight,
      contrastText: '#fff'
    }
  }
}

export default customTheme
