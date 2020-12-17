import { IReportStat } from 'types'
import { API_URL, request } from 'utils'

const REPORT_URI = `${API_URL}/api/rest/report`

const getReportStatForConfBy = async (conferenceName: string, by: string): Promise<IReportStat> => {
  return request(`${REPORT_URI}/stat/${by}?conference_name=${conferenceName}`).then(
    data => data as IReportStat
  )
}

const getReportStatForConfAndYearBy = async (
  conferenceName: string,
  year: number,
  by: string
): Promise<IReportStat> => {
  return request(`${REPORT_URI}/stat/${by}?conference_name=${conferenceName}&year=${year}`).then(
    data => data as IReportStat
  )
}

export const getReportStatByTags = async (year?: number): Promise<IReportStat[]> => {
  return request(`${REPORT_URI}/stat/tag${year ? `?year=${year}` : ''}`).then(
    data => data as IReportStat[]
  )
}

export const getReportStatForConfByTags = async (conferenceName: string): Promise<IReportStat> =>
  getReportStatForConfBy(conferenceName, 'tag')

export const getReportStatForConfAndYearByTags = async (
  conferenceName: string,
  year: number
): Promise<IReportStat> => getReportStatForConfAndYearBy(conferenceName, year, 'tag')

export const getReportStatByLanguages = async (year?: number): Promise<IReportStat[]> => {
  return request(`${REPORT_URI}/stat/language${year ? `?year=${year}` : ''}`).then(
    data => data as IReportStat[]
  )
}

export const getReportStatForConfByLanguages = async (
  conferenceName: string
): Promise<IReportStat> => getReportStatForConfBy(conferenceName, 'language')

export const getReportStatForConfAndYearByLanguage = async (
  conferenceName: string,
  year: number
): Promise<IReportStat> => getReportStatForConfAndYearBy(conferenceName, year, 'language')
