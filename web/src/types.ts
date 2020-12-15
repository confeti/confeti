import { Years } from './types'
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

export type Years = Record<string, number> | Record<string, Record<string, number>>

export interface ISpeakerCompany {
  addedDate: string
  name: string
  year: number
}

export interface IcContactInfo {
  company: ISpeakerCompany
  email: string
  location: string
  twitterUsername: string
}
export interface ISpeaker {
  id: string
  name: string
  avatar: string
  bio: string
  contactInfo: IcContactInfo
}

export interface ISpeakerStatByYears {
  id: string
  years: Years
}

export interface ISpeakerStatByConferences {
  id: string
  conferences: Years
}

export interface IConference {
  name: string
  year: number
  location: string
  logo: string
  url: string
}

export interface IConferenceStat {
  conferenceName: string
  years: Years
}

export interface ICompany {
  name: string
  logo?: string
}

export interface ICompanyStat {
  companyName: string
  years: Years
}

export interface IReportComplexity {
  value: number
  description: string
}

export interface IReportSource {
  presentation: string
  repo: string
  video: string
  talk: string
  article: string
}

export interface IReport {
  id: string
  title: string
  complexity: IReportComplexity
  description: string
  language: string
  source: IReportSource
  conferences: IConference[]
  speakers: ISpeaker[]
  tags: string[]
}

export interface IReportStat {
  conferenceName: string
  years: Years
}
