import { ISpeakerLocation, IPositionStack } from 'types'
import { request, POSITION_STACK_API_ACCESS_KEY } from 'utils'
import { getSpeakersByConference } from '../speaker/services'

const POSITION_STACK_URI = 'http://api.positionstack.com/v1/'

export const getDataForMapOfSpeakers = async (
  conferenceName: string,
  year: number
): Promise<ISpeakerLocation[]> => {
  const speakers = await getSpeakersByConference(conferenceName, year)
  console.log(speakers)
  const promises = speakers
    .filter(speaker => speaker.contactInfo.location !== undefined)
    .map(async speaker => {
      const location = (await request(
        `${POSITION_STACK_URI}/forward?access_key=${POSITION_STACK_API_ACCESS_KEY}&query=${speaker.contactInfo.location}`
      )) as IPositionStack
      return {
        location: speaker.contactInfo.location,
        name: speaker.name,
        longitude: location.data[0].longitude,
        latitude: location.data[0].latitude
      } as ISpeakerLocation
    })
  return Promise.all(promises)
}
