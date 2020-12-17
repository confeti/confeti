import { SetStatistic, ClearStatistic, SET_STATISTIC, CLEAR_STATISTIC } from './types'

const setStatisticData = (payload): SetStatistic => ({
  type: SET_STATISTIC,
  payload
})

const clearStatisticData = (): ClearStatistic => ({
  type: CLEAR_STATISTIC
})
