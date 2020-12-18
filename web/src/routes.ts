import { GeoMap } from 'pages/GeoMap'
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
  },
  {
    path: '/map',
    name: 'Map',
    component: GeoMap
  }
]
