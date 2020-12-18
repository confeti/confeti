import { TextField, Button } from '@material-ui/core'
import Box from '@material-ui/core/Box'
import Autocomplete from '@material-ui/lab/Autocomplete'
import { getNotifier } from 'App'
import { LoadingWrapper } from 'components/LoadingWrapper'
import { LoadingType, BackdropType } from 'components/LoadingWrapper/LoadingWrapper'
import { Formik, Form, Field } from 'formik'
import { useSnackbar } from 'notistack'
import React, { useEffect, useState } from 'react'
import { getConferences } from 'store/conference/services'
import { getDataForMapOfSpeakers } from 'store/location/services'
import { Wrapper } from 'store/wrapper'
import { IConference } from 'types'
import { saveAsJson } from 'utils'
import { useStyles } from './styles'

interface GeoMapProps {}

const GeoMap = () => {
  const classes = useStyles()
  const snackbarContext = useSnackbar()
  const [conferencesOptions, setConferencesOptions] = useState<Wrapper<IConference[]>>({
    isFetching: false,
    value: []
  })
  const [isFetching, setFetching] = useState<Wrapper<any>>({
    isFetching: false,
    value: []
  })

  const [yearsOptions, setYearsOptions] = useState<string[]>([])

  const getAllConferences = async () => {
    setConferencesOptions({ ...conferencesOptions, isFetching: true })
    try {
      const response = await getConferences()
      setConferencesOptions({ isFetching: false, value: response })
    } catch (err) {
      setConferencesOptions({ ...conferencesOptions, isFetching: false })
      getNotifier('error', snackbarContext)(err.message)
    }
  }

  useEffect(() => {
    getAllConferences()
    // eslint-disable-next-line
  }, [])

  const handleSetYearsOptions = (conference: IConference) => {
    const years = conferencesOptions.value
      .filter(conf => conf.name === conference.name)
      .map(conf => conf.year)
    setYearsOptions(years.map(String))
  }

  const handleSubmit = async (conference: IConference, year: number) => {
    setFetching({ isFetching: true })
    const data = await getDataForMapOfSpeakers(conference.name, year)
    saveAsJson(data, `${conference.name}_${year}`)
    setFetching({ isFetching: false })
    getNotifier('info', snackbarContext)('Use this json in kepler.gl')
  }

  return (
    <Box className={classes.root}>
      <LoadingWrapper
        type={LoadingType.LINEAR}
        deps={[conferencesOptions, isFetching]}
        backdrop={BackdropType.GLOBAL}
      >
        <Box>
          <Formik
            initialValues={{
              conference: null,
              year: null
            }}
            onSubmit={({ conference, year }) => handleSubmit(conference, year)}
          >
            {({ values, setFieldValue }) => (
              <Form>
                <Box className={classes.formField}>
                  <Field
                    name="conference"
                    component={Autocomplete}
                    id="conference-select"
                    options={conferencesOptions.value.filter(
                      (conf, i, arr) => arr.findIndex(c => c.name === conf.name) === i
                    )}
                    getOptionLabel={(option: IConference) => option.name}
                    filterSelectedOptions
                    disableCloseOnSelect
                    onChange={(_, value: IConference) => {
                      if (value !== null && value !== undefined) {
                        setFieldValue('conference', value)
                        handleSetYearsOptions(value)
                      }
                    }}
                    renderInput={params => (
                      <TextField
                        {...params}
                        variant="outlined"
                        label="Conference"
                        color="secondary"
                        placeholder="Choose conference"
                      />
                    )}
                  />
                </Box>
                <Box className={classes.formField}>
                  <Field
                    name="year"
                    component={Autocomplete}
                    id="year-select"
                    key={yearsOptions}
                    disabled={yearsOptions.length === 0}
                    options={yearsOptions}
                    getOptionLabel={(option: number) => option}
                    filterSelectedOptions
                    onChange={(_, value: number) => setFieldValue('year', value)}
                    renderInput={params => (
                      <TextField
                        {...params}
                        variant="outlined"
                        label="Year"
                        value={values.year}
                        color="secondary"
                        placeholder="Choose year"
                      />
                    )}
                  />
                </Box>
                <Box className={classes.btns}>
                  <Box className={classes.applyBtn}>
                    <Button
                      color="secondary"
                      variant="contained"
                      disabled={values.conference === null || values.year === null}
                      onClick={() => handleSubmit(values.conference, values.year)}
                    >
                      Generate
                    </Button>
                  </Box>
                  <Box className={classes.keplerBtn}>
                    <a
                      href="https://kepler.gl/demo"
                      target="_blank"
                      rel="noopener noreferrer"
                      className={classes.keplerBtnLink}
                    >
                      <Button color="secondary" variant="contained">
                        kepler.gl
                      </Button>
                    </a>
                  </Box>
                </Box>
              </Form>
            )}
          </Formik>
        </Box>
      </LoadingWrapper>
    </Box>
  )
}

export default GeoMap
