import { Box, Button, Chip, TextField } from '@material-ui/core'
import Autocomplete from '@material-ui/lab/Autocomplete/Autocomplete'
import { getNotifier } from 'App'
import { LoadingWrapper } from 'components/LoadingWrapper'
import { BackdropType, LoadingType } from 'components/LoadingWrapper/LoadingWrapper'
import { Field, Form, Formik } from 'formik'
import { useSnackbar } from 'notistack'
import React, { useEffect, useState } from 'react'
import { getConferences } from 'store/conference/services'
import { getReportStatForConfAndYearByLanguage } from 'store/report/services'
import { Wrapper } from 'store/wrapper'
import { ChartType, IChart, IConference, IPieChartData, IReportStat } from 'types'
import { StatisticBox } from '../StatisticBox'
import { useStyles } from './styles'

interface LanguageStatisticsProps {
  defaultChartType?: ChartType
}

const LanguageStatistics = ({ defaultChartType }: LanguageStatisticsProps) => {
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

  const handleSubmit = (
    conferences: IConference[],
    year: number,
    chartType: ChartType,
    setChartData: React.Dispatch<React.SetStateAction<IChart>>,
    setChartType: React.Dispatch<React.SetStateAction<ChartType>>
  ) => {
    if (conferences.length === 1) {
      if (chartType !== ChartType.PIE) {
        setChartData(undefined)
        setChartType(ChartType.PIE)
      }
      getReportStatForConfAndYearByLanguage(conferences[0].name, year).then(stat => {
        Object.entries(stat.years).forEach(value => {
          const [, languages] = value
          setChartData({
            data: Object.entries(languages)
              .sort((v1, v2) => {
                const [, count1] = v1
                const [, count2] = v2
                return (count2 as number) - (count1 as number)
              })
              .slice(0, Math.min(Object.keys(languages).length, 5))
              .map(v => {
                const [lang, count] = v
                return { id: lang, label: lang, value: count } as IPieChartData
              })
          })
        })
      })
    } else if (conferences.length > 1) {
      if (chartType !== ChartType.BAR) {
        setChartData(undefined)
        setChartType(ChartType.BAR)
      }
      const promises = [] as Promise<IReportStat>[]
      conferences.forEach(conf => {
        promises.push(getReportStatForConfAndYearByLanguage(conf.name, year))
      })
      Promise.all(promises).then(stats => {
        const data = stats.map(stat => {
          let obj = { conference: stat.conferenceName }
          Object.entries(stat.years).forEach(value => {
            const [, languages] = value
            const topLang = Object.entries(languages)
              .sort((v1, v2) => {
                const [, count1] = v1
                const [, count2] = v2
                return (count2 as number) - (count1 as number)
              })
              .slice(0, Math.min(Object.keys(languages).length, 5))
              .reduce((r, [k, v]) => ({ ...r, [k]: v }), {})
            obj = { ...obj, ...topLang }
          })
          return obj
        })
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
                conferences: [],
                year: undefined
              }}
              onSubmit={({ conferences, year }) =>
                handleSubmit(conferences, year, chartType, setChartData, setChartType)
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
                        handleSubmit(
                          values.conferences,
                          values.year,
                          chartType,
                          setChartData,
                          setChartType
                        )
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
