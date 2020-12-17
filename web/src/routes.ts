import { Home } from 'pages/Home'
import { Statistic } from 'pages/statistic'
import { IRouteInfo } from 'types'

export const routes: IRouteInfo[] = [
  {
    path: '/home',
    name: 'Home',
    component: Home
  },
  {
    path: '/statistic',
    name: 'Statistic',
    component: Statistic
  }
]
