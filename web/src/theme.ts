import { ThemeOptions } from '@material-ui/core'
import { IColors } from './types'

export const colors = {
  // bgLight: '#fff6da',
  // bgDark: '#262525',
  // primaryLight: '#fc6b3f',
  // primaryDark: '#262525',
  // secondaryLight: '#84f2d6',
  // secondaryDark: '#84f2d6'
  bgLight: '#f9f9f9',
  bgDark: '#262525',
  primaryLight: '#113a5d',
  primaryDark: '#062743',
  secondaryLight: '#ff7a8a',
  secondaryDark: '#ff7a8a',
  textLight: '#333333',
  textDark: '#8d9cab'
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
