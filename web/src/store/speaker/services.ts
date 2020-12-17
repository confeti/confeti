import { ISpeaker, ISpeakerStatByYears, ISpeakerStatByConferences } from 'types'
import { API_URL, request } from 'utils'

const SPEAKER_URI = `${API_URL}/api/rest/speaker`

export const getSpeakers = async (): Promise<ISpeaker[]> => {
  return request(SPEAKER_URI).then(data => data as ISpeaker[])
}

export const getSpeakersStatByYears = async (): Promise<ISpeakerStatByYears[]> => {
  return request(`${SPEAKER_URI}/stat/year`).then(data => data as ISpeakerStatByYears[])
}

export const getSpeakersStatByConferences = async (): Promise<ISpeakerStatByConferences[]> => {
  return request(`${SPEAKER_URI}/stat/conference`).then(data => data as ISpeakerStatByConferences[])
}

export const getSpeakerStatByYears = async (id: string): Promise<ISpeakerStatByYears> => {
  return request(`${SPEAKER_URI}/${id}/stat/year`).then(data => data as ISpeakerStatByYears)
}

export const getSpeakerStatByYear = async (
  id: string,
  year: number
): Promise<ISpeakerStatByYears> => {
  return request(`${SPEAKER_URI}/${id}/stat/?year=${year}`).then(
    data => data as ISpeakerStatByYears
  )
}

export const getSpeakerStatByConferences = async (
  id: string
): Promise<ISpeakerStatByConferences> => {
  return request(`${SPEAKER_URI}/${id}/stat/conference`).then(
    data => data as ISpeakerStatByConferences
  )
}

export const getSpeakerStatByConference = async (
  id: string,
  conferenceName: string
): Promise<ISpeakerStatByConferences> => {
  return request(`${SPEAKER_URI}/${id}/stat?conference_name=${conferenceName}`).then(
    data => data as ISpeakerStatByConferences
  )
}
