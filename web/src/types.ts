import { ComponentType } from 'react'

export interface IColors {
  bgLight: string
  bgDark: string
  primaryLight: string
  primaryDark: string
  secondaryLight: string
  secondaryDark: string
}

export type ThemeMode = 'dark' | 'light'

export interface IRouteInfo {
  path: string
  name: string
  component: ComponentType<any>
  exact?: boolean
}

export interface ISettings {
  themeMode: ThemeMode
}
