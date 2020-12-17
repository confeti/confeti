import { Avatar, Box, Button, Chip, TextField } from '@material-ui/core'
import Autocomplete from '@material-ui/lab/Autocomplete/Autocomplete'
import { getNotifier } from 'App'
import { LoadingWrapper } from 'components/LoadingWrapper'
import { BackdropType, LoadingType } from 'components/LoadingWrapper/LoadingWrapper'
import { Field, Form, Formik } from 'formik'
import { useSnackbar } from 'notistack'
import React, { useEffect, useState } from 'react'
import {
  getSpeakers,
  getSpeakersStatByConferences,
  getSpeakersStatByYears,
  getSpeakerStatByYears
} from 'store/speaker/services'
import { Wrapper } from 'store/wrapper'
import {
  ChartType,
  IChart,
  ICompany,
  IConference,
  ILineData,
  IPieChartData,
  ISpeaker,
  ISpeakerStatByConferences,
  ISpeakerStatByYears
} from 'types'
import { StatisticBox } from '../StatisticBox'
import { useStyles } from './styles'

interface SpeakerStatisticsProps {
  defaultChartType?: ChartType
}

const SpeakerStatistics = ({ defaultChartType }: SpeakerStatisticsProps) => {
  const classes = useStyles()
  const snackbarContext = useSnackbar()
  const [speakersOptions, setSpeakersOptions] = useState<Wrapper<ISpeaker[]>>({
    isFetching: false,
    value: []
  })
  const [speakersStatByYears, setSpeakerStatByYears] = useState<Wrapper<ISpeakerStatByYears[]>>({
    isFetching: false,
    value: []
  })
  const [speakersStatByConfs, setSpeakerStatByConfs] = useState<
    Wrapper<ISpeakerStatByConferences[]>
  >({
    isFetching: false,
    value: []
  })
  const [yearsOptions, setYearsOptions] = useState<string[]>([])
  const [conferencesOptions, setConferencesOptions] = useState<string[]>([])

  const getAllSpeakers = async () => {
    setSpeakersOptions({ ...speakersOptions, isFetching: true })
    try {
      const speakerResponse = await getSpeakers()
      setSpeakersOptions({ isFetching: false, value: speakerResponse })
      const speakerStatYearsResponse = await getSpeakersStatByYears()
      setSpeakerStatByYears({ isFetching: false, value: speakerStatYearsResponse })
      setYearsOptions(
        speakerStatYearsResponse
          .map(stat => Object.entries(stat.years).map(value => value[0]))
          .flat(1)
          .filter(
            (year: string, i: number, arr: string[]) =>
              arr.indexOf(year) === i && arr.lastIndexOf(year) !== i
          )
      )
      const speakerStatConfsResponse = await getSpeakersStatByConferences()
      setSpeakerStatByConfs({ isFetching: false, value: speakerStatConfsResponse })
      setConferencesOptions(
        speakerStatConfsResponse
          .map(stat => Object.entries(stat.conferences).map(value => value[0]))
          .flat(1)
          .filter(
            (conf: string, i: number, arr: string[]) =>
              arr.indexOf(conf) === i && arr.lastIndexOf(conf) !== i
          )
      )
    } catch (err) {
      setSpeakersOptions({ ...speakersOptions, isFetching: false })
      setSpeakerStatByYears({ ...speakersStatByYears, isFetching: false })
      setSpeakerStatByConfs({ ...speakersStatByConfs, isFetching: false })
      getNotifier('error', snackbarContext)(err.message)
    }
  }

  useEffect(() => {
    getAllSpeakers()
    // eslint-disable-next-line
  }, [])

  const handleSubmitByYears = (
    speakers: ISpeaker[],
    chartType: ChartType,
    setChartData: React.Dispatch<React.SetStateAction<IChart>>,
    setChartType: React.Dispatch<React.SetStateAction<ChartType>>
  ) => {
    if (speakers.length === 1) {
      if (chartType !== ChartType.LINE) {
        setChartData(undefined)
        setChartType(ChartType.LINE)
      }
      getSpeakerStatByYears(speakers[0].id).then(stat => {
        setChartData({
          legendX: 'years',
          data: [
            {
              id: speakers[0].name,
              data: Object.entries(stat.years).map(v => {
                const [y, count] = v
                return { x: y, y: count } as ILineData
              })
            }
          ]
        })
      })
    } else if (speakers.length > 1) {
      if (chartType !== ChartType.BAR) {
        setChartData(undefined)
        setChartType(ChartType.BAR)
      }
      const promises = [] as Promise<ISpeakerStatByYears>[]
      speakers.forEach(speaker => {
        promises.push(getSpeakerStatByYears(speaker.id))
      })

      Promise.all(promises).then(stats => {
        const data = stats.map(stat => ({
          speaker: speakersOptions.value.find(sp => sp.id === stat.id).name,
          ...stat.years
        }))
        setChartData({ indexBy: 'speaker', legendY: 'count', data })
      })
    }
  }

  const handleTopSpeakersByYear = (
    year: number,
    chartType: ChartType,
    setChartData: React.Dispatch<React.SetStateAction<IChart>>,
    setChartType: React.Dispatch<React.SetStateAction<ChartType>>
  ) => {
    if (chartType !== ChartType.PIE) {
      setChartData(undefined)
      setChartType(ChartType.PIE)
    }
    const topSpeakersStat = speakersStatByYears.value
      .filter(stat => stat.years[year.toString()] !== undefined)
      .sort(
        (s1, s2) => (s2.years[year.toString()] as number) - (s1.years[year.toString()] as number)
      )
      .slice(0, 5)
      .map(stat => {
        const speakerName = speakersOptions.value.find(sp => sp.id === stat.id).name
        return {
          id: speakerName,
          label: speakerName,
          value: stat.years[year.toString()]
        } as IPieChartData
      })
    setChartData({ data: topSpeakersStat })
  }

  const handleTopSpeakersByConf = (
    conf: string,
    chartType: ChartType,
    setChartData: React.Dispatch<React.SetStateAction<IChart>>,
    setChartType: React.Dispatch<React.SetStateAction<ChartType>>
  ) => {
    if (chartType !== ChartType.PIE) {
      setChartData(undefined)
      setChartType(ChartType.PIE)
    }
    const topSpeakersStat = speakersStatByConfs.value
      .filter(stat => stat.conferences[conf] !== undefined)
      .sort((s1, s2) => (s2.conferences[conf] as number) - (s1.conferences[conf] as number))
      .slice(0, 5)
      .map(stat => {
        const speakerName = speakersOptions.value.find(sp => sp.id === stat.id).name
        return {
          id: speakerName,
          label: speakerName,
          value: stat.conferences[conf]
        } as IPieChartData
      })
    setChartData({ data: topSpeakersStat })
  }

  return (
    <LoadingWrapper
      type={LoadingType.LINEAR}
      deps={[speakersOptions, speakersStatByYears]}
      backdrop={BackdropType.GLOBAL}
    >
      <StatisticBox defaultChartType={defaultChartType}>
        {({ chartType, setChartData, setChartType }) => (
          <Box>
            <Formik
              initialValues={{
                speakers: [],
                year: null,
                conference: null
              }}
              onSubmit={({ speakers }) =>
                handleSubmitByYears(speakers, chartType, setChartData, setChartType)
              }
            >
              {({ values, setFieldValue }) => (
                <Form>
                  <Box className={classes.formField}>
                    <Field
                      name="speakers"
                      component={Autocomplete}
                      multiple
                      limitTags={5}
                      id="speakers-select"
                      options={speakersOptions.value}
                      disabled={values.year !== null || values.conference !== null}
                      getOptionLabel={(option: ICompany) => option.name}
                      filterSelectedOptions
                      disableCloseOnSelect
                      onChange={(_, value: ICompany[]) => setFieldValue('speakers', value)}
                      getOptionDisabled={() => values.speakers.length >= 5}
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
                      renderOption={(option: ISpeaker) => (
                        <div className={classes.option}>
                          <Avatar
                            className={classes.avatarOption}
                            alt={option.name}
                            src={option.avatar}
                          />
                          <span className={classes.optionText}>{option.name}</span>
                        </div>
                      )}
                      renderInput={params => (
                        <TextField
                          {...params}
                          variant="outlined"
                          label="Speakers"
                          color="secondary"
                          placeholder="Choose up to 5 speakers"
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
                      disabled={values.speakers.length > 0 || values.conference !== null}
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
                  <Box className={classes.formField}>
                    <Field
                      name="conference"
                      component={Autocomplete}
                      id="conference-select"
                      key={conferencesOptions}
                      disabled={values.speakers.length > 0 || values.year !== null}
                      options={conferencesOptions}
                      getOptionLabel={(option: string) => option}
                      filterSelectedOptions
                      onChange={(_, value: string) => setFieldValue('conference', value)}
                      renderInput={params => (
                        <TextField
                          {...params}
                          variant="outlined"
                          label="Conference"
                          value={values.conference}
                          color="secondary"
                          placeholder="Choose conference"
                        />
                      )}
                    />
                  </Box>

                  <Box className={classes.applyBtn}>
                    <Button
                      color="secondary"
                      variant="contained"
                      disabled={values.speakers.length === 0 && !values.year && !values.conference}
                      onClick={() => {
                        if (values.speakers.length > 0) {
                          handleSubmitByYears(
                            values.speakers,
                            chartType,
                            setChartData,
                            setChartType
                          )
                        } else if (values.year) {
                          handleTopSpeakersByYear(
                            values.year,
                            chartType,
                            setChartData,
                            setChartType
                          )
                        } else if (values.conference) {
                          handleTopSpeakersByConf(
                            values.conference,
                            chartType,
                            setChartData,
                            setChartType
                          )
                        }
                      }}
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

export default SpeakerStatistics
