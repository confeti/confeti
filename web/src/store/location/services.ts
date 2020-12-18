import { ISpeakerLocation, IPositionStack } from 'types'
import { request } from 'utils'
import { getSpeakersByConference } from '../speaker/services'

const POSITION_STACK_URI = 'http://api.positionstack.com/v1'

export const getDataForMapOfSpeakers = async (
  conferenceName: string,
  year: number
): Promise<ISpeakerLocation[]> => {
  const speakers = await getSpeakersByConference(conferenceName, year)
  const promises = speakers
    .filter(speaker => speaker.contactInfo !== null && speaker.contactInfo.location !== null)
    .map(async speaker => {
      try {
        const location = (await request(
          `${POSITION_STACK_URI}/forward?access_key=c7f02f899ea945e43c0420b690255e29&query=${speaker.contactInfo.location}`
        )) as IPositionStack
        let loc = location.data[0]
        if (location.data.length !== 1) {
          loc = location.data.find(l => l.name === speaker.contactInfo.location)
        }
        loc = loc !== undefined ? loc : location.data[0]
        return {
          location: speaker.contactInfo.location,
          name: speaker.name,
          id: speaker.id,
          avatar: speaker.avatar,
          longitude: loc.longitude,
          latitude: loc.latitude
        } as ISpeakerLocation
      } catch (err) {
        return undefined
      }
    })
  return (await Promise.all(promises))
    .filter(el => el !== undefined && el.longitude !== undefined && el.latitude !== undefined)
    .sort((el1, el2) => Object.keys(el2).length - Object.keys(el1).length)
}
