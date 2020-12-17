import { IConference, IConferenceStat } from 'types'
import { API_URL, request } from 'utils'

const CONFERENCE_URI = `${API_URL}/api/rest/conference`

export const getConferences = async (): Promise<IConference[]> => {
  return request(CONFERENCE_URI).then(data => data as IConference[])
}

export const getConferencesStat = async (): Promise<IConferenceStat[]> => {
  return request(`${CONFERENCE_URI}/stat`).then(data => data as IConferenceStat[])
}

export const getConferenceStat = async (conferenceName: string): Promise<IConferenceStat> => {
  return request(`${CONFERENCE_URI}/${conferenceName}/stat`).then(data => data as IConferenceStat)
}

export const getConferenceStatByYear = async (
  conferenceName: string,
  year: number
): Promise<IConferenceStat> => {
  return request(`${CONFERENCE_URI}/${conferenceName}/stat?year=${year}`).then(
    data => data as IConferenceStat
  )
}
