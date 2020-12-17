import { Box, Button, Chip, TextField } from '@material-ui/core'
import Autocomplete from '@material-ui/lab/Autocomplete/Autocomplete'
import { getNotifier } from 'App'
import { LoadingWrapper } from 'components/LoadingWrapper'
import { BackdropType, LoadingType } from 'components/LoadingWrapper/LoadingWrapper'
import { Field, Form, Formik } from 'formik'
import { useSnackbar } from 'notistack'
import React, { useEffect, useState } from 'react'
import { getConferences } from 'store/conference/services'
import { getReportStatForConfAndYearByTags } from 'store/report/services'
import { Wrapper } from 'store/wrapper'
import { ChartType, IChart, IConference, IPieChartData } from 'types'
import { StatisticBox } from '../StatisticBox'
import { useStyles } from './styles'

interface TagStatisticsProps {
  defaultChartType?: ChartType
}

const TagStatistics = ({ defaultChartType }: TagStatisticsProps) => {
  const classes = useStyles()
  const snackbarContext = useSnackbar()
  const [conferencesOptions, setConferencesOptions] = useState<Wrapper<IConference[]>>({
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
    if (conference) {
      const years = conferencesOptions.value
        .filter(conf => conf.name === conference.name)
        .map(conf => conf.year)
      setYearsOptions(years.map(String))
    } else {
      setYearsOptions([])
    }
  }

  const handleSubmit = (
    conference: IConference,
    year: number,
    setChartData: React.Dispatch<React.SetStateAction<IChart>>,
    setChartType: React.Dispatch<React.SetStateAction<ChartType>>
  ) => {
    setChartType(ChartType.PIE)
    getReportStatForConfAndYearByTags(conference.name, year).then(stat => {
      Object.entries(stat.years).forEach(value => {
        const [, tags] = value
        setChartData({
          data: Object.entries(tags)
            .sort((v1, v2) => {
              const [, count1] = v1
              const [, count2] = v2
              return (count2 as number) - (count1 as number)
            })
            .slice(0, 5)
            .map(v => {
              const [lang, count] = v
              return { id: lang, label: lang, value: count } as IPieChartData
            })
        })
      })
    })
  }

  return (
    <LoadingWrapper
      type={LoadingType.LINEAR}
      deps={[conferencesOptions]}
      backdrop={BackdropType.GLOBAL}
    >
      <StatisticBox defaultChartType={defaultChartType}>
        {({ setChartData, setChartType }) => (
          <Box>
            <Formik
              initialValues={{
                conference: undefined,
                year: undefined
              }}
              onSubmit={({ conference, year }) =>
                handleSubmit(conference, year, setChartData, setChartType)
              }
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
                      onChange={(_, value: IConference, reason) => {
                        setFieldValue('conference', value)
                        handleSetYearsOptions(value)
                        // Todo: it is better to get years when field is closed
                        if (reason === 'remove-option' || reason === 'clear') {
                          setFieldValue('year', '')
                        }
                      }}
                      renderTags={(value: IConference[], getTagProps: any) =>
                        value.map((option, index) => (
                          <Chip
                            className={classes.chip}
                            key={option.name}
                            // avatar={<Avatar alt={option.name} src={option.logo} />}
                            label={`${option.name}`}
                            color="secondary"
                            {...getTagProps({ index })}
                          />
                        ))
                      }
                      // renderOption={(option: IConference) => (
                      //   <div className={classes.option}>
                      //     <Avatar
                      //       className={classes.avatarOption}
                      //       alt={option.name}
                      //       src={option.logo}
                      //     />
                      //     <span className={classes.optionText}>{option.name}</span>
                      //   </div>
                      // )}
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
                  <Box className={classes.applyBtn}>
                    <Button
                      color="secondary"
                      variant="contained"
                      disabled={!values.conference || !values.year}
                      onClick={() =>
                        handleSubmit(values.conference, values.year, setChartData, setChartType)
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

export default TagStatistics
