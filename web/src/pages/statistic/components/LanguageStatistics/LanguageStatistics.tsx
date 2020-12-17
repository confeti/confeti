import { Avatar, Box, Button, Chip, TextField } from '@material-ui/core'
import Autocomplete from '@material-ui/lab/Autocomplete/Autocomplete'
import { getNotifier } from 'App'
import { LoadingWrapper } from 'components/LoadingWrapper'
import { BackdropType, LoadingType } from 'components/LoadingWrapper/LoadingWrapper'
import { Field, Form, Formik } from 'formik'
import { useSnackbar } from 'notistack'
import React, { useEffect, useState } from 'react'
import { getConferences } from 'store/conference/services'
import { Wrapper } from 'store/wrapper'
import { ChartType, IConference } from 'types'
import { StatisticBox } from '../StatisticBox'
import { useStyles } from './styles'

interface LanguageStatisticsProps {
  chartType?: ChartType
}

const LanguageStatistics = ({ chartType }: LanguageStatisticsProps) => {
  const classes = useStyles()
  const snackbarContext = useSnackbar()
  const [conferencesOptions, setConferencesOptions] = useState<Wrapper<IConference[]>>({
    isFetching: true,
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

  const handleSetYearsOptions = (conferences: IConference[]) => {
    const years =
      conferences.length === 1
        ? conferencesOptions.value
            .filter(conf => conf.name === conferences[0].name)
            .map(conf => conf.year)
        : conferencesOptions.value
            .filter(conf => conferences.findIndex(c => c.name === conf.name) !== -1)
            .map((conf: IConference) => conf.year)
            .filter(
              (year: number, i: number, arr: number[]) =>
                arr.indexOf(year) === i && arr.lastIndexOf(year) !== i
            )
    setYearsOptions(years.map(String))
  }

  // Todo: process data to create chart
  const handleSubmit = (
    conferences: IConference[],
    year: number,
    setChartData: any,
    setChartType: any
  ) => {
    console.log(conferences, year)
  }

  return (
    <LoadingWrapper
      type={LoadingType.LINEAR}
      deps={[conferencesOptions]}
      backdrop={BackdropType.GLOBAL}
    >
      <StatisticBox defaultChartType={chartType}>
        {({ setChartData, setChartType }) => (
          <Box>
            <Formik
              initialValues={{
                conferences: [],
                year: undefined
              }}
              onSubmit={({ conferences, year }) =>
                handleSubmit(conferences, year, setChartData, setChartType)
              }
            >
              {({ values, setFieldValue }) => (
                <Form>
                  <Box className={classes.formField}>
                    <Field
                      name="conferences"
                      component={Autocomplete}
                      multiple
                      limitTags={5}
                      id="conferences-select"
                      options={conferencesOptions.value.filter(
                        (conf, i, arr) => arr.findIndex(c => c.name === conf.name) === i
                      )}
                      getOptionLabel={(option: IConference) => option.name}
                      filterSelectedOptions
                      disableCloseOnSelect
                      onChange={(_, value: IConference[], reason) => {
                        setFieldValue('conferences', value)

                        // Todo: it is better to get years when field is closed
                        if (reason === 'remove-option' || reason === 'clear') {
                          handleSetYearsOptions(value)
                          setFieldValue('year', '')
                        }
                      }}
                      getOptionDisabled={() => values.conferences.length >= 5}
                      onClose={() => handleSetYearsOptions(values.conferences)}
                      renderTags={(value: IConference[], getTagProps: any) =>
                        value.map((option, index) => (
                          <Chip
                            className={classes.chip}
                            key={option.name}
                            avatar={<Avatar alt={option.name} src={option.logo} />}
                            label={`${option.name}`}
                            color="secondary"
                            {...getTagProps({ index })}
                          />
                        ))
                      }
                      renderOption={(option: IConference) => (
                        <div className={classes.option}>
                          <Avatar
                            className={classes.avatarOption}
                            alt={option.name}
                            src={option.logo}
                          />
                          <span className={classes.optionText}>{option.name}</span>
                        </div>
                      )}
                      renderInput={params => (
                        <TextField
                          {...params}
                          variant="outlined"
                          label="Conferences"
                          color="secondary"
                          placeholder="Choose at least 5 conferences"
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
                  <Box className={classes.applyBtn}>
                    <Button
                      color="secondary"
                      variant="contained"
                      disabled={values.conferences.length === 0 || !values.year}
                      onClick={() =>
                        handleSubmit(values.conferences, values.year, setChartData, setChartType)
                      }
                    >
                      Apply
                    </Button>
                  </Box>
                </Form>
              )}
            </Formik>
          </Box>
        )}
      </StatisticBox>
    </LoadingWrapper>
  )
}

export default LanguageStatistics
