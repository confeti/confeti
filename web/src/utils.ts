import { History } from 'history'

export const API_URL = process.env.REACT_APP_API_URL

export const historyPush = (history: History, pathname: string) => {
  history.push(pathname, {
    from: history.location.pathname
  })
}

const checkStatus = async (res: Response, handleResponse?: (res: Response) => void) => {
  if (handleResponse) {
    handleResponse(res.clone())
  }
  if (res.ok) {
    return res.text()
  }
  throw Error(res.statusText)
}

const parseJSON = (text: string) => {
  return text ? JSON.parse(text) : {}
}

export const request = async (
  url: string,
  options: RequestInit = {},
  handleResponse?: (res: Response) => void
) => {
  return fetch(url, {
    ...options
  })
    .then(res => checkStatus(res, handleResponse))
    .then(parseJSON)
}
