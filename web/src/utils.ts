import { History } from 'history'

export const API_URL = process.env.REACT_APP_API_URL
export const { POSITION_STACK_API_ACCESS_KEY } = process.env

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

export const saveAsJson = (obj: any, name: string) => {
  const contentType = 'application/json;charset=utf-8;'
  if (window.navigator && window.navigator.msSaveOrOpenBlob) {
    const blob = new Blob([decodeURIComponent(encodeURI(JSON.stringify(obj)))], {
      type: contentType
    })
    navigator.msSaveOrOpenBlob(blob, name)
  } else {
    const a = document.createElement('a')
    a.download = name
    a.href = `data:${contentType},${encodeURIComponent(JSON.stringify(obj))}`
    a.target = '_blank'
    document.body.appendChild(a)
    a.click()
    document.body.removeChild(a)
  }
}
