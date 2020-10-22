import { History } from 'history'

export const API_URL = process.env.REACT_APP_API_URL

export const historyPush = (history: History, pathname: string) => {
  history.push(pathname, {
    from: history.location.pathname
  })
}
