import { Wrapper } from 'store/wrapper'
import { IBarChart, IPieChart } from 'types'

export const SET_STATISTIC = 'SET_STATISTIC'
export const CLEAR_STATISTIC = 'CLEAR_STATISTIC'

export interface StatisticState {
  chartData: Wrapper<IPieChart | IBarChart>
}

export interface SetStatistic {
  type: typeof SET_STATISTIC
  payload: Wrapper<IPieChart | IBarChart>
}

export interface ClearStatistic {
  type: typeof CLEAR_STATISTIC
}

export type StatisticActionsType = SetStatistic | ClearStatistic
