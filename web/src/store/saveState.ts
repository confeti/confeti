import { save, load, RLSOptions } from 'redux-localstorage-simple'

const options = { states: ['settings'], namespace: 'confeti' } as RLSOptions

export const saveState = () => save(options)
export const loadState = () => load(options)
