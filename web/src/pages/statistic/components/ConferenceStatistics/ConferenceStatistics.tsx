import { Box, Button, Chip, TextField } from '@material-ui/core'
import Autocomplete from '@material-ui/lab/Autocomplete/Autocomplete'
import { getNotifier } from 'App'
import { LoadingWrapper } from 'components/LoadingWrapper'
import { BackdropType, LoadingType } from 'components/LoadingWrapper/LoadingWrapper'
import { Field, Form, Formik } from 'formik'
import { useSnackbar } from 'notistack'
import React, { useEffect, useState } from 'react'
import { getConferences, getConferenceStat } from 'store/conference/services'
import { Wrapper } from 'store/wrapper'
import { ChartType, IChart, IConference, IConferenceStat, ILineData } from 'types'
import { StatisticBox } from '../StatisticBox'
import { useStyles } from './styles'

interface ConferenceStatisticsProps {
  defaultChartType?: ChartType
}

const ConferenceStatistics = ({ defaultChartType }: ConferenceStatisticsProps) => {
  const classes = useStyles()
  const snackbarContext = useSnackbar()
  const [conferencesOptions, setConferencesOptions] = useState<Wrapper<IConference[]>>({
    isFetching: false,
    value: []
  })

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

  const handleSubmit = (
    conferences: IConference[],
    chartType: ChartType,
    setChartData: React.Dispatch<React.SetStateAction<IChart>>,
    setChartType: React.Dispatch<React.SetStateAction<ChartType>>
  ) => {
    if (conferences.length === 1) {
      if (chartType !== ChartType.LINE) {
        setChartData(undefined)
        setChartType(ChartType.LINE)
      }
      getConferenceStat(conferences[0].name).then(stat => {
        setChartData({
          legendX: 'years',
          data: [
            {
              id: conferences[0].name,
              data: Object.entries(stat.years).map(v => {
                const [y, count] = v
                return { x: y, y: count } as ILineData
              })
            }
          ]
        })
      })
    } else if (conferences.length > 1) {
      if (chartType !== ChartType.BAR) {
        setChartData(undefined)
        setChartType(ChartType.BAR)
      }
      const promises = [] as Promise<IConferenceStat>[]
      conferences.forEach(conf => {
        promises.push(getConferenceStat(conf.name))
      })

      Promise.all(promises).then(stats => {
        const data = stats.map(stat => ({ conference: stat.conferenceName, ...stat.years }))
        setChartData({ indexBy: 'conference', legendY: 'count', data })
      })
    }
  }

  return (
    <LoadingWrapper
      type={LoadingType.LINEAR}
      deps={[conferencesOptions]}
      backdrop={BackdropType.GLOBAL}
    >
      <StatisticBox defaultChartType={defaultChartType}>
        {({ chartType, setChartData, setChartType }) => (
          <Box>
            <Formik
              initialValues={{
                conferences: []
              }}
              onSubmit={({ conferences }) =>
                handleSubmit(conferences, chartType, setChartData, setChartType)
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
                      onChange={(_, value: IConference[]) => setFieldValue('conferences', value)}
                      getOptionDisabled={() => values.conferences.length >= 5}
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
                          label="Conferences"
                          color="secondary"
                          placeholder="Choose up to 5 conferences"
                        />
                      )}
                    />
                  </Box>

                  <Box className={classes.applyBtn}>
                    <Button
                      color="secondary"
                      variant="contained"
                      disabled={values.conferences.length === 0}
                      onClick={() =>
                        handleSubmit(values.conferences, chartType, setChartData, setChartType)
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

export default ConferenceStatistics
