import { combineReducers } from 'redux'
import { settingsReducer } from './settings/reducers'

export const rootReducer = combineReducers({
  settings: settingsReducer
})
