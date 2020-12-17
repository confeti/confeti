import { ICompany, ICompanyStat } from 'types'
import { API_URL, request } from 'utils'

const COMPANY_URI = `${API_URL}/api/rest/company`

export const getCompanies = async (): Promise<ICompany[]> => {
  return request(COMPANY_URI).then(data => data as ICompany[])
}

export const getCompaniesStat = async (): Promise<ICompanyStat[]> => {
  return request(`${COMPANY_URI}/stat`).then(data => data as ICompanyStat[])
}

export const getCompanyStat = async (companyName: string): Promise<ICompanyStat> => {
  return request(`${COMPANY_URI}/${companyName}/stat`).then(data => data as ICompanyStat)
}

export const getCompanyStatByYear = async (
  companyName: string,
  year: number
): Promise<ICompanyStat> => {
  return request(`${COMPANY_URI}/${companyName}/stat?year=${year}`).then(
    data => data as ICompanyStat
  )
}
