import React, { useEffect, useState } from 'react'
import Autocomplete from '@material-ui/lab/Autocomplete'
import { getNotifier } from 'App'
import { LoadingWrapper } from 'components/LoadingWrapper'
import { LoadingType, BackdropType } from 'components/LoadingWrapper/LoadingWrapper'
import { Formik, Form, Field } from 'formik'
import { useSnackbar } from 'notistack'
import { getConferences } from 'store/conference/services'
import { getDataForMapOfSpeakers } from 'store/location/services'
import { GoogleMap, useLoadScript, Marker, InfoWindow } from '@react-google-maps/api'
import { Wrapper } from 'store/wrapper'
import { TextField, Box, Button, Avatar } from '@material-ui/core'
import { LocationOnRounded } from '@material-ui/icons'
import { IConference } from 'types'
import { useStyles } from './styles'

interface GeoMapProps {}

const GeoMap = () => {
  const classes = useStyles()
  const snackbarContext = useSnackbar()
  const [conferencesOptions, setConferencesOptions] = useState<Wrapper<IConference[]>>({
    isFetching: false,
    value: []
  })
  const [yearsOptions, setYearsOptions] = useState<string[]>([])
  const { isLoaded } = useLoadScript({
    googleMapsApiKey: 'AIzaSyA7O1NRZ-sAjk5qWeIjaF3z-iEmSdXa-B0'
  })
  const [markers, setMarkers] = useState<Wrapper<any[]>>({ isFetching: false, value: [] })
  const [selected, setSelected] = useState(null)

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
    setMarkers({ ...markers, isFetching: true })
    const data = await getDataForMapOfSpeakers(conference.name, year)
    setMarkers({
      isFetching: false,
      value: data.map(d => ({ ...d, lat: d.latitude, lng: d.longitude }))
    })
  }

  return (
    <Box className={classes.root}>
      <LoadingWrapper
        type={LoadingType.LINEAR}
        deps={[conferencesOptions, markers]}
        backdrop={BackdropType.GLOBAL}
      >
        <Box className={classes.tools}>
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
                      Apply
                    </Button>
                  </Box>
                </Box>
              </Form>
            )}
          </Formik>
        </Box>
        {isLoaded && (
          <GoogleMap
            mapContainerStyle={{ width: '100%', height: '100%', marginBottom: '20px' }}
            center={{
              lat: 59.9375,
              lng: 30.308611
            }}
            zoom={5}
          >
            {markers.value.map(marker => (
              <Marker
                key={marker.id}
                position={{ lat: marker.lat, lng: marker.lng }}
                onClick={() => {
                  setSelected(marker)
                }}
                icon={{
                  url: marker.avatar,
                  origin: new window.google.maps.Point(0, 0),
                  anchor: new window.google.maps.Point(15, 15),
                  scaledSize: new window.google.maps.Size(30, 30)
                }}
              />
            ))}
            {selected ? (
              <InfoWindow
                position={{ lat: selected.lat, lng: selected.lng }}
                onCloseClick={() => {
                  setSelected(null)
                }}
              >
                <Box style={{ display: 'flex', flexDirection: 'row' }}>
                  <Avatar src={selected.avatar} style={{ marginRight: 10 }} />
                  <Box style={{ display: 'flex', flexDirection: 'column' }}>
                    <span style={{ color: 'black' }}>{selected.name}</span>
                    <Box style={{ display: 'flex', flexDirection: 'row', alignItems: 'center' }}>
                      <LocationOnRounded style={{ color: 'black' }} />
                      <span style={{ color: 'black' }}>{selected.location}</span>
                    </Box>
                  </Box>
                </Box>
              </InfoWindow>
            ) : null}
          </GoogleMap>
        )}
      </LoadingWrapper>
    </Box>
  )
}

export default GeoMap
